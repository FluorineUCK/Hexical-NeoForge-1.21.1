package miyucomics.hexical.features.telepathy

import miyucomics.hexical.features.evocation.ServerEvocationManager
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.KeyStatePayload
import net.minecraft.server.level.ServerPlayer

object ServerPeripheralReceiver : InitHook() {
	private val ACCEPTED_KEYS = setOf(
		"key.forward",
		"key.left",
		"key.right",
		"key.back",
		"key.jump",
		"key.sneak",
		"key.use",
		"key.attack",
		"key.hexical.telepathy",
		"key.hexical.evoke"
	)

	override fun init() = Unit

	@JvmStatic
	fun handleKeyState(player: ServerPlayer, payload: KeyStatePayload) {
		if (payload.key !in ACCEPTED_KEYS) return
		val server = player.server ?: return
		player.serverKeybindActive()[payload.key] = payload.pressed
		player.serverKeybindDuration()[payload.key] = 0
		if (payload.pressed && payload.key == "key.hexical.telepathy")
			player.serverScroll = 0
		if (payload.key == "key.hexical.evoke") {
			if (payload.pressed)
				ServerEvocationManager.startEvocation(player, server)
			else
				ServerEvocationManager.endEvocation(player, server)
		}
	}

	@JvmStatic
	fun handleScroll(player: ServerPlayer, delta: Int) {
		player.serverScroll += delta.coerceIn(-64, 64)
	}
}
