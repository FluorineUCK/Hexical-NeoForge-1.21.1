package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

class HextitoCastEnv(caster: ServerPlayer, castingHand: InteractionHand) : PlayerBasedCastEnv(caster, castingHand) {
	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}

	override fun getCastingHand(): InteractionHand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

	public override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
		if (caster.isCreative)
			return 0
		return this.extractMediaFromInventory(costLeft, this.canOvercast(), simulate)
	}
}