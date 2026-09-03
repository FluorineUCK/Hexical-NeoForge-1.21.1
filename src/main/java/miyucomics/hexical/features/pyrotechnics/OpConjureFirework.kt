package miyucomics.hexical.features.pyrotechnics

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import it.unimi.dsi.fastutil.ints.IntArrayList

object OpConjureFirework : SpellAction {
	override val argc = 8
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)

		val velocity = args.getVec3(1, argc)
		val duration = args.getIntBetween(2, 1, 3, argc)
		val shape = args.getPositiveIntUnderInclusive(3, 4, argc)

		val colors = args.getList(4, argc)
		if (colors.isEmpty())
			throw MishapInvalidIota.of(args[3], 4, "nonempty_list")
		val trueColors = colors.map {
			if (it !is Vec3Iota)
				throw MishapInvalidIota.of(args[3], 4, "vector_list")
			translateVectorToColor(it.vec3)
		}

		val fades = args.getList(5, argc).map {
			if (it !is Vec3Iota)
				throw MishapInvalidIota.of(args[3], 4, "vector_list")
			translateVectorToColor(it.vec3)
		}

		val flicker = args.getBool(6, argc)
		val trail = args.getBool(7, argc)

		return SpellAction.Result(Spell(position, velocity, duration, shape, trueColors, fades, flicker, trail), MediaConstants.SHARD_UNIT, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val velocity: Vec3, val duration: Int, val shape: Int, val colors: List<Int>, val fades: List<Int>, val flicker: Boolean, val trail: Boolean) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val explosion = FireworkExplosion(
				FireworkExplosion.Shape.byId(shape),
				IntArrayList(colors),
				IntArrayList(fades),
				trail,
				flicker
			)
			val fireworkStack = ItemStack(Items.FIREWORK_ROCKET)
			fireworkStack.set(DataComponents.FIREWORKS, Fireworks(duration, listOf(explosion)))

			env.world.addFreshEntity(FireworkRocketEntity(env.world, fireworkStack, position.x, position.y, position.z, true).apply {
				setDeltaMovement(velocity.x, velocity.y, velocity.z)
			})
		}
	}

	private fun translateVectorToColor(vector: Vec3): Int {
		return (vector.x.coerceIn(0.0, 1.0) * 255).toInt() shl 16 or
		(vector.y.coerceIn(0.0, 1.0) * 255).toInt() shl 8 or
		(vector.z.coerceIn(0.0, 1.0) * 255).toInt()
	}
}
