package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import miyucomics.hexical.features.curios.FluteCurioItemModel;
import miyucomics.hexical.features.curios.HandbellCurioItemModel;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
	@Shadow @Final private ItemModelShaper itemModelShaper;

	@Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
	private void injectModel(ItemStack itemStack, Level world, LivingEntity livingEntity, int i, CallbackInfoReturnable<BakedModel> cir) {
		if (itemStack.is(HexicalItems.CURIO_FLUTE))
			cir.setReturnValue(this.itemModelShaper.getModelManager().getModel(FluteCurioItemModel.heldFluteModel));
		if (itemStack.is(HexicalItems.CURIO_HANDBELL))
			cir.setReturnValue(this.itemModelShaper.getModelManager().getModel(HandbellCurioItemModel.heldHandbellModel));
	}

	@Inject(method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V", at = @At("HEAD"))
	private void renderFlatFlute(ItemStack stack, ItemDisplayContext mode, boolean bl, PoseStack matrices, MultiBufferSource vertices, int i, int j, BakedModel model, CallbackInfo ci, @Local(argsOnly = true) LocalRef<BakedModel> modelReference) {
		if ((mode == ItemDisplayContext.GUI || mode == ItemDisplayContext.GROUND || mode == ItemDisplayContext.FIXED) && stack.is(HexicalItems.CURIO_FLUTE))
			modelReference.set(this.itemModelShaper.getModelManager().getModel(FluteCurioItemModel.fluteModel));
		if ((mode == ItemDisplayContext.GUI || mode == ItemDisplayContext.GROUND || mode == ItemDisplayContext.FIXED) && stack.is(HexicalItems.CURIO_HANDBELL))
			modelReference.set(this.itemModelShaper.getModelManager().getModel(HandbellCurioItemModel.handbellModel));
	}
}
