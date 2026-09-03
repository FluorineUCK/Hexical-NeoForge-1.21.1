package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.ColorProvider
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.block.SoundType
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.gameevent.GameEvent

class HexCandleBlock : CandleBlock(
	Properties.of()
		.mapColor(MapColor.COLOR_PURPLE)
		.noOcclusion()
		.strength(0.1f)
		.sound(SoundType.CANDLE)
		.lightLevel(LIGHT_EMISSION)
		.pushReaction(PushReaction.DESTROY)
), EntityBlock {
	override fun useItemOn(stack: ItemStack, state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, hit: BlockHitResult): ItemInteractionResult {
		if (player.isShiftKeyDown)
			return super.useItemOn(stack, state, world, pos, player, hand, hit)
		if (!state.getValue(AbstractCandleBlock.LIT))
			return super.useItemOn(stack, state, world, pos, player, hand, hit)

		val candle = (world.getBlockEntity(pos)!! as HexCandleBlockEntity)
		if (IXplatAbstractions.INSTANCE.isPigment(stack))
			candle.setPigment(FrozenPigment(stack.copy(), player.uuid))
		else
			candle.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
		world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
		return ItemInteractionResult.sidedSuccess(world.isClientSide)
	}

	override fun useWithoutItem(state: BlockState, world: Level, pos: BlockPos, player: Player, hit: BlockHitResult): InteractionResult {
		if (player.isShiftKeyDown || !state.getValue(AbstractCandleBlock.LIT))
			return super.useWithoutItem(state, world, pos, player, hit)
		(world.getBlockEntity(pos) as? HexCandleBlockEntity)?.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
		world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
		return InteractionResult.sidedSuccess(world.isClientSide)
	}

	override fun animateTick(state: BlockState, world: Level, pos: BlockPos, random: RandomSource) {
		if (!state.getValue(AbstractCandleBlock.LIT))
			return

		val blockEntity = world.getBlockEntity(pos)
		if (blockEntity !is HexCandleBlockEntity)
			return

		val colorProvider = blockEntity.getPigment().colorProvider
		getParticleOffsets(state).forEach { offset: Vec3 -> spawnCandleParticles(world, Vec3.atLowerCornerOf(pos).add(offset), random, colorProvider) }
	}

	override fun placeLiquid(worldAccess: LevelAccessor, blockPos: BlockPos, blockState: BlockState, fluidState: FluidState): Boolean {
		if (blockState.getValue(WATERLOGGED) || fluidState.type !== Fluids.WATER)
			return false
		worldAccess.setBlock(blockPos, blockState.setValue(WATERLOGGED, true), 3)
		worldAccess.scheduleTick(blockPos, fluidState.type, fluidState.type.getTickDelay(worldAccess))
		return true
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HexCandleBlockEntity(pos, state)

	companion object {
		fun spawnCandleParticles(world: Level, position: Vec3, random: RandomSource, colorProvider: ColorProvider) {
			if (random.nextFloat() < 0.17f)
				world.playLocalSound(position.x + 0.5, position.y + 0.5, position.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, true)
			world.addParticle(
				ConjureParticleOptions(colorProvider.getColor(world.gameTime.toFloat(), position)),
				position.x, position.y, position.z,
				0.0, world.random.nextFloat() * 0.02, 0.0
			)
		}
	}
}
