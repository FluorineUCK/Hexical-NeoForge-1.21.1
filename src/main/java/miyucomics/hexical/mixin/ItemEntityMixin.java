package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Shadow public abstract ItemStack getItem();

	@Inject(method = "tick", at = @At("HEAD"))
	void deactivateDroppedLamp(CallbackInfo ci) {
		if (!((Entity) (Object) this).level().isClientSide && this.getItem().is(HexicalItems.ARCH_LAMP_ITEM))
			CustomData.update(DataComponents.CUSTOM_DATA, this.getItem(), tag -> tag.putBoolean("active", false));
	}
}
