package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.BlockPos

abstract class PrestidigitationHandlerBlock : PrestidigitationHandler {
	abstract fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean
	abstract fun affect(env: CastingEnvironment, pos: BlockPos)

	companion object {
		fun getBlockState(env: CastingEnvironment, pos: BlockPos): BlockState = env.world.getBlockState(pos)
		fun getBlock(env: CastingEnvironment, pos: BlockPos): Block = this.getBlockState(env, pos).block
		fun setBlockState(env: CastingEnvironment, pos: BlockPos, state: BlockState) {
			env.world.setBlockAndUpdate(pos, state)
			env.world.updateNeighborsAt(pos, state.block)
		}
	}
}