package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.hexcompat.deserializePigment
import miyucomics.hexical.hexcompat.serializePigment
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level

abstract class BaseSpecklike(entityType: EntityType<out BaseSpecklike>, world: Level) : Entity(entityType, world), Specklike {
	private var lifespan = -1

	var clientPigment: FrozenPigment = FrozenPigment.DEFAULT.get()
	var clientSize = 1f
	var clientThickness = 1f
	var clientRoll = 0f

	open fun processState() {}

	override fun tick() {
		if (lifespan != -1)
			lifespan--
		if (lifespan == 0)
			discard()
		super.tick()
	}

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		entityData.set(pigmentDataTracker, nbt.getCompound("pigment"))
		entityData.set(rollDataTracker, nbt.getFloat("roll"))
		entityData.set(sizeDataTracker, nbt.getFloat("size"))
		entityData.set(thicknessDataTracker, nbt.getFloat("thickness"))
		this.lifespan = nbt.getInt("lifespan")
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		nbt.putCompound("pigment", entityData.get(pigmentDataTracker))
		nbt.putFloat("roll", entityData.get(rollDataTracker))
		nbt.putFloat("size", entityData.get(sizeDataTracker))
		nbt.putFloat("thickness", entityData.get(thicknessDataTracker))
		nbt.putInt("lifespan", lifespan)
	}

	override fun setLifespan(lifespan: Int) {
		this.lifespan = lifespan
	}

	override fun setSize(size: Float) = entityData.set(sizeDataTracker, size)
	override fun setRoll(rotation: Float) = entityData.set(rollDataTracker, rotation)
	override fun setThickness(thickness: Float) = entityData.set(thicknessDataTracker, thickness)
	override fun setPigment(pigment: FrozenPigment) = entityData.set(pigmentDataTracker, serializePigment(pigment))
	override fun defineSynchedData(builder: SynchedEntityData.Builder) {
		builder.define(stateDataTracker, CompoundTag())
		builder.define(pigmentDataTracker, CompoundTag())
		builder.define(rollDataTracker, 0f)
		builder.define(sizeDataTracker, 1f)
		builder.define(thicknessDataTracker, 1f)
	}

	override fun onSyncedDataUpdated(data: EntityDataAccessor<*>) {
		when (data) {
			stateDataTracker -> processState()
			pigmentDataTracker -> this.clientPigment = deserializePigment(entityData.get(pigmentDataTracker)) ?: FrozenPigment.DEFAULT.get()
			sizeDataTracker -> this.clientSize = entityData.get(sizeDataTracker)
			rollDataTracker -> this.clientRoll = entityData.get(rollDataTracker)
			thicknessDataTracker -> this.clientThickness = entityData.get(thicknessDataTracker)
			else -> {}
		}
	}

	companion object {
		val stateDataTracker: EntityDataAccessor<CompoundTag> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.COMPOUND_TAG)
		private val pigmentDataTracker: EntityDataAccessor<CompoundTag> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.COMPOUND_TAG)
		private val sizeDataTracker: EntityDataAccessor<Float> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.FLOAT)
		private val thicknessDataTracker: EntityDataAccessor<Float> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.FLOAT)
		private val rollDataTracker: EntityDataAccessor<Float> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.FLOAT)
	}
}
