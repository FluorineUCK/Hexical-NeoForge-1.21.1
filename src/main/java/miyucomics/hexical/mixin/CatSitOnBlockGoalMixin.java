package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CatSitOnBlockGoal.class)
class CatSitOnBlockGoalMixin {
	@Inject(method = "isValidTarget(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"), cancellable = true)
	void sits(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue())
			return;
		if (!world.isEmptyBlock(pos.above())) {
			cir.setReturnValue(false);
			return;
		}
		BlockState state = world.getBlockState(pos);
		if (state.is(HexicalBlocks.SENTINEL_BED_BLOCK) && state.getValue(DirectionalBlock.FACING) == Direction.UP)
			cir.setReturnValue(true);
	}
}
