package miyucomics.hexical.features.lesser_sentinels

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.minecraft.server.level.ServerPlayer

object ServerLesserSentinelPusher : InitHook() {
	val LESSER_SENTINEL_CHANNEL = HexicalMain.id("lesser_sentinels")

	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onLogin)
		NeoForge.EVENT_BUS.addListener(::onChangedDimension)
	}

	private fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
		(event.entity as? ServerPlayer)?.syncLesserSentinels()
	}

	private fun onChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
		(event.entity as? ServerPlayer)?.syncLesserSentinels()
	}
}
