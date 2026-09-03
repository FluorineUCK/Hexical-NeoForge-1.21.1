package miyucomics.hexical.hexcompat

import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.ContinuationIota
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.math.HexAngle
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.dyes.DyeOption
import miyucomics.hexical.features.pigments.PigmentIota
import miyucomics.hexpose.iotas.DisplayIota
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.ItemStackIota
import net.minecraft.Util
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.NumericTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.phys.Vec3

private const val LEGACY_TYPE = "hexcasting:type"
private const val LEGACY_DATA = "hexcasting:data"
private const val LEGACY_HEXPOSE_STACK_ID = "hexpose:stack_id"
private const val LEGACY_HEXPOSE_STACK_COUNT = "hexpose:stack_count"
private const val LEGACY_HEXPOSE_STACK_NBT = "hexpose:stack_tag"
private const val MAX_LEGACY_IOTA_DEPTH = 256
private const val MAX_LEGACY_IOTA_COUNT = 1024

private class LegacyReadLimitExceeded : RuntimeException()
private class LegacyReadBudget(var remaining: Int = MAX_LEGACY_IOTA_COUNT) {
	fun consume() {
		if (remaining <= 0)
			throw LegacyReadLimitExceeded()
		remaining--
	}
}

/** Reads the typed-Iota envelope used by Hex Casting 0.11.x. */
internal fun isLegacyIotaTag(tag: CompoundTag): Boolean =
	tag.contains(LEGACY_TYPE, Tag.TAG_STRING.toInt()) && tag.contains(LEGACY_DATA)

internal fun deserializeLegacyIota(tag: CompoundTag, level: ServerLevel?): Iota = try {
	deserializeLegacyIota(tag, level, 0, LegacyReadBudget())
} catch (_: LegacyReadLimitExceeded) {
	GarbageIota()
}

private fun deserializeLegacyIota(
	tag: CompoundTag,
	level: ServerLevel?,
	depth: Int,
	budget: LegacyReadBudget
): Iota {
	if (depth >= MAX_LEGACY_IOTA_DEPTH)
		throw LegacyReadLimitExceeded()
	budget.consume()

	val data = tag.get(LEGACY_DATA) ?: return GarbageIota()
	return try {
		when (tag.getString(LEGACY_TYPE)) {
			"hexcasting:null" -> NullIota()
			"hexcasting:garbage" -> GarbageIota()
			"hexcasting:double" -> DoubleIota((data as NumericTag).asDouble)
			"hexcasting:boolean" -> BooleanIota((data as NumericTag).asByte.toInt() != 0)
			"hexcasting:vec3" -> Vec3Iota(deserializeLegacyVec3(data) ?: return GarbageIota())
			"hexcasting:pattern" -> PatternIota(deserializeLegacyPattern(data) ?: return GarbageIota())
			"hexcasting:list" -> {
				val entries = data as? ListTag ?: return GarbageIota()
				if (entries.size > budget.remaining)
					throw LegacyReadLimitExceeded()
				ListIota(entries.map { child ->
					val childTag = child as? CompoundTag ?: return@map GarbageIota()
					deserializeLegacyIota(childTag, level, depth + 1, budget)
				})
			}
			"hexcasting:entity" -> {
				val entityData = data as? CompoundTag ?: return GarbageIota()
				val uuidTag = entityData.get("uuid") ?: return GarbageIota()
				// Legacy entity iotas did not encode the player bit. Keep the
				// current codec's conservative default so unknown UUIDs remain
				// protected by true-name checks until resolved.
				EntityIota(NbtUtils.loadUUID(uuidTag), null, true)
			}
			"hexcasting:continuation" -> SpellContinuation.CODEC
				.parse(NbtOps.INSTANCE, data)
				.result()
				.orElse(null)
				?.let(::ContinuationIota)
				?: GarbageIota()
			"hexical:dye" -> {
				val ordinal = (data as? NumericTag)?.asInt ?: return GarbageIota()
				DyeOption.entries.getOrNull(ordinal)?.let(::DyeIota) ?: GarbageIota()
			}
			"hexical:pigment" -> {
				val payload = data as? CompoundTag ?: return GarbageIota()
				deserializeLegacyPigment(payload.get("pigment") ?: payload)?.let(::PigmentIota)
					?: GarbageIota()
			}
			"hexpose:identifier" -> deserializeLegacyIdentifier(data) ?: GarbageIota()
			"hexpose:item_stack" -> deserializeLegacyItemStack(data) ?: GarbageIota()
			"hexpose:text" -> deserializeLegacyDisplay(data, level) ?: GarbageIota()
			else -> GarbageIota()
		}
	} catch (limit: LegacyReadLimitExceeded) {
		throw limit
	} catch (_: RuntimeException) {
		GarbageIota()
	}
}

