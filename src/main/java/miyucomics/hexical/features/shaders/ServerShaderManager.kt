package miyucomics.hexical.features.shaders

import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.ShaderPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor

object ServerShaderManager : InitHook() {
    fun setShader(player: ServerPlayer, shader: ResourceLocation?) {
		PacketDistributor.sendToPlayer(player, ShaderPayload(shader))
    }

    override fun init() {
		NeoForge.EVENT_BUS.addListener(::onClone)
    }

	private fun onClone(event: PlayerEvent.Clone) {
		if (event.isWasDeath) (event.entity as? ServerPlayer)?.let { setShader(it, null) }
	}
}
