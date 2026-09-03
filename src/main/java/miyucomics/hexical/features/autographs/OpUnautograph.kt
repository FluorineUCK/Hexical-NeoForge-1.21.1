package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.world.item.ItemStack

object OpUnautograph : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val stack = env.getHeldItemToOperateOn { ItemStackDataCompat.contains(it, "autographs") }
		if (stack == null)
			throw MishapBadOffhandItem.of(null, "autographed")
		return SpellAction.Result(Spell(stack.stack), MediaConstants.DUST_UNIT, listOf())
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			ItemStackDataCompat.update(stack) { it.remove("autographs") }

			val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack)
			if (hexHolder?.hasHex() == true)
				hexHolder.clearHex()

			val datumHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack)
			if (datumHolder != null && datumHolder.writeIota(null, true))
				datumHolder.writeIota(null, false)
		}
	}
}
