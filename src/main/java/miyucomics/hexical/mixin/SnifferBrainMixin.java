package miyucomics.hexical.mixin;

import miyucomics.hexical.features.periwinkle.SnifferEntityMinterface;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.animal.sniffer.SnifferAi$Digging")
public class SnifferBrainMixin {
	@Inject(method = "canStillUse(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/sniffer/Sniffer;J)Z", at = @At("HEAD"), cancellable = true)
	private void allowCustomDigging(ServerLevel world, Sniffer sniffer, long l, CallbackInfoReturnable<Boolean> cir) {
		if (((SnifferEntityMinterface) sniffer).isDiggingCustom())
			cir.setReturnValue(true);
	}

	@Inject(method = "stop(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/sniffer/Sniffer;J)V", at = @At("HEAD"), cancellable = true)
	private void forceDiggingSuccess(ServerLevel world, Sniffer sniffer, long l, CallbackInfo ci) {
		if (((SnifferEntityMinterface) sniffer).isDiggingCustom()) {
			sniffer.getBrain().setMemoryWithExpiry(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 9600L);
			ci.cancel();
		}
	}
}
