package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.utils.MediaHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true)
	public void addCharmedMediaDisplay(CallbackInfoReturnable<Boolean> cir) {
		CustomData data = ((ItemStack) (Object) this).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if (data.contains("charmed"))
			cir.setReturnValue(true);
	}

	@Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true)
	public void addCharmedMediaStep(CallbackInfoReturnable<Integer> cir) {
		ItemStack stack = ((ItemStack) (Object) this);
		CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if (nbt.contains("charmed")) {
			CompoundTag charm = nbt.getCompound("charmed");
			long maxMedia = charm.getLong("max_media");
			long media = charm.getLong("media");
			cir.setReturnValue(MediaHelper.mediaBarWidth(media, maxMedia));
		}
	}

	@Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true)
	public void addCharmedMediaColor(CallbackInfoReturnable<Integer> cir) {
		CustomData data = ((ItemStack) (Object) this).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if (data.contains("charmed"))
			cir.setReturnValue(0xff_e83d72);
	}
}
