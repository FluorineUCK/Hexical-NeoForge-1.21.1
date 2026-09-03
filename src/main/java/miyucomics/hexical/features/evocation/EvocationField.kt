package miyucomics.hexical.features.evocation

import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag

class EvocationField : PlayerField {
	var active: Boolean = false
	var duration: Int = -1
	var evocation: ListTag = ListTag()

	override fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		this.evocation = compound.getList("evocation_hex", Tag.TAG_COMPOUND.toInt())
	}

	override fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		compound.putList("evocation_hex", evocation)
	}

	override fun handleRespawn(new: Player, old: Player) {
		new.evocation = old.evocation
	}
}

var Player.evocationActive: Boolean
	get() = this.getHexicalPlayerManager().get(EvocationField::class).active
	set(active) { this.getHexicalPlayerManager().get(EvocationField::class).active = active }
var Player.evocationDuration: Int
	get() = this.getHexicalPlayerManager().get(EvocationField::class).duration
	set(duration) { this.getHexicalPlayerManager().get(EvocationField::class).duration = duration }
var Player.evocation: ListTag
	get() = this.getHexicalPlayerManager().get(EvocationField::class).evocation
	set(hex) { this.getHexicalPlayerManager().get(EvocationField::class).evocation = hex }
