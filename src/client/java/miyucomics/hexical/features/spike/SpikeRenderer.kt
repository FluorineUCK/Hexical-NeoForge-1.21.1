package miyucomics.hexical.features.spike

import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix3f
import org.joml.Matrix4f
import kotlin.math.floor
import kotlin.math.max

class SpikeRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<SpikeEntity>(ctx) {
	private val textures = listOf(
		ResourceLocation.withDefaultNamespace("textures/block/small_amethyst_bud.png"),
		ResourceLocation.withDefaultNamespace("textures/block/medium_amethyst_bud.png"),
		ResourceLocation.withDefaultNamespace("textures/block/large_amethyst_bud.png"),
		ResourceLocation.withDefaultNamespace("textures/block/amethyst_cluster.png")
	)

	override fun getTextureLocation(spike: SpikeEntity): ResourceLocation {
		return textures[max(floor(spike.getAnimationProgress() * (textures.size - 1)).toInt(), 0)]
	}

	override fun render(spike: SpikeEntity, yaw: Float, deltaTick: Float, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int) {
		matrices.pushPose()
		val buffer = vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(spike)))

		val direction = spike.getDirection()
		matrices.mulPose(direction.rotation)
		// I don't know why this works, but it works and I am never touching it again.
		val y = direction.stepY.toDouble()
		matrices.translate(-0.5, -0.5 + y / 2, -1.0 + y * y / 2)

		val mat = matrices.last().pose()
		val norm = matrices.last().normal()

		vertex(mat, buffer, norm, 1f, 1f, 0f, 0f, 0f)
		vertex(mat, buffer, norm, 1f, 0f, 0f, 0f, 1f)
		vertex(mat, buffer, norm, 0f, 0f, 1f, 1f, 1f)
		vertex(mat, buffer, norm, 0f, 1f, 1f, 1f, 0f)
		vertex(mat, buffer, norm, 0f, 1f, 0f, 0f, 0f)
		vertex(mat, buffer, norm, 0f, 0f, 0f, 0f, 1f)
		vertex(mat, buffer, norm, 1f, 0f, 1f, 1f, 1f)
		vertex(mat, buffer, norm, 1f, 1f, 1f, 1f, 0f)

		matrices.popPose()
	}

	private fun vertex(mat: Matrix4f, verts: VertexConsumer, normal: Matrix3f, x: Float, y: Float, z: Float, u: Float, v: Float) = verts.addVertex(mat, x, y, z)
		.setColor(255, 255, 255, 255)
		.setUv(u, v)
		.setOverlay(OverlayTexture.NO_OVERLAY)
		.setLight(LightTexture.FULL_BRIGHT)
		.setNormal(0f, 1f, 0f)
}
