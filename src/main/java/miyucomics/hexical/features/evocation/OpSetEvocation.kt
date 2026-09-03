package miyucomics.hexical.features.evocation

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.entity.player.Player
import net.minecraft.server.level.ServerPlayer

object OpSetEvocation : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()
		CastingUtils.assertNoTruename(args[0], env)
		return SpellAction.Result(Spell(args.getList(0, argc).toList()), MediaConstants.CRYSTAL_UNIT, listOf())
	}

	private data class Spell(val hex: List<Iota>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			(env.castingEntity as Player).evocation = HexSerialization.serializeHex(hex)
		}
	}
}