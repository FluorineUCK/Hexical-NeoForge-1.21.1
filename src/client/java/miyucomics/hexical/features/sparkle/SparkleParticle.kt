package miyucomics.hexical.features.sparkle

import net.minecraft.client.particle.*
import net.minecraft.client.multiplayer.ClientLevel

class SparkleParticle(world: ClientLevel?, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double, provider: SpriteSet) : TextureSheetParticle(world, x, y, z, velocityX, velocityY, velocityZ) {
	private val spriteProvider: SpriteSet

	init {
		this.lifetime = 20
		this.xd = 0.0
		this.yd = 0.0
		this.zd = 0.0
		this.spriteProvider = provider
		this.pickSprite(provider)
		this.setSpriteFromAge(provider)
		this.scale(2.5f)
	}

	override fun tick() {
		super.tick()
		this.setSpriteFromAge(this.spriteProvider)
	}

	public override fun getLightColor(tint: Float): Int {
		val i = super.getLightColor(tint)
		val k = i shr 16 and 0xFF
		return 240 or (k shl 16)
	}

	override fun getRenderType(): ParticleRenderType {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT
	}

	@JvmRecord
	data class Factory(val spriteProvider: SpriteSet) : ParticleProvider<SparkleParticleEffect> {
		override fun createParticle(effect: SparkleParticleEffect, world: ClientLevel, d: Double, e: Double, f: Double, g: Double, h: Double, i: Double): Particle {
			val sparkleParticle = SparkleParticle(world, d, e, f, g, h, i, this.spriteProvider)
			sparkleParticle.setColor(effect.color.x, effect.color.y, effect.color.z)
			sparkleParticle.setLifetime(effect.lifespan)
			return sparkleParticle
		}
	}
}
