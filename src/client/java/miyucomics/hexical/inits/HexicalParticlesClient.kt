package miyucomics.hexical.inits

import miyucomics.hexical.features.confetti.ConfettiParticle
import miyucomics.hexical.features.sparkle.SparkleParticle
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent

object HexicalParticlesClient {
	fun registerProviders(event: RegisterParticleProvidersEvent) {
		event.registerSpriteSet(HexicalParticles.CONFETTI_PARTICLE) { sprite -> ConfettiParticle.Factory(sprite) }
		event.registerSpriteSet(HexicalParticles.SPARKLE_PARTICLE) { sprite -> SparkleParticle.Factory(sprite) }
	}
}
