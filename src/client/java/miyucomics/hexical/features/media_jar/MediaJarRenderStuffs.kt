package miyucomics.hexical.features.media_jar

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.core.Direction
import com.mojang.math.Axis
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.abs

object MediaJarRenderStuffs {
	fun renderFluid(matrices: PoseStack, vertexConsumers: MultiBufferSource, filled: Float, offset: Vector3f = Vector3f(0f)) {
		if (filled == 0f)
			return
		val consumer = vertexConsumers.getBuffer(MediaJarShader.mediaJarRenderLayer)
		matrices.pushPose()
		matrices.translate(0.5f, 1f / 16f, 0.5f)
		addRectangularPrism(consumer, matrices, height = filled * 12f / 16f, offset)
		matrices.popPose()
	}

	private val NEGATIVE_X_ROTATION: Quaternionf = Axis.XP.rotationDegrees(-90f)
	private val DIR2ROT: Map<Direction, Quaternionf> = enumValues<Direction>().associateWith { it.opposite.rotation.mul(NEGATIVE_X_ROTATION) }

	private fun addRectangularPrism(consumer: VertexConsumer, matrices: PoseStack, height: Float, offset: Vector3f) {
		val halfWidth = 0.5f / 2f
		val halfHeight = height / 2f

		matrices.pushPose()
		matrices.translate(0f, halfHeight, 0f)

		for (direction in Direction.values()) {
			var depth = halfWidth
			var y0 = -halfHeight
			var y1 = halfHeight

			if (direction.axis == Direction.Axis.Y) {
				depth = halfHeight
				y0 = -halfWidth
				y1 = halfWidth
			}

			matrices.pushPose()
			matrices.mulPose(DIR2ROT.getValue(direction))
			matrices.translate(0f, 0f, -depth)
			addQuad(consumer, matrices, -halfWidth, y0, halfWidth, y1, offset)
			matrices.popPose()
		}

		matrices.popPose()
	}

	private fun addQuad(consumer: VertexConsumer, matrices: PoseStack, x0: Float, y0: Float, x1: Float, y1: Float, offset: Vector3f) {
		val quadWidth = (abs(x0 - x1) * 255).toInt()
		val quadHeight = (abs(y0 - y1) * 255).toInt()
		consumer.addVertex(matrices.last().pose(), x0, y1, 0f).setUv(0f, 1f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x, offset.y, offset.z)
		consumer.addVertex(matrices.last().pose(), x1, y1, 0f).setUv(1f, 1f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x, offset.y, offset.z)
		consumer.addVertex(matrices.last().pose(), x1, y0, 0f).setUv(1f, 0f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x, offset.y, offset.z)
		consumer.addVertex(matrices.last().pose(), x0, y0, 0f).setUv(0f, 0f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x, offset.y, offset.z)
	}
}
