package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.component.DataComponents

object OpMageMouth : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()

		val wristpocket = (env.castingEntity as ServerPlayer).wristpocket
		if (wristpocket.`is`(Items.POTION) || wristpocket.`is`(Items.HONEY_BOTTLE) || wristpocket.`is`(Items.MILK_BUCKET) || wristpocket.has(DataComponents.FOOD))
			return SpellAction.Result(Spell(wristpocket), MediaConstants.DUST_UNIT, listOf())
		throw InedibleWristpocketMishap()
	}

	private data class Spell(val wristpocket: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			val original = caster.getItemInHand(env.castingHand)
			caster.setItemInHand(env.castingHand, wristpocket)
			val newStack = wristpocket.finishUsingItem(env.world, caster)
			caster.wristpocket = newStack
			caster.setItemInHand(env.castingHand, original)
		}
	}
}
