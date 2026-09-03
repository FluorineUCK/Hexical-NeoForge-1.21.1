package miyucomics.hexical.features.rotate

import miyucomics.hexical.misc.InitHook
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction

object BlockRotationHandlerRegistry : InitHook() {
    private val handlers = mutableListOf<BlockRotationHandler>()

    fun modify(state: BlockState, direction: Direction) = handlers.firstOrNull { it.canHandle(state, direction) }?.handle(state, direction)

    override fun init() {
        register(object : BlockRotationHandler {
            override fun canHandle(state: BlockState, direction: Direction) = state.properties.contains(BlockStateProperties.AXIS)
            override fun handle(state: BlockState, direction: Direction) = state.setValue(BlockStateProperties.AXIS, direction.axis)
        })

        register(object : BlockRotationHandler {
            override fun canHandle(state: BlockState, direction: Direction) = state.properties.contains(BlockStateProperties.FACING)
            override fun handle(state: BlockState, direction: Direction) = state.setValue(BlockStateProperties.FACING, direction)
        })

        register(object : BlockRotationHandler {
            override fun canHandle(state: BlockState, direction: Direction) = state.properties.contains(BlockStateProperties.FACING_HOPPER) && direction != Direction.UP
            override fun handle(state: BlockState, direction: Direction) = state.setValue(BlockStateProperties.FACING_HOPPER, direction)
        })

        register(object : BlockRotationHandler {
            override fun canHandle(state: BlockState, direction: Direction) = state.properties.contains(BlockStateProperties.HORIZONTAL_FACING) && direction != Direction.UP  && direction != Direction.DOWN
            override fun handle(state: BlockState, direction: Direction) = state.setValue(BlockStateProperties.HORIZONTAL_FACING, direction)
        })

        register(object : BlockRotationHandler {
            override fun canHandle(state: BlockState, direction: Direction) = state.properties.contains(BlockStateProperties.VERTICAL_DIRECTION) && !(direction == Direction.UP || direction == Direction.DOWN)
            override fun handle(state: BlockState, direction: Direction) = state.setValue(BlockStateProperties.VERTICAL_DIRECTION, direction)
        })
    }

    fun register(handler: BlockRotationHandler) {
        handlers += handler
    }
}