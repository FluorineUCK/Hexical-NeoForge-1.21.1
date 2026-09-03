package miyucomics.hexical.features.media_jar

import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.world.item.ItemDisplayContext
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.ItemStack

class MediaJarItemRenderer : BlockEntityWithoutLevelRenderer(
	Minecraft.getInstance().blockEntityRenderDispatcher,
	Minecraft.getInstance().entityModels
) {
	override fun renderByItem(stack: ItemStack, mode: ItemDisplayContext, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int, overlay: Int) {
		Minecraft.getInstance().blockRenderer.renderSingleBlock(HexicalBlocks.MEDIA_JAR_BLOCK.defaultBlockState(), matrices, vertexConsumers, light, overlay)
		val media = ItemStackDataCompat.blockEntityData(stack)?.getLong("media") ?: 0
		MediaJarRenderStuffs.renderFluid(matrices, vertexConsumers, media.toFloat() / MediaJarBlock.MAX_CAPACITY.toFloat())
	}
}
