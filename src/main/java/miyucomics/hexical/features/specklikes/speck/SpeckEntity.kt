package miyucomics.hexical.features.specklikes.speck

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.putCompound
import miyucomics.hexical.features.specklikes.BaseSpecklike
import miyucomics.hexical.hexcompat.deserializePattern
import miyucomics.hexical.hexcompat.serializePattern
import miyucomics.hexical.inits.HexicalEntities
import miyucomics.hexical.misc.PatternUtils
import net.minecraft.world.entity.EntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec2
import net.minecraft.world.level.Level

class SpeckEntity(entityType: EntityType<out SpeckEntity>, world: Level) : BaseSpecklike(entityType, world) {
	constructor(world: Level) : this(HexicalEntities.SPECK_ENTITY, world)

	var clientIsText = false
	var clientText: Component = Component.empty()
	var clientVerts: List<Vec2> = listOf()

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		super.readAdditionalSaveData(nbt)
		entityData.set(stateDataTracker, nbt.getCompound("display"))
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		super.addAdditionalSaveData(nbt)
		nbt.putCompound("display", entityData.get(stateDataTracker))
	}

	fun setIota(iota: Iota) {
		if (iota is PatternIota) {
			entityData.set(stateDataTracker, serializePattern(iota.pattern))
		} else {
			val compound = CompoundTag()
			compound.putString("text", Component.Serializer.toJson(iota.display(), registryAccess()))
			entityData.set(stateDataTracker, compound)
		}
	}

	override fun processState() {
		val raw = entityData.get(stateDataTracker)
		if (raw.contains("text")) {
			this.clientIsText = true
			this.clientText = Component.Serializer.fromJson(raw.getString("text"), registryAccess()) ?: Component.empty()
		} else {
			this.clientIsText = false
			this.clientVerts = deserializePattern(raw)?.let { PatternUtils.getNormalizedStrokes(it) } ?: emptyList()
		}
	}
}
