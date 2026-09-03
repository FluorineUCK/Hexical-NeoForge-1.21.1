package miyucomics.hexical.features.rotate

import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.Direction

interface BlockRotationHandler {
    fun canHandle(state: BlockState, direction: Direction): Boolean
    fun handle(state: BlockState, direction: Direction): BlockState
}