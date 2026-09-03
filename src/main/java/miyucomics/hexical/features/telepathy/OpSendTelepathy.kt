package miyucomics.hexical.features.telepathy

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer

object OpSendTelepathy : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			throw MishapBadCaster()
		caster.displayClientMessage(args[0].display(), true)
		return emptyList()
	}
}