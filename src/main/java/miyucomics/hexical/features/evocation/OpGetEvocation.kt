package miyucomics.hexical.features.evocation

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.entity.player.Player
import net.minecraft.server.level.ServerPlayer

object OpGetEvocation : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()
		val deserialized = HexSerialization.deserializeHex((env.castingEntity as Player).evocation, env.world)
		return listOf(ListIota(deserialized))
	}
}