package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.api.utils.vecFromNBT
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec3
import net.minecraft.core.HolderLookup

class ArchLampField : PlayerField {
	var position: Vec3 = Vec3.ZERO
	var rotation: Vec3 = Vec3.ZERO
	var velocity: Vec3 = Vec3.ZERO
	var storage: CompoundTag = CompoundTag()
	var time: Long = 0

	override fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		if (!compound.contains("arch_lamp"))
			return
		val archLampData = compound.getCompound("arch_lamp")
		this.position = vecFromNBT(archLampData.getLongArray("position"))
		this.rotation = vecFromNBT(archLampData.getLongArray("rotation"))
		this.velocity = vecFromNBT(archLampData.getLongArray("velocity"))
		this.storage = archLampData.getCompound("storage")
		this.time = archLampData.getLong("time")
	}

	override fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		compound.putCompound("arch_lamp", CompoundTag().also {
			it.put("position", this.position.serializeToNBT())
			it.put("rotation", this.rotation.serializeToNBT())
			it.put("velocity", this.velocity.serializeToNBT())
			it.putCompound("storage", this.storage)
			it.putLong("time", this.time)
		})
	}
}

fun Player.getArchLampField() = this.getHexicalPlayerManager().get(ArchLampField::class)
