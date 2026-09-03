package miyucomics.hexical.features.sparkle

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

object OpSparkle : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)
		val color = args.getVec3(1, argc)
		val lifespan = args.getPositiveIntUnderInclusive(2, 100, argc)
		return SpellAction.Result(Spell(position, color, lifespan), MediaConstants.DUST_UNIT / 100, listOf())
	}

	private data class Spell(val position: Vec3, val color: Vec3, val lifespan: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			env.world.sendParticles(
				SparkleParticleEffect(
					Vector3f(
						color.x.toFloat(),
						color.y.toFloat(),
						color.z.toFloat()
					), lifespan
				), position.x, position.y, position.z, 1, 0.0, 0.0, 0.0, 0.0)
		}
	}
}