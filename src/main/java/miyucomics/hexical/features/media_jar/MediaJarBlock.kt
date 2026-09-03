package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.SoundType
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor

class MediaJarBlock : Block(
	Properties
		.of()
		.emissiveRendering { _, _, _ -> true }
		.lightLevel { _ -> 15 }
		.sound(SoundType.GLASS)
		.noOcclusion()
), EntityBlock, SimpleWaterloggedBlock {
	init {
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false))
	}

	override fun getShape(state: BlockState, view: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = Shapes.box(3.0 / 16, 0.0, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16)
	override fun newBlockEntity(pos: BlockPos, state: BlockState) = MediaJarBlockEntity(pos, state)

	override fun getStateForPlacement(itemPlacementContext: BlockPlaceContext): BlockState? {
		val fluidState = itemPlacementContext.level.getFluidState(itemPlacementContext.clickedPos)
		return this.defaultBlockState().setValue(LanternBlock.WATERLOGGED, fluidState.type === Fluids.WATER)
	}

	override fun playerWillDestroy(world: Level, blockPos: BlockPos, blockState: BlockState, playerEntity: Player): BlockState {
		val blockEntity = world.getBlockEntity(blockPos)
		if (blockEntity is MediaJarBlockEntity) {
			val itemStack = ItemStack(HexicalBlocks.MEDIA_JAR_ITEM)
			blockEntity.saveToItem(itemStack, world.registryAccess())
			val item = ItemEntity(world, blockPos.x.toDouble() + 0.5, blockPos.y.toDouble() + 0.5, blockPos.z.toDouble() + 0.5, itemStack)
			item.setDefaultPickUpDelay()
			world.addFreshEntity(item)
		}

		return super.playerWillDestroy(world, blockPos, blockState, playerEntity)
	}

	override fun useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult {
		val jar = world.getBlockEntity(pos) as MediaJarBlockEntity
		player.swing(hand)

		return when (val result = TransmutingHelper.transmuteItem(world, stack, jar.getMedia(), jar::insertMedia, jar::withdrawMedia)) {
			is TransmutationResult.AbsorbedMedia -> {
				world.playSound(null, player, HexicalSounds.AMETHYST_MELT, SoundSource.BLOCKS, 1f, 1f)
				ItemInteractionResult.sidedSuccess(world.isClientSide)
			}
			is TransmutationResult.TransmutedItems -> {
				world.playSound(null, pos, HexicalSounds.ITEM_DUNKS, SoundSource.BLOCKS, 1f, 1f)
				result.output.forEach(player::addItem)
				ItemInteractionResult.sidedSuccess(world.isClientSide)
			}
			is TransmutationResult.RefilledHolder -> {
				world.playSound(null, pos, HexicalSounds.ITEM_DUNKS, SoundSource.BLOCKS, 1f, 1f)
				ItemInteractionResult.sidedSuccess(world.isClientSide)
			}
			is TransmutationResult.Pass -> ItemInteractionResult.FAIL
		}
	}

	override fun updateShape(blockState: BlockState, direction: Direction, blockState2: BlockState, worldAccess: LevelAccessor, blockPos: BlockPos, blockPos2: BlockPos): BlockState {
		if (blockState.getValue(WATERLOGGED))
			worldAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccess))
		return super.updateShape(blockState, direction, blockState2, worldAccess, blockPos, blockPos2)
	}

	override fun getFluidState(blockState: BlockState): FluidState {
		if (blockState.getValue(WATERLOGGED))
			return Fluids.WATER.getSource(false)
		return super.getFluidState(blockState)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(WATERLOGGED)
	}

	companion object {
		val WATERLOGGED: BooleanProperty = BlockStateProperties.WATERLOGGED
		const val MAX_CAPACITY = MediaConstants.CRYSTAL_UNIT * 64
	}
}
