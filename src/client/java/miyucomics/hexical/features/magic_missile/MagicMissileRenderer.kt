package miyucomics.hexical.features.magic_missile

import miyucomics.hexical.HexicalMain
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ArrowRenderer
import net.minecraft.resources.ResourceLocation

class MagicMissileRenderer(context: EntityRendererProvider.Context) : ArrowRenderer<MagicMissileEntity>(context) {
	override fun getTextureLocation(entity: MagicMissileEntity) = TEXTURE
	companion object {
		val TEXTURE: ResourceLocation = HexicalMain.id("textures/entity/magic_missile.png")
	}
}
