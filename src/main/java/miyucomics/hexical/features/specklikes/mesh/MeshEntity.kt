package miyucomics.hexical.features.specklikes.mesh

import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import dev.kosmx.playerAnim.core.util.Vec3f
import miyucomics.hexical.features.specklikes.BaseSpecklike
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.world.entity.EntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level

class MeshEntity(entityType: EntityType<out MeshEntity>, world: Level) : BaseSpecklike(entityType, world) {
	constructor(world: Level) : this(HexicalEntities.MESH_ENTITY, world)

	var clientVertices: MutableList<Vec3f> = mutableListOf()

	override fun readAdditionalSaveData(nbt: CompoundTag) {
		super.readAdditionalSaveData(nbt)
		entityData.set(stateDataTracker, nbt.getCompound("shape"))
	}

	override fun addAdditionalSaveData(nbt: CompoundTag) {
		super.addAdditionalSaveData(nbt)
		nbt.putCompound("shape", entityData.get(stateDataTracker))
	}

	fun getShape(): List<Vec3Iota> {
		val list = entityData.get(stateDataTracker).getList("shape", Tag.TAG_FLOAT.toInt())
		val deserializedVertices = mutableListOf<Vec3Iota>()
		for (i in 0 until (list.size / 3))
			deserializedVertices.add(Vec3Iota(Vec3(list.getFloat(3 * i).toDouble(), list.getFloat(3 * i + 1).toDouble(), list.getFloat(3 * i + 2).toDouble())))
		return deserializedVertices
	}

	fun setShape(shape: List<Vec3f>) {
		val compound = CompoundTag()
		val list = ListTag()
		for (vertex in shape) {
			list.add(FloatTag.valueOf(vertex.x))
			list.add(FloatTag.valueOf(vertex.y))
			list.add(FloatTag.valueOf(vertex.z))
		}
		compound.putList("shape", list)
		this.entityData.set(stateDataTracker, compound)
	}

	override fun processState() {
		val list = this.entityData.get(stateDataTracker).getList("shape", Tag.TAG_FLOAT.toInt())
		this.clientVertices = mutableListOf()
		for (i in 0 until (list.size / 3))
			clientVertices.add(Vec3f(list.getFloat(3 * i), list.getFloat(3 * i + 1), list.getFloat(3 * i + 2)))
	}
}
