package miyucomics.hexical.features.telepathy

import miyucomics.hexical.inits.HexicalKeybinds
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.KeyStatePayload
import miyucomics.hexical.network.ScrollPayload
import net.minecraft.client.Minecraft
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor

object ClientPeripheralPusher : InitHook() {
	private var previousState = mutableMapOf<String, Boolean>()

	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onClientTick)
		NeoForge.EVENT_BUS.addListener(::onMouseScroll)
	}

	private fun onClientTick(event: ClientTickEvent.Post) {
		val client = Minecraft.getInstance()
		if (client.player == null) return

		for (key in listOf(client.options.keyUp, client.options.keyLeft, client.options.keyRight, client.options.keyDown, client.options.keyJump, client.options.keyShift, client.options.keyUse, client.options.keyAttack, HexicalKeybinds.TELEPATHY_KEYBIND, HexicalKeybinds.EVOKE_KEYBIND)) {
				if (previousState.keys.contains(key.name)) {
					if (previousState[key.name] == true && !key.isDown) {
						PacketDistributor.sendToServer(KeyStatePayload(key.name, false))
					} else if (previousState[key.name] == false && key.isDown) {
						PacketDistributor.sendToServer(KeyStatePayload(key.name, true))
					}
				}

				previousState[key.name] = key.isDown
			}
	}

	private fun onMouseScroll(event: InputEvent.MouseScrollingEvent) {
		if (HexicalKeybinds.TELEPATHY_KEYBIND.isDown) {
			PacketDistributor.sendToServer(ScrollPayload(event.scrollDeltaY.toInt()))
			event.isCanceled = true
		}
	}
}
