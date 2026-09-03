package miyucomics.hexical.features.confetti

import miyucomics.hexical.inits.HexicalParticles
import miyucomics.hexical.network.ConfettiPayload
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

object ClientConfettiReceiver {
	fun handle(payload: ConfettiPayload) {
		val client = Minecraft.getInstance()
		val level = client.level ?: return
		val random = RandomSource.create(payload.seed)
		val pos = payload.pos
		val dir = payload.direction
		val speed = payload.speed
		level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.MASTER, 1f, 1f, true)
		repeat(100) {
			val alteredVelocity = if (dir == Vec3.ZERO) {
				Vec3.directionFromRotation(random.nextFloat() * 180f - 90f, random.nextFloat() * 360f).scale(speed)
			} else {
				dir.add(
					(random.nextDouble() * 2 - 1) / 5,
					(random.nextDouble() * 2 - 1) / 5,
					(random.nextDouble() * 2 - 1) / 5
				).scale((random.nextFloat() * 0.25 + 0.75) * speed)
			}
			level.addParticle(HexicalParticles.CONFETTI_PARTICLE, pos.x, pos.y, pos.z, alteredVelocity.x, alteredVelocity.y, alteredVelocity.z)
		}
	}
}
