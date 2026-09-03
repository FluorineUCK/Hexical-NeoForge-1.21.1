package miyucomics.hexical.features.spike

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalDamageTypes
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.world.entity.*
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import java.util.*
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class)
class SpikeEntity(entityType: EntityType<SpikeEntity>, world: Level) : Entity(entityType, world) {
	private var timer = 0
	private var conjurerUUID: UUID? = null
	private var conjurer: Player? = null

	constructor(world: Level, x: Double, y: Double, z: Double, direction: Direction, delay: Int) : this(HexicalEntities.SPIKE_ENTITY, world) {
		this.setDirection(direction)
		this.setPos(x, y, z)
		this.timer = -delay
		this.entityData.set(timerDataTracker, this.timer)
	}

	override fun tick() {
		this.timer += 1
		if (this.timer == EMERGE_LENGTH) {
			this.playSound(SoundEvents.GLASS_BREAK, 1f, 1f)
			for (livingEntity in level().getEntitiesOfClass(LivingEntity::class.java, boundingBox))
				this.damage(livingEntity)
		}
		if (this.timer > EMERGE_LENGTH + STAY_LENGTH + DISAPPEAR_LENGTH && !level().isClientSide) {
			(level() as ServerLevel).sendParticles(ItemParticleOption(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK, 1)), this.x, this.y, this.z, 8, HexicalMain.RANDOM.nextGaussian() / 20f, HexicalMain.RANDOM.nextGaussian() / 20f, HexicalMain.RANDOM.nextGaussian() / 20f, HexicalMain.RANDOM.nextGaussian() / 10f)
			level().playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 0.25f, 1.5f)
			this.discard()
		}
	}

	private fun damage(target: LivingEntity) {
		val direction = Direction.from3DDataValue(this.entityData.get(directionDataTracker)).step()
		direction.mul(0.5f)
		target.push(direction.x.toDouble(), direction.y.toDouble() + 0.5f, direction.z.toDouble())
		if (!target.isAlive || target.isInvulnerable)
			return
		target.hurt(level().damageSources().source(HexicalDamageTypes.SPIKE, this, getConjurer()), 6f)
	}

	private fun setDirection(direction: Direction) {
		this.entityData.set(directionDataTracker, direction.get3DDataValue())
		if (direction.axis.isHorizontal) {
			this.xRot = 0.0f
			this.yRot = (direction.get2DDataValue() * 90).toFloat()
		} else {
			this.xRot = (-90 * direction.axisDirection.getStep()).toFloat()
			this.yRot = 0.0f
		}
		this.xRotO = this.xRot
		this.yRotO = this.yRot
	}

	fun getAnimationProgress(): Float {
		if (this.timer < 0)
			return 0f
		if (this.timer in 0..<EMERGE_LENGTH)
			return (this.timer / EMERGE_LENGTH.toFloat()).pow(5f)
		if (this.timer in EMERGE_LENGTH..<EMERGE_LENGTH + STAY_LENGTH)
			return 1f
		if (this.timer >= EMERGE_LENGTH + STAY_LENGTH)
			return 1f - (this.timer - EMERGE_LENGTH - STAY_LENGTH) / DISAPPEAR_LENGTH.toFloat()
		return 1f
	}

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		this.timer = nbt.getInt("timer")
		if (nbt.hasUUID("conjurer"))
			this.conjurerUUID = nbt.getUUID("conjurer")
		else {
			this.conjurer = null
			this.conjurerUUID = null
		}
		this.entityData.set(timerDataTracker, this.timer)
		this.setDirection(Direction.from3DDataValue(nbt.getInt("direction")))
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		nbt.putInt("timer", this.timer)
		this.conjurerUUID?.let { nbt.putUUID("conjurer", it) }
		nbt.putInt("direction", this.entityData.get(directionDataTracker))
	}

	fun setConjurer(player: Player) {
		this.conjurer = player
		this.conjurerUUID = player.uuid
	}

	private fun getConjurer(): Player? {
		if (this.conjurer != null)
			return this.conjurer
		if (this.conjurerUUID == null)
			return null
		this.conjurer = this.level().getPlayerByUUID(conjurerUUID ?: return null)
		if (this.conjurer != null)
			return conjurer
		return null
	}

	override fun getDirection(): Direction = Direction.from3DDataValue(this.entityData.get(directionDataTracker))

	override fun defineSynchedData(builder: SynchedEntityData.Builder) {
		builder.define(directionDataTracker, 0)
		builder.define(timerDataTracker, 0)
	}

	override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
		if (data == timerDataTracker)
			this.timer = this.entityData.get(timerDataTracker)
	}

	companion object {
		private const val EMERGE_LENGTH = 10
		private const val STAY_LENGTH = 10
		private const val DISAPPEAR_LENGTH = 20
		private val directionDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(SpikeEntity::class.java, EntityDataSerializers.INT)
		private val timerDataTracker: EntityDataAccessor<Int> = SynchedEntityData.defineId(SpikeEntity::class.java, EntityDataSerializers.INT)
	}
}
