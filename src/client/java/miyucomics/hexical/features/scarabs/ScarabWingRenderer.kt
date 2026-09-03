package miyucomics.hexical.features.scarabs

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation

object ScarabWingRenderer : InitHook() {
	override fun init() {
		ItemProperties.register(HexicalItems.SCARAB_BEETLE_ITEM, ResourceLocation.withDefaultNamespace("active")) { stack, _, _, _ ->
			if (ItemStackDataCompat.customData(stack).getBoolean("active"))
				1.0f
			else
				0.0f
		}
	}
}
