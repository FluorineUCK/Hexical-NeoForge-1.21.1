package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

object OpSleight : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val wristpocket = WristpocketUtils.getWristpocketStack(env) ?: throw NeedsWristpocketMishap()

		when (args[0]) {
			is EntityIota -> {
				val item = args.getItemEntity(env.world, 0, argc)
				env.assertEntityInRange(item)
				return SpellAction.Result(SwapSpell(item, wristpocket), MediaConstants.DUST_UNIT / 4, listOf(ParticleSpray.burst(item.position(), 1.0)))
			}
			is Vec3Iota -> {
				val position = args.getVec3(0, argc)
				env.assertVecInRange(position)
				return SpellAction.Result(ConjureSpell(position, wristpocket), MediaConstants.DUST_UNIT / 4, listOf(ParticleSpray.burst(position, 1.0)))
			}
			else -> throw MishapInvalidIota.of(args[0], 0, "entity_or_vector")
		}
	}

	private data class ConjureSpell(val position: Vec3, val wristpocket: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (!wristpocket.isEmpty)
				env.world.addFreshEntity(ItemEntity(env.world, position.x, position.y, position.z, wristpocket))
			WristpocketUtils.setWristpocketStack(env, ItemStack.EMPTY)
		}
	}

	private data class SwapSpell(val item: ItemEntity, val wristpocket: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			WristpocketUtils.setWristpocketStack(env, item.item)
			if (!wristpocket.isEmpty)
				item.item = wristpocket
			else
				item.discard()
		}
	}
}