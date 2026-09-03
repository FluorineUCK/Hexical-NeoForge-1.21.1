package miyucomics.hexical.features.conjure

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.FlowerPotBlock
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.BlockTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

object OpConjureFlower : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getBlockPos(0, argc)
		env.assertPosInRange(position)

		val id = args.getIdentifier(1, argc)
		if (!BuiltInRegistries.BLOCK.containsKey(id))
			throw MishapInvalidIota.of(args[1], 0, "conjurable_flower_id")
		val type = BuiltInRegistries.BLOCK.get(id)
		if (!type.defaultBlockState().`is`(HexicalBlocks.CONJURABLE_FLOWERS))
			throw MishapInvalidIota.of(args[1], 0, "conjurable_flower_id")

		if (env.world.getBlockState(position).`is`(BlockTags.FLOWER_POTS))
			return SpellAction.Result(PotPlant(position, type), MediaConstants.DUST_UNIT / 4, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))

		if (!env.world.getBlockState(position).canBeReplaced())
			throw MishapBadBlock.of(position, "flower_spawnable")
		if (type.defaultBlockState().properties.contains(DoublePlantBlock.HALF) && !env.world.getBlockState(position.above()).canBeReplaced())
			throw MishapBadBlock.of(position.above(), "flower_spawnable")
		if (!env.world.getBlockState(position.below()).isFaceSturdy(env.world, position.below(), Direction.UP))
			throw MishapBadBlock.of(position.below(), "solid_platform")

		return SpellAction.Result(GroundPlant(position, type), MediaConstants.DUST_UNIT / 4, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
	}

	private data class GroundPlant(val position: BlockPos, val flower: Block) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (flower is DoublePlantBlock) {
				DoublePlantBlock.placeAt(env.world, flower.defaultBlockState(), position, Block.UPDATE_CLIENTS or Block.UPDATE_KNOWN_SHAPE)
			}
			else {
				env.world.setBlockAndUpdate(position, flower.defaultBlockState())
			}
		}
	}

	private data class PotPlant(val position: BlockPos, val flower: Block) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val currentPot = env.world.getBlockState(position).block as? FlowerPotBlock ?: return
			val pot = currentPot.emptyPot.fullPotsView[BuiltInRegistries.BLOCK.getKey(flower)]?.get() ?: return
			env.world.setBlockAndUpdate(position, pot.defaultBlockState())
		}
	}
}
