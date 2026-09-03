package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.*
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.StandingSignBlock
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.item.BlockItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.core.BlockPos

object OpGetDye : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment) = listOf(
		when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(env.world, 0, argc)
				env.assertEntityInRange(entity)
				processEntity(entity)
			}
			is IdentifierIota -> {
				when (val item = BuiltInRegistries.ITEM.get(args.getIdentifier(0, argc))) {
					is BlockItem -> getDyeFromBlock(item.block)
					else -> {
						if (DyeDataHook.getDye(item) != null)
							DyeIota(DyeDataHook.getDye(item)!!)
						else
							NullIota()
					}
				}
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				processVec3d(position, env.world)
			}
			else -> NullIota()
		}
	)

	private fun processEntity(entity: Entity): Iota {
		return when (entity) {
			is Cat -> DyeIota(DyeOption.fromDyeColor(entity.collarColor))
			is ItemEntity -> {
				when (val item = entity.item.item) {
					is BlockItem -> getDyeFromBlock(item.block)
					else -> {
						if (DyeDataHook.getDye(item) != null)
							DyeIota(DyeDataHook.getDye(item)!!)
						else
							NullIota()
					}
				}
			}
			is Sheep -> DyeIota(DyeOption.fromDyeColor(entity.color))
			is Shulker -> DyeIota(DyeOption.fromDyeColor(entity.color))
			is Wolf -> DyeIota(DyeOption.fromDyeColor(entity.collarColor))
			else -> NullIota()
		}
	}

	private fun processVec3d(position: BlockPos, world: ServerLevel): Iota {
		val state = world.getBlockState(position)
		if (state.block is StandingSignBlock) {
			val sign = world.getBlockEntity(position) as SignBlockEntity
			return ListIota(listOf(DyeIota(DyeOption.fromDyeColor(sign.frontText.color)), DyeIota(DyeOption.fromDyeColor(sign.backText.color))))
		}
		return getDyeFromBlock(world.getBlockState(position).block)
	}

	private fun getDyeFromBlock(block: Block): Iota {
		val dye = DyeDataHook.getDye(block) ?: return NullIota()
		return DyeIota(dye)
	}
}