private fun deserializeLegacyIdentifier(tag: Tag): IdentifierIota? = runCatching {
	val compound = tag as? CompoundTag ?: return null
	val namespace = compound.getString("namespace")
	val path = compound.getString("path")
	val identifier = ResourceLocation.tryParse("$namespace:$path") ?: return null
	IdentifierIota(identifier)
}.getOrNull()

private fun deserializeLegacyItemStack(tag: Tag): ItemStackIota? = runCatching {
	val compound = tag as? CompoundTag ?: return null
	val identifier = ResourceLocation.tryParse(compound.getString(LEGACY_HEXPOSE_STACK_ID)) ?: return null
	val item = BuiltInRegistries.ITEM.getOptional(identifier).orElse(null) ?: return null
	val count = compound.getInt(LEGACY_HEXPOSE_STACK_COUNT)
	val stack = if (count <= 0) ItemStack.EMPTY else ItemStack(item, count)
	(compound.get(LEGACY_HEXPOSE_STACK_NBT) as? CompoundTag)?.copy()?.let { customData ->
		sanitizeLegacyItemStackIotas(customData)
		if (!customData.isEmpty && !stack.isEmpty)
			stack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData))
	}
	ItemStackIota(stack)
}.getOrNull()

private fun deserializeLegacyDisplay(tag: Tag, level: ServerLevel?): DisplayIota? = runCatching {
	val compound = tag as? CompoundTag ?: return null
	if (!compound.contains("text", Tag.TAG_STRING.toInt())) return null
	val provider = level?.registryAccess() ?: RegistryAccess.EMPTY
	val component = Component.Serializer.fromJson(compound.getString("text"), provider) ?: return null
	DisplayIota(component)
}.getOrNull()

private fun sanitizeLegacyItemStackIotas(root: CompoundTag) {
	val queue = ArrayDeque<Tag>()
	queue.add(root)
	while (queue.isNotEmpty()) {
		when (val next = queue.removeFirst()) {
			is ListTag -> next.forEach(queue::addLast)
			is CompoundTag -> {
				if (next.contains(LEGACY_HEXPOSE_STACK_ID)) {
					next.remove(LEGACY_HEXPOSE_STACK_ID)
					next.remove(LEGACY_HEXPOSE_STACK_COUNT)
					next.remove(LEGACY_HEXPOSE_STACK_NBT)
				}
				next.allKeys.mapNotNullTo(queue) { next.get(it) }
			}
		}
	}
}

internal fun deserializeLegacyPattern(tag: Tag): HexPattern? = runCatching {
	val compound = tag as? CompoundTag ?: return null
	if (!compound.contains("start_dir", Tag.TAG_ANY_NUMERIC.toInt()) ||
		!compound.contains("angles", Tag.TAG_BYTE_ARRAY.toInt())) return null

	val start = HexDir.entries.getOrNull(compound.getByte("start_dir").toInt()) ?: return null
	val angles = (compound.get("angles") as ByteArrayTag).asByteArray.map { ordinal ->
		HexAngle.entries.getOrNull(ordinal.toInt()) ?: return null
	}.toMutableList()
	HexPattern(start, angles)
}.getOrNull()

internal fun deserializeLegacyPigment(tag: Tag): FrozenPigment? = runCatching {
	val pigment = tag as? CompoundTag ?: return null
	val stackTag = pigment.get("stack") as? CompoundTag ?: return null
	val id = ResourceLocation.tryParse(stackTag.getString("id")) ?: return null
	val item = BuiltInRegistries.ITEM.getOptional(id).orElse(null) ?: return null
	val count = when {
		stackTag.contains("count", Tag.TAG_ANY_NUMERIC.toInt()) -> stackTag.getInt("count")
		stackTag.contains("Count", Tag.TAG_ANY_NUMERIC.toInt()) -> stackTag.getInt("Count")
		else -> 1
	}.coerceAtLeast(1)
	val stack = ItemStack(item, count)

	// Hexical pigments were normally plain stacks. Preserve any legacy custom
	// payload rather than silently discarding it during the 1.21 component hop.
	(stackTag.get("tag") as? CompoundTag)?.takeUnless(CompoundTag::isEmpty)?.let {
		stack.set(DataComponents.CUSTOM_DATA, CustomData.of(it.copy()))
	}

	val owner = if (pigment.hasUUID("owner")) pigment.getUUID("owner") else Util.NIL_UUID
	FrozenPigment(stack, owner)
}.getOrNull()

private fun deserializeLegacyVec3(tag: Tag): Vec3? = when (tag) {
	is LongArrayTag -> tag.asLongArray.takeIf { it.size == 3 }?.let {
		Vec3(Double.fromBits(it[0]), Double.fromBits(it[1]), Double.fromBits(it[2]))
	}
	is CompoundTag -> if (tag.contains("x") && tag.contains("y") && tag.contains("z"))
		Vec3(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
	else null
	else -> null
}
