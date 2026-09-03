package miyucomics.hexical.features.confetti

import miyucomics.hexical.HexicalMain
import net.minecraft.client.particle.*
import net.minecraft.client.Camera
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise
import net.minecraft.world.level.levelgen.LegacyRandomSource
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

class ConfettiParticle(world: ClientLevel, x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double, sprite: SpriteSet) : TextureSheetParticle(world, x, y, z) {
	private val particleId: Double
	private var pitch = 0f
	private var yaw = 0f
	private var prevPitch = 0f
	private var prevYaw = 0f
	private var deltaPitch = 0f
	private var deltaYaw = 0f
	private var deltaRoll = 0f

	init {
		this.pickSprite(sprite)
		this.particleId = random.nextDouble()
		this.xd = dx
		this.yd = dy
		this.zd = dz

		this.setSize(0.001f, 0.001f)
		this.gravity = 0.2f
		this.friction = 0.9f
		this.lifetime = random.nextInt(400) + 300
		this.quadSize *= 1.25f
	}

	override fun render(consumer: VertexConsumer, camera: Camera, deltaTick: Float) {
		val rotation = Quaternionf()
			.rotateZ(Mth.lerp(deltaTick, this.oRoll, this.roll))
			.rotateY(Mth.lerp(deltaTick, this.prevYaw, this.yaw))
			.rotateX(Mth.lerp(deltaTick, this.prevPitch, this.pitch))

		val vertices = arrayOf(
			Vector3f(-this.quadSize, -this.quadSize, 0f),
			Vector3f(-this.quadSize, this.quadSize, 0f),
			Vector3f(this.quadSize, this.quadSize, 0f),
			Vector3f(this.quadSize, -this.quadSize, 0f)
		).map { vertex ->
			vertex.rotate(rotation).add(
				(Mth.lerp(deltaTick, this.xo.toFloat(), this.x.toFloat()) - camera.position.x.toFloat()),
				(Mth.lerp(deltaTick, this.yo.toFloat(), this.y.toFloat()) - camera.position.y.toFloat()),
				(Mth.lerp(deltaTick, this.zo.toFloat(), this.z.toFloat()) - camera.position.z.toFloat())
			)
		}

		val light = this.getLightColor(deltaTick)
		fun vertex(vertex: Vector3f, u: Float, v: Float) =
			consumer.addVertex(vertex.x(), vertex.y(), vertex.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light)

		vertex(vertices[0], this.u1, this.v1)
		vertex(vertices[1], this.u1, this.v0)
		vertex(vertices[2], this.u0, this.v0)
		vertex(vertices[3], this.u0, this.v1)

		vertex(vertices[3], this.u0, this.v1)
		vertex(vertices[2], this.u0, this.v0)
		vertex(vertices[1], this.u1, this.v0)
		vertex(vertices[0], this.u1, this.v1)
	}

	override fun tick() {
		this.xd += X_NOISE.getValue(particleId, age.toDouble(), false) / 100
		this.zd += Z_NOISE.getValue(particleId, age.toDouble(), false) / 100

		this.prevYaw = this.yaw
		this.prevPitch = this.pitch
		this.oRoll = this.roll

		if (onGround || (this.x == this.xo && this.z == this.zo && this.y == this.yo && this.age != 0)) {
			this.age = max(age.toDouble(), (this.lifetime - 20).toDouble()).toInt()
		} else {
			this.deltaYaw += (YAW_NOISE.getValue(particleId, age.toDouble(), false)).toFloat() / 10f
			this.deltaRoll += (ROLL_NOISE.getValue(particleId, age.toDouble(), false)).toFloat() / 10f
			this.deltaPitch += (PITCH_NOISE.getValue(particleId, age.toDouble(), false)).toFloat() / 10f
			this.yaw += this.deltaYaw
			this.pitch += this.deltaPitch
			this.roll += this.deltaRoll
		}

		this.deltaYaw *= 0.98f
		this.deltaRoll *= 0.98f
		this.deltaPitch *= 0.98f

		super.tick()
	}

	class Factory(private val sprite: SpriteSet) : ParticleProvider<SimpleParticleType> {
		override fun createParticle(typeIn: SimpleParticleType, world: ClientLevel, x: Double, y: Double, z: Double, dx: Double, dy: Double, dz: Double): Particle {
			val particle = ConfettiParticle(world, x, y, z, dx, dy, dz, sprite)
			particle.yaw = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
			particle.pitch = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
			particle.roll = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
			return particle
		}
	}

	override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
	companion object {
		private val X_NOISE: PerlinSimplexNoise = noise(58637214)
		private val Z_NOISE: PerlinSimplexNoise = noise(823917)
		private val YAW_NOISE: PerlinSimplexNoise = noise(28943157)
		private val ROLL_NOISE: PerlinSimplexNoise = noise(80085)
		private val PITCH_NOISE: PerlinSimplexNoise = noise(49715286)
		private fun noise(seed: Int) =
			PerlinSimplexNoise(LegacyRandomSource(seed.toLong()), listOf(-7, -2, -1, 0, 1, 2))
	}
}
