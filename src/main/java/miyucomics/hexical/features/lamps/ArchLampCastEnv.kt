package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

class ArchLampCastEnv(caster: ServerPlayer, castingHand: InteractionHand, finale: Boolean, stack: ItemStack) : LampCastEnv(caster, castingHand, finale) {
	private var mediaHolder: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!

	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}
	override fun postExecution(result: CastResult) = super.postExecution(result.copy(cast = NullIota()))
	override fun getCastingHand(): InteractionHand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

	override fun extractMediaEnvironment(costLeft: Long, simulate: Boolean): Long {
		if (caster.isCreative)
			return 0
		return costLeft - mediaHolder.withdrawMedia(costLeft.toInt().toLong(), simulate)
	}
}