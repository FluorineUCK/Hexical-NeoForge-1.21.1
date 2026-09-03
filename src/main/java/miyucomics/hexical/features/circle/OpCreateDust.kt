package miyucomics.hexical.features.circle

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

object OpCreateDust : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env !is CircleCastEnv)
			throw MishapNoSpellCircle()

		val circle = env.impetus ?: throw MishapNoSpellCircle()
		val bounds = circle.executionState!!.bounds

		val position = args.getVec3(0, argc)
		if (!bounds.aabb().contains(position))
			throw OutsideCircleMishap()
		val amount = args.getPositiveIntUnderInclusive(1, 64, argc)

		return SpellAction.Result(Spell(position, amount), (MediaConstants.DUST_UNIT * amount * 1.1).toLong(), listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val amount: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val item = ItemStack(HexItems.AMETHYST_DUST.get(), amount)
			env.world.addFreshEntity(ItemEntity(env.world, position.x, position.y, position.z, item))
		}
	}
}
