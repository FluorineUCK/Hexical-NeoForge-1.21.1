package miyucomics.hexical.features.pedestal

import miyucomics.hexical.inits.HexicalBlocks.PEDESTAL_BLOCK_ENTITY
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object PedestalRenderHooks : InitHook() {
	override fun init() {
		BlockEntityRenderers.register(PEDESTAL_BLOCK_ENTITY, ::PedestalBlockEntityRenderer)
	}
}