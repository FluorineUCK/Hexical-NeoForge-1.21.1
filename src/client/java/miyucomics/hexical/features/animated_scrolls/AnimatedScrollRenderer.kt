package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.client.render.makeZappy
import miyucomics.hexical.RenderUtils
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec2
import org.joml.Matrix3f
import org.joml.Matrix4f

class AnimatedScrollRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<AnimatedScrollEntity>(ctx) {
	override fun render(scroll: AnimatedScrollEntity, yaw: Float, deltaTick: Float, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int) {
		matrices.pushPose()
		matrices.mulPose(Axis.XP.rotationDegrees(scroll.xRot))
		matrices.mulPose(Axis.YP.rotationDegrees(180.0f - scroll.yRot))
		val worldLight = LevelRenderer.getLightColor(scroll.level(), scroll.blockPosition())
		if (scroll.entityData.get(AnimatedScrollEntity.stateDataTracker) != 2)
			drawFrame(matrices, vertexConsumers, getTextureLocation(scroll), scroll.entityData.get(AnimatedScrollEntity.sizeDataTracker).toFloat(), worldLight)
		drawPattern(matrices, vertexConsumers, scroll, worldLight)
		matrices.popPose()
	}

	override fun getTextureLocation(scroll: AnimatedScrollEntity) = when (scroll.entityData.get(AnimatedScrollEntity.sizeDataTracker)) {
		1 -> if (scroll.entityData.get(AnimatedScrollEntity.stateDataTracker) == 1) ANCIENT_SMALL else PRISTINE_SMALL
		2 -> if (scroll.entityData.get(AnimatedScrollEntity.stateDataTracker) == 1) ANCIENT_MEDIUM else PRISTINE_MEDIUM
		3 -> if (scroll.entityData.get(AnimatedScrollEntity.stateDataTracker) == 1) ANCIENT_LARGE else PRISTINE_LARGE
		else -> ANCIENT_SMALL
	}

	companion object {
		private val PRISTINE_SMALL: ResourceLocation = modLoc("textures/block/scroll_paper.png")
		private val PRISTINE_MEDIUM: ResourceLocation = modLoc("textures/entity/scroll_medium.png")
		private val PRISTINE_LARGE: ResourceLocation = modLoc("textures/entity/scroll_large.png")
		private val ANCIENT_SMALL: ResourceLocation = modLoc("textures/block/ancient_scroll_paper.png")
		private val ANCIENT_MEDIUM: ResourceLocation = modLoc("textures/entity/scroll_ancient_medium.png")
		private val ANCIENT_LARGE: ResourceLocation = modLoc("textures/entity/scroll_ancient_large.png")
		private val WHITE: ResourceLocation = modLoc("textures/entity/white.png")

		private fun vertex(mat: Matrix4f, normal: Matrix3f, light: Int, verts: VertexConsumer, x: Float, y: Float, z: Float, u: Float, v: Float, nx: Float, ny: Float, nz: Float) {
			verts.addVertex(mat, x, y, z).setColor(-0x1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(nx, ny, nz)
		}

		private fun drawFrame(matrices: PoseStack, vertexConsumers: MultiBufferSource, texture: ResourceLocation, size: Float, light: Int) {
			matrices.pushPose()
			matrices.translate((-size / 2f).toDouble(), (-size / 2f).toDouble(), (1f / 32f).toDouble())

			val dz = -1f / 16f
			val margin = 1f / 48f
			val last = matrices.last()
			val mat = last.pose()
			val norm = last.normal()

			val verts = vertexConsumers.getBuffer(RenderType.entityCutout(texture))
			vertex(mat, norm, light, verts, 0f, 0f, dz, 0f, 0f, 0f, 0f, -1f)
			vertex(mat, norm, light, verts, 0f, size, dz, 0f, 1f, 0f, 0f, -1f)
			vertex(mat, norm, light, verts, size, size, dz, 1f, 1f, 0f, 0f, -1f)
			vertex(mat, norm, light, verts, size, 0f, dz, 1f, 0f, 0f, 0f, -1f)

			vertex(mat, norm, light, verts, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1f)
			vertex(mat, norm, light, verts, size, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
			vertex(mat, norm, light, verts, size, size, 0f, 1f, 1f, 0f, 0f, 1f)
			vertex(mat, norm, light, verts, 0f, size, 0f, 0f, 1f, 0f, 0f, 1f)

			vertex(mat, norm, light, verts, 0f, 0f, 0f, 0f, 0f, 0f, -1f, 0f)
			vertex(mat, norm, light, verts, 0f, 0f, dz, 0f, margin, 0f, -1f, 0f)
			vertex(mat, norm, light, verts, size, 0f, dz, 1f, margin, 0f, -1f, 0f)
			vertex(mat, norm, light, verts, size, 0f, 0f, 1f, 0f, 0f, -1f, 0f)

			vertex(mat, norm, light, verts, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 0f)
			vertex(mat, norm, light, verts, 0f, size, 0f, 0f, 1f, -1f, 0f, 0f)
			vertex(mat, norm, light, verts, 0f, size, dz, margin, 1f, -1f, 0f, 0f)
			vertex(mat, norm, light, verts, 0f, 0f, dz, margin, 0f, -1f, 0f, 0f)

			vertex(mat, norm, light, verts, size, 0f, dz, 1f - margin, 0f, 1f, 0f, 0f)
			vertex(mat, norm, light, verts, size, size, dz, 1f - margin, 1f, 1f, 0f, 0f)
			vertex(mat, norm, light, verts, size, size, 0f, 1f, 1f, 1f, 0f, 0f)
			vertex(mat, norm, light, verts, size, 0f, 0f, 1f, 0f, 1f, 0f, 0f)

			vertex(mat, norm, light, verts, 0f, size, dz, 0f, 1f - margin, 0f, 1f, 0f)
			vertex(mat, norm, light, verts, 0f, size, 0f, 0f, 1f, 0f, 1f, 0f)
			vertex(mat, norm, light, verts, size, size, 0f, 1f, 1f, 0f, 1f, 0f)
			vertex(mat, norm, light, verts, size, size, dz, 1f, 1f - margin, 0f, 1f, 0f)

			matrices.popPose()
		}

		private fun drawPattern(matrices: PoseStack, vertexConsumers: MultiBufferSource, scroll: AnimatedScrollEntity, light: Int) {
			if (scroll.entityData.get(AnimatedScrollEntity.stateDataTracker) != 2)
				matrices.translate(0.0, 0.0, -0.75 / 16.0)
			val scale = when (scroll.entityData.get(AnimatedScrollEntity.sizeDataTracker)) {
				1 -> 0.5f
				2 -> 1.25f
				3 -> 2f
				else -> 1f
			}
			matrices.scale(scale, scale, 1f)
			val peek = matrices.last()
			val buffer = vertexConsumers.getBuffer(RenderType.entityCutout(WHITE))
			val zappy = makeZappy(scroll.cachedVerts, null, 10, 1f, 0.1f, 0f, 0.1f, 0.9f, 0.0)

			fun makeVertex(pos: Vec2) = buffer.addVertex(peek.pose(), pos.x, pos.y, 0f)
				.setColor(scroll.entityData.get(AnimatedScrollEntity.colorDataTracker))
				.setUv(0f, 0f)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(if (scroll.entityData.get(AnimatedScrollEntity.glowDataTracker)) LightTexture.FULL_BRIGHT else light)
				.setNormal(peek, 0f, 1f, 0f)

			RenderUtils.quadifyLines(::makeVertex, 0.025f / scroll.entityData.get(AnimatedScrollEntity.sizeDataTracker), zappy)
		}
	}
}
