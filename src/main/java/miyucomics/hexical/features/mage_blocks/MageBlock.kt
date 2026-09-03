package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.features.mage_blocks.modifiers.BouncyModifier
import miyucomics.hexical.features.mage_blocks.modifiers.RedstoneModifier
import miyucomics.hexical.features.mage_blocks.modifiers.VolatileModifier
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.SoundType
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

class MageBlock : Block(Properties.of().noOcclusion().noLootTable().instabreak().mapColor(MapColor.NONE).isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false }.isValidSpawn { _, _, _, _ -> false }.sound(SoundType.AMETHYST_CLUSTER)), EntityBlock {
	override fun isSignalSource(state: BlockState) = true
	override fun getSignal(state: BlockState, world: BlockGetter, pos: BlockPos, direction: Direction): Int {
		val blockEntity = world.getBlockEntity(pos)
		if (blockEntity !is MageBlockEntity)
			return 0
		if (blockEntity.hasModifier(RedstoneModifier.TYPE))
			return blockEntity.getModifier(RedstoneModifier.TYPE).power
		return 0
	}


	override fun fallOn(world: Level, state: BlockState, pos: BlockPos, entity: Entity, fallDistance: Float) {
		val blockEntity = world.getBlockEntity(pos) as MageBlockEntity
		if (!blockEntity.hasModifier(BouncyModifier.TYPE))
			super.fallOn(world, state, pos, entity, fallDistance)
	}

	override fun updateEntityAfterFallOn(world: BlockGetter, entity: Entity) {
		val blockEntity = world.getBlockEntity(entity.blockPosition().offset(0, -1, 0))
		if (blockEntity !is MageBlockEntity)
			return
		if (blockEntity.hasModifier(BouncyModifier.TYPE)) {
			val velocity = entity.deltaMovement
			if (velocity.y < 0) {
				entity.setDeltaMovement(velocity.x, -velocity.y, velocity.z)
				entity.fallDistance = 0f
			}
		} else
			super.updateEntityAfterFallOn(world, entity)
	}

	override fun playerWillDestroy(world: Level, position: BlockPos, state: BlockState, player: Player): BlockState {
		destroyMageBlock(world, position, state)
		return super.playerWillDestroy(world, position, state, player)
	}

	fun destroyMageBlock(world: Level, position: BlockPos, state: BlockState) {
		val blockEntity = world.getBlockEntity(position) as? MageBlockEntity ?: return
		world.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState())

		if (blockEntity.hasModifier(VolatileModifier.TYPE)) {
			for (offset in Direction.stream()) {
				val positionToTest = position.offset(offset.normal)
				val otherState = world.getBlockState(positionToTest)
				val block = otherState.block
				if (block == HexicalBlocks.MAGE_BLOCK)
					destroyMageBlock(world, positionToTest, otherState)
			}
		}
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState) = MageBlockEntity(pos, state)
	override fun <T : BlockEntity> getTicker(pworld: Level, pstate: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> = BlockEntityTicker { world, position, state, blockEntity ->
		(blockEntity as MageBlockEntity).modifiers.forEach { it -> it.value.tick(world, position, state) }
	}

	// defer shapes to disguise
	private fun getBlockDisguise(world: BlockGetter, pos: BlockPos): BlockState? = (world.getBlockEntity(pos) as? MageBlockEntity)?.disguise

	override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		getBlockDisguise(world, pos)?.getShape(world, pos, context) ?: Shapes.block()

	override fun getOcclusionShape(state: BlockState, world: BlockGetter, pos: BlockPos): VoxelShape =
		getBlockDisguise(world, pos)?.getOcclusionShape(world, pos) ?: Shapes.block()

	override fun getInteractionShape(state: BlockState, world: BlockGetter, pos: BlockPos): VoxelShape =
		getBlockDisguise(world, pos)?.getInteractionShape(world, pos) ?: Shapes.block()

	override fun getCollisionShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		getBlockDisguise(world, pos)?.getCollisionShape(world, pos, context) ?: Shapes.block()

	override fun getVisualShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		getBlockDisguise(world, pos)?.getVisualShape(world, pos, context) ?: Shapes.block()
}
