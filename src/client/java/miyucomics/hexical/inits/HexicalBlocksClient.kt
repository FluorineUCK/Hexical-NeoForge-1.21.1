package miyucomics.hexical.inits

import miyucomics.hexical.inits.HexicalBlocks.PERIWINKLE_FLOWER
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ItemBlockRenderTypes

object HexicalBlocksClient {
	fun clientInit() {
		ItemBlockRenderTypes.setRenderLayer(PERIWINKLE_FLOWER, RenderType.cutout())
	}
}
