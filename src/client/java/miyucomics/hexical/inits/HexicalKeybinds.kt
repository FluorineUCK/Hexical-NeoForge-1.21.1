package miyucomics.hexical.inits

import net.minecraft.client.KeyMapping
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import org.lwjgl.glfw.GLFW

object HexicalKeybinds {
	@JvmField
	val OPEN_HEXBOOK = KeyMapping("key.hexical.open_hexbook", GLFW.GLFW_KEY_N, "key.categories.hexical")
	val TELEPATHY_KEYBIND = KeyMapping("key.hexical.telepathy", GLFW.GLFW_KEY_G, "key.categories.hexical")
	val EVOKE_KEYBIND = KeyMapping("key.hexical.evoke", GLFW.GLFW_KEY_R, "key.categories.hexical")

	fun register(event: RegisterKeyMappingsEvent) {
		event.register(OPEN_HEXBOOK)
		event.register(EVOKE_KEYBIND)
		event.register(TELEPATHY_KEYBIND)
	}
}
