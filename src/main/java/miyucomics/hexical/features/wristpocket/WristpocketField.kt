package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.utils.serializeToNBT
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.HolderLookup

class WristpocketField : PlayerField {
	var wristpocket: ItemStack = ItemStack.EMPTY

	override fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		if (!compound.contains("wristpocket"))
			return
		wristpocket = ItemStack.parseOptional(provider, compound.getCompound("wristpocket"))
	}

	override fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		compound.put("wristpocket", wristpocket.saveOptional(provider))
	}

	override fun handleRespawn(new: Player, old: Player) {
		new.wristpocket = old.wristpocket
	}
}

var Player.wristpocket: ItemStack
	get() = this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket
	set(stack) { this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket = stack }
