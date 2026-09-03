package miyucomics.hexical.features.pedestal

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent
import at.petrak.hexcasting.api.casting.circles.ICircleComponent.ControlFlow
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.InteractionHand
import net.minecraft.world.Containers
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import java.util.*

class PedestalBlock : BlockCircleComponent(Properties.ofFullCopy(Blocks.DEEPSLATE_TILES).strength(4f, 4f)), EntityBlock {
	init {
		registerDefaultState(stateDefinition.any()
			.setValue(ENERGIZED, false)
			.setValue(FACING, Direction.NORTH))
	}

	override fun setPlacedBy(world: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		(world.getBlockEntity(pos) as PedestalBlockEntity).onBlockPlace()
		super.setPlacedBy(world, pos, state, placer, stack)
	}

	override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, moved: Boolean) {
		if (state.`is`(newState.block))
			return
		val pedestal = world.getBlockEntity(pos)
		if (pedestal is PedestalBlockEntity) {
			Containers.dropContents(world, pos, pedestal)
			world.updateNeighbourForOutputSignal(pos, this)
			pedestal.onBlockBreak()
		}
		super.onRemove(state, world, pos, newState, moved)
	}

	override fun isPathfindable(state: BlockState, navigationType: PathComputationType) = false

	override fun useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult {
		val pedestal = world.getBlockEntity(pos)
		if (pedestal is PedestalBlockEntity) {
			pedestal.onUse(player, hand)
			return ItemInteractionResult.sidedSuccess(world.isClientSide)
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
	}

	override fun useWithoutItem(state: BlockState, world: Level, pos: BlockPos, player: Player, hit: BlockHitResult): InteractionResult {
		val pedestal = world.getBlockEntity(pos) as? PedestalBlockEntity ?: return InteractionResult.PASS
		pedestal.onUse(player, InteractionHand.MAIN_HAND)
		return InteractionResult.sidedSuccess(world.isClientSide)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		super.createBlockStateDefinition(builder)
		builder.add(FACING)
	}

	override fun canEnterFromDirection(enterDir: Direction, pos: BlockPos, state: BlockState, world: ServerLevel) = enterDir != this.normalDir(pos, state, world).opposite

	override fun acceptControlFlow(image: CastingImage, env: CircleCastEnv, enterDir: Direction, pos: BlockPos, state: BlockState, world: ServerLevel): ControlFlow {
		val exits = this.possibleExitDirections(pos, state, world)
		exits.remove(enterDir.opposite)
		return ControlFlow.Continue((world.getBlockEntity(pos) as PedestalBlockEntity).modifyImage(image), exits.map { dir -> exitPositionFromDirection(pos, dir) })
	}

	override fun possibleExitDirections(pos: BlockPos, state: BlockState, world: Level): EnumSet<Direction> {
		val exits = EnumSet.allOf(Direction::class.java)
		val normal = this.normalDir(pos, state, world)
		exits.remove(normal)
		exits.remove(normal.opposite)
		return exits
	}

	override fun particleHeight(pos: BlockPos, state: BlockState, world: Level) = PedestalBlockEntity.HEIGHT.toFloat()
	override fun normalDir(pos: BlockPos, state: BlockState, world: Level, recursionLeft: Int): Direction = state.getValue(FACING)

	override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState = super.getStateForPlacement(ctx)!!.setValue(FACING, ctx.clickedFace)
	override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = SHAPES.getValue(state.getValue(FACING))

	override fun getAnalogOutputSignal(state: BlockState, world: Level, pos: BlockPos): Int {
		val blockEntity = world.getBlockEntity(pos)
		if (blockEntity !is PedestalBlockEntity)
			return 0
		return AbstractContainerMenu.getRedstoneSignalFromContainer(blockEntity as Container)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState) = PedestalBlockEntity(pos, state)
	override fun <T : BlockEntity> getTicker(world: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker { world1, _, _, blockEntity -> (blockEntity as PedestalBlockEntity).tick(world1) }

	companion object {
		val FACING: DirectionProperty = BlockStateProperties.FACING
		val SHAPES = EnumMap<Direction, VoxelShape>(Direction::class.java).apply {
			put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.25, 1.0, 1.0, 1.0))
			put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.75))
			put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.75, 1.0, 1.0))
			put(Direction.WEST, Shapes.box(0.25, 0.0, 0.0, 1.0, 1.0, 1.0))
			put(Direction.UP, Shapes.box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0))
			put(Direction.DOWN, Shapes.box(0.0, 0.25, 0.0, 1.0, 1.0, 1.0))
		}
	}
}
