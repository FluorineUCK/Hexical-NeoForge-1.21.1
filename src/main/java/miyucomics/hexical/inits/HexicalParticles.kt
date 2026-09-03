package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.sparkle.SparkleParticleEffect
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalParticles {
	val CONFETTI_PARTICLE: SimpleParticleType = SimpleParticleType(true)
	val SPARKLE_PARTICLE: ParticleType<SparkleParticleEffect> = object : ParticleType<SparkleParticleEffect>(true) {
		override fun codec() = SparkleParticleEffect.CODEC
		override fun streamCodec() = SparkleParticleEffect.STREAM_CODEC
	}

	fun register(event: RegisterEvent) {
		if (event.registryKey != Registries.PARTICLE_TYPE) return
		event.register(Registries.PARTICLE_TYPE, HexicalMain.id("confetti")) { CONFETTI_PARTICLE }
		event.register(Registries.PARTICLE_TYPE, HexicalMain.id("sparkle")) { SPARKLE_PARTICLE }
	}
}
