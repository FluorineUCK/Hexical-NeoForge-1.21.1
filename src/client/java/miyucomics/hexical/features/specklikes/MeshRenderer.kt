package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.features.specklikes.mesh.MeshEntity
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import com.mojang.math.Axis
import net.minecraft.world.phys.Vec3
import org.joml.Matrix3f
import org.joml.Matrix4f
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalStdlibApi::class)
class MeshRenderer(ctx: EntityRendererProvider.Context) : EntityRenderer<MeshEntity>(ctx) {
	override fun getTextureLocation(entity: MeshEntity): ResourceLocation = WHITE
	override fun shouldRender(entity: MeshEntity, frustum: Frustum, x: Double, y: Double, z: Double) = true
	override fun render(entity: MeshEntity, yaw: Float, tickDelta: Float, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int) {
		val vertices = entity.clientVertices
		if (vertices.size < 2)
			return

		matrices.pushPose()
		matrices.translate(0.0, 0.25, 0.0)
		matrices.mulPose(Axis.YP.rotationDegrees(-entity.yRot))
		matrices.mulPose(Axis.XP.rotationDegrees(entity.xRot))
		matrices.mulPose(Axis.ZP.rotationDegrees(entity.clientRoll))
		matrices.scale(entity.clientSize, entity.clientSize, entity.clientSize)

		val buf = vertexConsumers.getBuffer(renderLayer)
		for (i in 1..<vertices.size) {
			val a = vertices[i - 1]
			val b = vertices[i]
			drawConnection(matrices, buf, Vec3(a.x.toDouble(), a.y.toDouble(), a.z.toDouble()), Vec3(b.x.toDouble(), b.y.toDouble(), b.z.toDouble()), entity.clientPigment, entity.clientThickness * 0.025)
		}
		matrices.popPose()
	}

	private fun drawConnection(matrices: PoseStack, vertices: VertexConsumer, start: Vec3, end: Vec3, pigment: FrozenPigment, thickness: Double) {
		val direction = end.subtract(start).normalize()
		var perpendicular = direction.cross(Vec3(1.0, 0.0, 0.0))
		if (direction.dot(Vec3(1.0, 0.0, 0.0)) > 0.99 || direction.dot(Vec3(1.0, 0.0, 0.0)) < -0.99)
			perpendicular = direction.cross(Vec3(0.0, 1.0, 0.0))

		val pose = matrices.last().pose()
		val norm = matrices.last().normal()

		// these calculations are reused often but could just be ran once
		val perpendicularCrossProduct = perpendicular.cross(direction)
		val directionDotProduct = direction.scale(direction.dot(perpendicular))

		for (i in 0 until SIDES) {
			val startAngle = i * ANGLE_INCREMENT
			val endAngle = (i + 1) % SIDES * ANGLE_INCREMENT
			val a = perpendicular.scale(cos(startAngle)).add(perpendicularCrossProduct.scale(sin(startAngle))).add(directionDotProduct.scale(1 - cos(startAngle))).normalize().scale(thickness)
			val b = perpendicular.scale(cos(endAngle)).add(perpendicularCrossProduct.scale(sin(endAngle))).add(directionDotProduct.scale(1 - cos(endAngle))).normalize().scale(thickness)

			vertex(pose, norm, vertices, start.add(a), pigment)
			vertex(pose, norm, vertices, start.add(b), pigment)
			vertex(pose, norm, vertices, end.add(b), pigment)
			vertex(pose, norm, vertices, end.add(a), pigment)
		}
	}

	private fun vertex(pose: Matrix4f, norm: Matrix3f, vertices: VertexConsumer, position: Vec3, pigment: FrozenPigment) {
		vertices.addVertex(pose, position.x.toFloat(), position.y.toFloat(), position.z.toFloat())
			.setColor(pigment.colorProvider.getColor(0f, position))
			.setUv(0f, 0f)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(LightTexture.FULL_BRIGHT)
			.setNormal(0f, 1f, 0f)
	}

	companion object {
		private const val SIDES = 6
		private const val ANGLE_INCREMENT = 2 * Math.PI / SIDES
		private val WHITE = modLoc("textures/entity/white.png")
		private val renderLayer = RenderType.entityCutoutNoCull(WHITE)
	}
}
