package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer

object OpWish : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		CastingUtils.assertNoTruename(args[0], env)
		val stack = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.HAND_LAMP_ITEM) || stack.`is`(HexicalItems.ARCH_LAMP_ITEM) }
		if (stack == null)
			throw MishapBadOffhandItem.of(null, "lamp")
		return SpellAction.Result(Spell(args.getList(0, argc).toList(), stack.stack), 0, listOf())
	}

	private data class Spell(val patterns: List<Iota>, val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			IXplatAbstractions.INSTANCE.findHexHolder(stack)?.writeHex(patterns, null, IXplatAbstractions.INSTANCE.findMediaHolder(stack)?.media!!)
			if (env.castingEntity is ServerPlayer)
				HexicalAdvancements.EDUCATE_GENIE.trigger(env.castingEntity as ServerPlayer)
		}
	}
}