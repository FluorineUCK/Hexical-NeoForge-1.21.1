package miyucomics.hexical.mixin;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockItem.class)
public class BlockItemMixin {
	@Inject(method = "setBlockEntityData", at = @At("RETURN"))
    private static void stripPedestalUUID(ItemStack stack, BlockEntityType<?> blockEntityType, CompoundTag tag, CallbackInfo ci) {
		CustomData.update(DataComponents.BLOCK_ENTITY_DATA, stack, beTag -> beTag.remove("persistent_uuid"));
    }
}
