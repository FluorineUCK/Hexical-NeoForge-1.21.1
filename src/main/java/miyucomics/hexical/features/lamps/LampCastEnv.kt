package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

open class LampCastEnv(caster: ServerPlayer, castingHand: InteractionHand, private val finale: Boolean) : PackagedItemCastEnv(caster, castingHand) {
	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}
	override fun postExecution(result: CastResult) = super.postExecution(result.copy(cast = NullIota()))
	override fun getCastingHand(): InteractionHand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)
	fun getFinale() = finale
}