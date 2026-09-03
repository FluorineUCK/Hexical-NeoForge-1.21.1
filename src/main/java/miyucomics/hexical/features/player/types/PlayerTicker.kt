package miyucomics.hexical.features.player.types

import net.minecraft.world.entity.player.Player

interface PlayerTicker {
	fun tick(player: Player) {}
}