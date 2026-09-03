package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import kotlin.math.min

object OpRechargeLamp : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val stack = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.HAND_LAMP_ITEM) || stack.`is`(HexicalItems.ARCH_LAMP_ITEM) }
		if (stack == null)
			throw MishapBadOffhandItem.of(null, "lamp")

		val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack.stack)!!
		val leftToFull = 200000 * MediaConstants.DUST_UNIT - mediaHolder.media
		val battery = min(leftToFull.toDouble(), args.getPositiveDoubleUnderInclusive(0, 200000.0, argc))

		return SpellAction.Result(Spell((battery * MediaConstants.DUST_UNIT).toInt(), stack.stack), (battery * MediaConstants.DUST_UNIT).toLong(), listOf())
	}

	private data class Spell(val battery: Int, val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack)!!
			hexHolder.writeHex(hexHolder.getHex(env.world) ?: listOf(), null, IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!.media + battery)

			if (env.castingEntity is ServerPlayer)
				HexicalAdvancements.RELOAD_LAMP.trigger(env.castingEntity as ServerPlayer)
		}
	}
}