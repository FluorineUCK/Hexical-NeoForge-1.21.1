package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

object OpMageHand : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is ServerPlayer)
			throw MishapBadCaster()

		val wristpocket = WristpocketUtils.getWristpocketStack(env) ?: throw NeedsWristpocketMishap()

		when (val iota = args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(env.world, 0, argc)
				env.assertEntityInRange(entity)
				return SpellAction.Result(EntitySpell(entity, wristpocket), MediaConstants.DUST_UNIT, listOf())
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				return SpellAction.Result(BlockSpell(position, wristpocket), MediaConstants.DUST_UNIT, listOf())
			}
			else -> throw MishapInvalidIota.of(iota, 0, "entity_or_vector")
		}
	}

	private data class BlockSpell(val position: BlockPos, val wristpocket: ItemStack) : RenderedSpell {
			override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			val originalItem = caster.getItemInHand(env.castingHand)

			caster.setItemInHand(env.castingHand, wristpocket)
			val block = env.world.getBlockState(position)
			val hit = BlockHitResult(Vec3.atCenterOf(position), Direction.UP, position, false)
			val result = block.useItemOn(wristpocket, env.world, caster, env.castingHand, hit)
			if (!result.consumesAction())
				wristpocket.useOn(UseOnContext(caster, env.castingHand, hit))

			WristpocketUtils.setWristpocketStack(env, caster.getItemInHand(env.castingHand))
			caster.setItemInHand(env.castingHand, originalItem)
		}
	}

	private data class EntitySpell(val entity: Entity, val wristpocket: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			val originalItem = caster.getItemInHand(env.castingHand)

			caster.setItemInHand(env.castingHand, wristpocket)
			val result = entity.interact(caster, env.castingHand)
			if (!result.consumesAction() && entity is LivingEntity)
				wristpocket.interactLivingEntity(caster, entity, env.castingHand)

			WristpocketUtils.setWristpocketStack(env, caster.getItemInHand(env.castingHand))
			caster.setItemInHand(env.castingHand, originalItem)
		}
	}
}
