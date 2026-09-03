package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.inits.HexicalBlocks.MEDIA_JAR_BLOCK
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.Component

object MediaJarRenderHooks : InitHook() {
	override fun init() {
		ItemBlockRenderTypes.setRenderLayer(MEDIA_JAR_BLOCK, RenderType.cutout())
		ScryingLensOverlayRegistry.addDisplayer(MEDIA_JAR_BLOCK) { lines, _, pos, _, world, _ ->
			val jar = world.getBlockEntity(pos) as MediaJarBlockEntity
			lines.add(Pair(ItemStack(HexItems.AMETHYST_DUST.get()), Component.translatable("hexcasting.tooltip.media", TextUtilities.DUST_AMOUNT.format(jar.getMedia().toFloat() / MediaConstants.DUST_UNIT.toFloat()))))
		}
	}
}
