package miyucomics.hexical.features.dyes

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
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.features.specklikes.Specklike
import miyucomics.hexical.hexcompat.HexPigmentCompat
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.item.DyeColor
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import java.util.*
import net.minecraft.nbt.CompoundTag

object OpDye : SpellAction {
	override val argc = 2
	private const val COST = MediaConstants.DUST_UNIT / 8
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val dye = args.getDye(1, argc)
		when (args[0]) {
			is EntityIota -> {
				val entity = args.getEntity(env.world, 0, argc)
				env.assertEntityInRange(entity)
				return when (entity) {
					is Cat -> {
						val trueDye = args.getColoredDye(1, argc)
						SpellAction.Result(CatSpell(entity, trueDye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Sheep -> {
						val trueDye = args.getColoredDye(1, argc)
						SpellAction.Result(SheepSpell(entity, trueDye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Shulker -> {
						val trueDye = args.getColoredDye(1, argc)
						SpellAction.Result(ShulkerSpell(entity, trueDye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Specklike -> {
						val trueDye = args.getColoredDye(1, argc)
						SpellAction.Result(SpecklikeSpell(entity, trueDye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is Wolf -> {
						val trueDye = args.getColoredDye(1, argc)
						SpellAction.Result(WolfSpell(entity, trueDye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
					}
					is ItemEntity -> {
						when (val item = entity.item.item) {
							is BlockItem -> {
								if (DyeDataHook.isDyeable(item.block))
									SpellAction.Result(BlockItemSpell(entity, item.block, dye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
								else
									throw DyeableMishap(entity.position())
							}
							else -> {
								if (DyeDataHook.isDyeable(item))
									SpellAction.Result(ItemSpell(entity, item, dye), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
								else
									throw DyeableMishap(entity.position())
							}
						}
					}
					else -> throw DyeableMishap(entity.position())
				}
			}
			is Vec3Iota -> {
				val position = args.getBlockPos(0, argc)
				env.assertPosInRange(position)
				val state = env.world.getBlockState(position)
				if (!DyeDataHook.isDyeable(state.block))
					throw DyeableMishap(position.getCenter())
				return SpellAction.Result(BlockSpell(position, state, dye), COST, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
			}
			else -> throw MishapInvalidIota.of(args[0], 1, "entity_or_vector")
		}
	}

	private data class BlockSpell(val position: BlockPos, val state: BlockState, val dye: DyeOption) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			var newState = DyeDataHook.getNewBlock(state.block, dye)!!
			state.properties.filter(newState.properties::contains).forEach { property ->
				@Suppress("UNCHECKED_CAST")
				val typedProperty: Property<Comparable<Any>> = property as Property<Comparable<Any>>
				newState = newState.setValue(typedProperty, state.getValue(typedProperty))
			}
			env.world.setBlockAndUpdate(position, newState)
		}
	}

	private data class BlockItemSpell(val item: ItemEntity, val block: Block, val dye: DyeOption) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			item.item = item.item.transmuteCopy(DyeDataHook.getNewBlock(block, dye)!!.block.asItem(), item.item.count)
		}
	}

	private data class CatSpell(val cat: Cat, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			setCollarColor(cat, dye)
		}
	}

	private data class ItemSpell(val entity: ItemEntity, val item: Item, val dye: DyeOption) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val dyedItem = checkNotNull(DyeDataHook.getNewItem(item, dye)) { "Dyeable item lost its dye mapping: $item / $dye" }
			entity.item = entity.item.transmuteCopy(dyedItem, entity.item.count)
		}
	}

	private data class SheepSpell(val sheep: Sheep, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			sheep.color = dye
		}
	}

	private data class ShulkerSpell(val shulker: Shulker, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			shulker.variant = Optional.of(dye)
		}
	}

	private data class SpecklikeSpell(val speck: Specklike, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			speck.setPigment(FrozenPigment(ItemStack(HexPigmentCompat.dyePigmentItem(dye)), env.castingEntity!!.uuid))
		}
	}

	private data class WolfSpell(val wolf: Wolf, val dye: DyeColor) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			setCollarColor(wolf, dye)
		}
	}

	private fun setCollarColor(entity: net.minecraft.world.entity.Entity, dye: DyeColor) {
		val data = entity.saveWithoutId(CompoundTag())
		data.putByte("CollarColor", dye.id.toByte())
		entity.load(data)
	}
}
