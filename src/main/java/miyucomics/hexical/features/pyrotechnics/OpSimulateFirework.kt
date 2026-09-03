package miyucomics.hexical.features.pyrotechnics

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks

object OpSimulateFirework : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		val duration = args.getIntBetween(2, 1, 3, argc)
		env.assertVecInRange(position)

		val fireworkStar = env.getHeldItemToOperateOn { it.`is`(Items.FIREWORK_STAR) }
		if (fireworkStar == null)
			throw MishapBadOffhandItem.of(null, "firework_star")

		val explosion = fireworkStar.stack.getOrDefault(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT)
		return SpellAction.Result(Spell(position, args.getVec3(1, argc), duration, explosion), MediaConstants.SHARD_UNIT, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val velocity: Vec3, val duration: Int, val template: FireworkExplosion) :
		RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val stack = ItemStack(Items.FIREWORK_ROCKET)
			stack.set(DataComponents.FIREWORKS, Fireworks(duration, listOf(template)))

			val firework = FireworkRocketEntity(env.world, stack, position.x, position.y, position.z, true)
			firework.setDeltaMovement(velocity.x, velocity.y, velocity.z)
			env.world.addFreshEntity(firework)
		}
	}
}
