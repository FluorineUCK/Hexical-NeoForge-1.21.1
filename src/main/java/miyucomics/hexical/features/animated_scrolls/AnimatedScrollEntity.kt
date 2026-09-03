package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.hexcompat.deserializePattern
import miyucomics.hexical.hexcompat.serializePattern
import miyucomics.hexical.inits.HexicalEntities
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.PatternUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.HangingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class AnimatedScrollEntity(entityType: EntityType<AnimatedScrollEntity>, level: Level) : HangingEntity(entityType, level), ADIotaHolder {
	var patterns: List<CompoundTag> = emptyList()
	var cachedVerts: List<Vec2> = emptyList()
	var scroll: ItemStack = ItemStack.EMPTY

	constructor(level: Level) : this(HexicalEntities.ANIMATED_SCROLL_ENTITY, level)

	constructor(level: Level, position: BlockPos, dir: Direction, size: Int, patterns: List<CompoundTag>, scroll: ItemStack) : this(level) {
		this.pos = position
		this.patterns = patterns.map(CompoundTag::copy)
		this.entityData.set(sizeDataTracker, size)
		this.scroll = scroll
		setDirection(dir)
		if (!level.isClientSide) updateRender()
	}

	override fun tick() {
		super.tick()
		if (!level().isClientSide && level().gameTime % 20L == 0L) updateRender()
	}

	public override fun setDirection(facing: Direction) {
		super.setDirection(facing)
	}

	override fun calculateBoundingBox(position: BlockPos, facing: Direction): AABB {
		val size = entityData.get(sizeDataTracker).toDouble()
		val offset = if (entityData.get(sizeDataTracker) % 2 == 0) 0.5 else 0.0
		val center = Vec3.atCenterOf(position)
			.relative(facing, -0.46875)
			.relative(facing.counterClockWise, offset)
			.relative(Direction.UP, offset)
		val widthX = if (facing.axis == Direction.Axis.X) 0.0625 else size
		val widthZ = if (facing.axis == Direction.Axis.Z) 0.0625 else size
		return AABB.ofSize(center, widthX, size, widthZ)
	}

	fun updateRender() {
		val shown = if (patterns.isNotEmpty()) {
			patterns[(level().gameTime / 20L).toInt().mod(patterns.size)].copy()
		} else {
			CompoundTag().also { it.putBoolean("empty", true) }
		}
		entityData.set(patternDataTracker, shown)
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		super.addAdditionalSaveData(nbt)
		nbt.putInt("direction", direction.get3DDataValue())
		nbt.putInt("state", entityData.get(stateDataTracker))
		nbt.putInt("color", entityData.get(colorDataTracker))
		nbt.putBoolean("glow", entityData.get(glowDataTracker))
		nbt.putInt("size", entityData.get(sizeDataTracker))
		nbt.put("scroll", scroll.saveOptional(registryAccess()))
		nbt.put("patterns", ListTag().also { out -> patterns.forEach { out.add(it.copy()) } })
	}

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		super.readAdditionalSaveData(nbt)
		direction = Direction.from3DDataValue(nbt.getInt("direction"))
		entityData.set(stateDataTracker, nbt.getInt("state"))
		entityData.set(glowDataTracker, nbt.getBoolean("glow"))
		entityData.set(colorDataTracker, nbt.getInt("color"))
		entityData.set(sizeDataTracker, nbt.getInt("size").coerceIn(1, 3))
		scroll = ItemStack.parseOptional(registryAccess(), nbt.getCompound("scroll"))
		patterns = nbt.getList("patterns", Tag.TAG_COMPOUND.toInt()).map { (it as CompoundTag).copy() }
		setDirection(direction)
		updateRender()
	}

	override fun playPlacementSound() = playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F)

	override fun dropItem(entity: Entity?) {
		playSound(SoundEvents.PAINTING_BREAK, 1.0f, 1.0f)
		if (level().gameRules.getBoolean(GameRules.RULE_DOENTITYDROPS) && !scroll.isEmpty)
			spawnAtLocation(scroll)
	}

	override fun getPickResult() = ItemStack(
		when (entityData.get(sizeDataTracker)) {
			1 -> HexicalItems.SMALL_ANIMATED_SCROLL_ITEM
			2 -> HexicalItems.MEDIUM_ANIMATED_SCROLL_ITEM
			3 -> HexicalItems.LARGE_ANIMATED_SCROLL_ITEM
			else -> HexicalItems.SMALL_ANIMATED_SCROLL_ITEM
		}
	)

	override fun trackingPosition(): Vec3 = Vec3.atLowerCornerOf(pos)
	override fun moveTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) = setPos(x, y, z)
	override fun lerpTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float, interpolationSteps: Int) = setPos(x, y, z)

	override fun getAddEntityPacket(serverEntity: ServerEntity): Packet<ClientGamePacketListener> =
		ClientboundAddEntityPacket(this, direction.get3DDataValue(), pos)

	override fun recreateFromPacket(packet: ClientboundAddEntityPacket) {
		super.recreateFromPacket(packet)
		setDirection(Direction.from3DDataValue(packet.data))
	}

	fun setState(state: Int) {
		ItemStackDataCompat.update(scroll) { it.putInt("state", state) }
		entityData.set(stateDataTracker, state)
	}

	fun setColor(color: Int) {
		ItemStackDataCompat.update(scroll) { it.putInt("color", color) }
		entityData.set(colorDataTracker, color)
	}

	fun toggleGlow() {
		val glowing = !entityData.get(glowDataTracker)
		entityData.set(glowDataTracker, glowing)
		ItemStackDataCompat.update(scroll) { it.putBoolean("glow", glowing) }
	}

	override fun defineSynchedData(builder: SynchedEntityData.Builder) {
		builder.define(colorDataTracker, 0xff_000000.toInt())
		builder.define(glowDataTracker, false)
		builder.define(stateDataTracker, 0)
		builder.define(sizeDataTracker, 1)
		builder.define(patternDataTracker, CompoundTag())
	}

	override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
		super.onSyncedDataUpdated(data)
		when (data) {
			sizeDataTracker -> recalculateBoundingBox()
			patternDataTracker -> {
				val nbt = entityData.get(patternDataTracker)
				val pattern = if (nbt.contains("empty")) null else deserializePattern(nbt)
				cachedVerts = if (pattern == null) emptyList() else PatternUtils.getNormalizedStrokes(pattern, true)
			}
		}
	}

	override fun readIota(): Iota = ListIota(patterns.mapNotNull(::deserializePattern).map(::PatternIota))

	override fun writeIota(iota: Iota?, simulate: Boolean): Boolean {
		val next = when {
			iota == null || iota is NullIota -> emptyList()
			iota.type == HexIotaTypes.PATTERN.get() -> listOf(serializePattern((iota as PatternIota).pattern))
			iota.type == HexIotaTypes.LIST.get() -> {
				val values = (iota as ListIota).list
				if (values.any { it.type != HexIotaTypes.PATTERN.get() }) return false
				values.map { serializePattern((it as PatternIota).pattern) }
			}
			else -> return false
		}
		if (!simulate) {
			patterns = next
			ItemStackDataCompat.update(scroll) { root ->
				if (next.isEmpty()) root.remove("patterns")
				else root.put("patterns", ListTag().also { list -> next.forEach { list.add(it.copy()) } })
			}
			updateRender()
		}
		return true
	}

	override fun writeable() = true

	companion object {
		private val patternDataTracker: EntityDataAccessor<CompoundTag> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.COMPOUND_TAG)
		val glowDataTracker: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.BOOLEAN)
		val colorDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
		val sizeDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
		val stateDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(AnimatedScrollEntity::class.java, EntityDataSerializers.INT)
	}
}
