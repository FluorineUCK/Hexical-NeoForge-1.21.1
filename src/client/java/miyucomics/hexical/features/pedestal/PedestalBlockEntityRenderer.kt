package miyucomics.hexical.features.pedestal

import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.ItemDisplayContext
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource

class PedestalBlockEntityRenderer(context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<PedestalBlockEntity> {
	private val itemRenderer = context.itemRenderer
	private val random = RandomSource.create()

	override fun render(pedestal: PedestalBlockEntity, tickDelta: Float, matrices: PoseStack, vertices: MultiBufferSource, light: Int, overlay: Int) {
		if (pedestal.heldStack.isEmpty)
			return

		val level = pedestal.level ?: return
		val time = level.gameTime + tickDelta
		val offset = Vec3.atLowerCornerOf(pedestal.normalVector).scale(PedestalBlockEntity.HEIGHT)
		val light = LevelRenderer.getLightColor(level, pedestal.blockPos.offset(pedestal.normalVector))
		random.setSeed((if (pedestal.heldStack.isEmpty) 187 else Item.getId(pedestal.heldStack.item) + pedestal.heldStack.damageValue).toLong())
		val bakedModel = itemRenderer.getModel(pedestal.heldStack, level, null, 0)
		val hasDepth = bakedModel.isGui3d()
		val renderedAmount = getRenderedAmount(pedestal.heldStack)
		val scaleX = bakedModel.transforms.ground.scale.x()
		val scaleY = bakedModel.transforms.ground.scale.y()
		val scaleZ = bakedModel.transforms.ground.scale.z()

		matrices.pushPose()
		matrices.translate(offset.x + 0.5, offset.y + 0.35, offset.z + 0.5)
		matrices.mulPose(Axis.YP.rotationDegrees(time * 4))

		if (!hasDepth) {
			val initialOffsetZ = -Z_LAYER_OFFSET * (renderedAmount - 1) * scaleZ / 2f
			matrices.translate(0f, 0f, initialOffsetZ)
		}

		for (i in 0 until renderedAmount) {
			matrices.pushPose()

			if (i > 0) {
				if (hasDepth) {
					val randomX = (random.nextFloat() * 2.0f - 1.0f) * ITEM_OFFSET_MULTIPLIER
					val randomY = (random.nextFloat() * 2.0f - 1.0f) * ITEM_OFFSET_MULTIPLIER
					val randomZ = (random.nextFloat() * 2.0f - 1.0f) * ITEM_OFFSET_MULTIPLIER
					matrices.translate(randomX, randomY, randomZ)
				} else {
					val randomX = (random.nextFloat() * 2.0f - 1.0f) * ITEM_OFFSET_MULTIPLIER * 0.5f
					val randomY = (random.nextFloat() * 2.0f - 1.0f) * ITEM_OFFSET_MULTIPLIER * 0.5f
					matrices.translate(randomX, randomY, 0.0f)
				}
			}

			itemRenderer.renderStatic(pedestal.heldStack, ItemDisplayContext.GROUND, light, OverlayTexture.NO_OVERLAY, matrices, vertices, level, 0)
			matrices.popPose()
			if (!hasDepth)
				matrices.translate(0.0f * scaleX, 0.0f * scaleY, Z_LAYER_OFFSET * scaleZ)
		}

		matrices.popPose()
	}

	private fun getRenderedAmount(stack: ItemStack): Int {
		return when {
			stack.count > 48 -> 5
			stack.count > 32 -> 4
			stack.count > 16 -> 3
			stack.count > 1 -> 2
			else -> 1
		}
	}

	companion object {
		private const val ITEM_OFFSET_MULTIPLIER = 0.15f
		private const val Z_LAYER_OFFSET = 0.09375f
	}
}
