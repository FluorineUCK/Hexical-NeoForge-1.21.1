package miyucomics.hexical.features.conjure

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

class OpConjureEntity(val cost: Long, private val instantiate: (world: ServerLevel, position: Vec3, caster: LivingEntity?) -> Entity) : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)
		return SpellAction.Result(Spell(position, instantiate), cost, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val instantiate: (world: ServerLevel, position: Vec3, caster: LivingEntity?) -> Entity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
			val entity = instantiate(env.world, position, env.castingEntity)
			env.world.addFreshEntity(entity)
			return image.copy(stack = image.stack.appended(EntityIota(entity)))
		}
	}
}
