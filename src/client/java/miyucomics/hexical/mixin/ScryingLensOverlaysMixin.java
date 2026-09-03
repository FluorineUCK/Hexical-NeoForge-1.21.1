package miyucomics.hexical.mixin;

import at.petrak.hexcasting.client.render.ScryingLensOverlays;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScryingLensOverlays.class)
public class ScryingLensOverlaysMixin {
	@WrapOperation(method = "lambda$addScryingLensStuff$7", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private static boolean renderDefaultRedstoneLens(BlockState instance, Block block, Operation<Boolean> original) {
		return original.call(instance, block) || instance.is(HexicalBlocks.MAGE_BLOCK);
	}
}
