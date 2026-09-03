package miyucomics.hexical.features.grok

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object OpGrokPush : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()

		val caster = env.castingEntity as ServerPlayer
		val newStack = args.getList(0, argc).map { if (MishapOthersName.getTrueNameMishapFromDatum(env.world, it, caster) == null) it else NullIota() }
		val newParenthesized = args.getList(1, argc).map { if (MishapOthersName.getTrueNameMishapFromDatum(env.world, it, caster) == null) it else NullIota() }

		return SpellAction.Result(Spell(IXplatAbstractions.INSTANCE.getStaffcastVM(caster, InteractionHand.MAIN_HAND).image.copy(
			stack = TreeList.from(newStack),
			parenthesized = TreeList.from(newParenthesized.map { CastingImage.ParenthesizedIota(it, true) })
		)), 0, listOf())
	}

	private data class Spell(val image: CastingImage) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			IXplatAbstractions.INSTANCE.setStaffcastImage(env.castingEntity as ServerPlayer, image)
		}
	}
}
