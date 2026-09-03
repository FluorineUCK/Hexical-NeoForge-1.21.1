@file:JvmName("HexCodecCompat")

package miyucomics.hexical.hexcompat

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component

private fun requireCompound(tag: Tag?, description: String): CompoundTag =
	tag as? CompoundTag
		?: throw IllegalArgumentException("$description did not encode to a compound tag")

fun serializeIota(iota: Iota): CompoundTag = requireCompound(
	IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, iota).result().orElse(null),
	"Hex Casting iota ${iota.javaClass.name}"
)

fun deserializeIota(tag: Tag, level: ServerLevel? = null): Iota? {
	val iota = if (tag is CompoundTag && isLegacyIotaTag(tag)) {
		deserializeLegacyIota(tag, level)
	} else {
		IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null)
	} ?: return null
	if (level == null) return iota
	@Suppress("UNCHECKED_CAST")
	return if ((iota.type as IotaType<Iota>).validate(iota, level)) iota else null
}

fun deserializeIotaOrThrow(tag: Tag, level: ServerLevel? = null): Iota =
	requireNotNull(deserializeIota(tag, level)) { "Invalid Hex Casting iota payload" }

fun displayIota(tag: Tag): Component = deserializeIota(tag)?.display() ?: IotaType.brokenIota()

fun serializePattern(pattern: HexPattern): CompoundTag = requireCompound(
	HexPattern.CODEC.encodeStart(NbtOps.INSTANCE, pattern).result().orElse(null),
	"Hex Casting pattern"
)

fun deserializePattern(tag: Tag): HexPattern? =
	HexPattern.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null)
		?: deserializeLegacyPattern(tag)

fun serializePigment(pigment: FrozenPigment): CompoundTag = requireCompound(
	FrozenPigment.CODEC.encodeStart(NbtOps.INSTANCE, pigment).result().orElse(null),
	"Hex Casting pigment"
)

fun deserializePigment(tag: Tag): FrozenPigment? =
	FrozenPigment.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null)
		?: deserializeLegacyPigment(tag)
