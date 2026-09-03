package miyucomics.hexical.features.player

import miyucomics.hexical.misc.InitHook
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent

object RespawnPersistHook : InitHook() {
	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onClone)
	}

	private fun onClone(event: PlayerEvent.Clone) {
		event.entity.getHexicalPlayerManager().handleRespawn(event.entity, event.original)
	}
}
