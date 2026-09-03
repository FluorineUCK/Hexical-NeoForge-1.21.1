package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpRead;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import miyucomics.hexical.hexcompat.ItemStackDataCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = OpRead.class, remap = false)
public class OpReadMixin {
	@Inject(method = "execute", at = @At("HEAD"), cancellable = true)
	private void readCompass(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir) {
		CastingEnvironment.HeldItemInfo data = env.getHeldItemToOperateOn(item -> item.is(HexicalItems.CURIO_COMPASS));
		if (data == null)
			return;

		CompoundTag nbt = ItemStackDataCompat.customData(data.stack());
		LivingEntity caster = env.getCastingEntity();
		if (caster == null || !nbt.contains("needle")) {
			cir.setReturnValue(List.of(new NullIota()));
			return;
		}

		int[] rawCoordinates = nbt.getIntArray("needle");
		if (rawCoordinates.length < 3) {
			cir.setReturnValue(List.of(new NullIota()));
			return;
		}
		cir.setReturnValue(List.of(new Vec3Iota(new Vec3(rawCoordinates[0], rawCoordinates[1], rawCoordinates[2]).subtract(caster.getEyePosition()).normalize())));
	}
}
