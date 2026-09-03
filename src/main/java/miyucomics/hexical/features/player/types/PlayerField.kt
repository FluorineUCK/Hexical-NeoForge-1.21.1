package miyucomics.hexical.features.player.types

import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.HolderLookup

interface PlayerField {
	fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {}
	fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {}
	fun handleRespawn(new: Player, old: Player) {}
}
