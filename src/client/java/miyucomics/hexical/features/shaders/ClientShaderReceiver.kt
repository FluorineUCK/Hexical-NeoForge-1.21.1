package miyucomics.hexical.features.shaders

import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.ShaderPayload
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.common.NeoForge

object ClientShaderReceiver : InitHook() {
    override fun init() {
		NeoForge.EVENT_BUS.addListener(::onLogout)
    }

	fun handle(payload: ShaderPayload) = ShaderRenderer.setEffect(payload.shader)

	private fun onLogout(event: ClientPlayerNetworkEvent.LoggingOut) = ShaderRenderer.setEffect(null)
}
