package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.Entity
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpPrestidigitation : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		return when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(env.world, 0, argc)
				env.assertEntityInRange(entity)
				val handler = PrestidigitationHandlersHook.PRESTIDIGITATION_HANDLER.firstOrNull { it is PrestidigitationHandlerEntity<*> && it.canAffectEntity(env, entity) } ?: throw MishapNoPrestidigitation(entity.eyePosition)
				SpellAction.Result(EntitySpell(entity, handler as PrestidigitationHandlerEntity<*>), MediaConstants.DUST_UNIT / 10, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
			}
			is Vec3Iota -> {
				val pos = args.getBlockPos(0, argc)
				env.assertPosInRange(pos)
				val handler = PrestidigitationHandlersHook.PRESTIDIGITATION_HANDLER.firstOrNull { it is PrestidigitationHandlerBlock && it.canAffectBlock(env, pos) } ?: throw MishapNoPrestidigitation(Vec3.atCenterOf(pos))
				SpellAction.Result(BlockSpell(pos, handler as PrestidigitationHandlerBlock), MediaConstants.DUST_UNIT / 10, listOf(ParticleSpray.cloud(Vec3.atCenterOf(pos), 1.0)))
			}
			else -> throw MishapInvalidIota.of(args[0], 0, "entity_or_vector")
		}
	}

	private data class BlockSpell(val pos: BlockPos, val handler: PrestidigitationHandlerBlock) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			handler.affect(env, pos)
		}
	}

	private data class EntitySpell(val entity: Entity, val handler: PrestidigitationHandlerEntity<*>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			handler.affectSafely(env, entity)
		}
	}
}