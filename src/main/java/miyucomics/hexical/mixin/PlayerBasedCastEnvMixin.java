package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import miyucomics.hexical.features.media_log.MediaLogField;
import miyucomics.hexical.features.media_log.MediaLogFieldKt;
import miyucomics.hexical.features.periwinkle.WooleyedEffectRegister;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public class PlayerBasedCastEnvMixin {
	@Shadow @Final protected ServerPlayer caster;

	@Inject(method = "canOvercast", at = @At("HEAD"), cancellable = true)
	private void canOvercast(CallbackInfoReturnable<Boolean> cir) {
		if (this.caster.getItemBySlot(EquipmentSlot.HEAD).is(HexicalItems.LEI) || this.caster.hasEffect(WooleyedEffectRegister.effectHolder()))
			cir.setReturnValue(false);
	}

	@Inject(method = "sendMishapMsgToPlayer(Lat/petrak/hexcasting/api/casting/eval/sideeffects/OperatorSideEffect$DoMishap;)V", at = @At("HEAD"))
	private void captureMishap(OperatorSideEffect.DoMishap mishap, CallbackInfo ci) {
		Component message = mishap.getMishap().errorMessageWithName((CastingEnvironment) (Object) this, mishap.getErrorCtx());
		if (message != null && MediaLogField.isEnvCompatible((CastingEnvironment) (Object) this))
			MediaLogFieldKt.getMediaLog(caster).saveMishap(message);
	}
}
