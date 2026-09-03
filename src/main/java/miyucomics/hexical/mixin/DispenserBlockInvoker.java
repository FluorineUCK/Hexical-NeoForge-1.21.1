package miyucomics.hexical.mixin;

import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DispenserBlock.class)
public interface DispenserBlockInvoker {
	@Invoker("getDispenseMethod")
	DispenseItemBehavior invokeGetDispenseMethod(Level level, ItemStack item);

	@Invoker("dispenseFrom")
	void invokeDispenseFrom(ServerLevel level, BlockState state, BlockPos pos);
}
