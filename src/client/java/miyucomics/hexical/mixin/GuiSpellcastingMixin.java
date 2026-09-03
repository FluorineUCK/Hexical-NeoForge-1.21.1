package miyucomics.hexical.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GuiSpellcasting.class)
public class GuiSpellcastingMixin {
	@Unique
	long lastCounter = System.currentTimeMillis();
	@Unique
	double zoomFactor = 0;

	@WrapOperation(method = "hexSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
	double thinkyCarpet(LocalPlayer player, Holder<Attribute> entityAttribute, Operation<Double> original) {
		long deltaTime = System.currentTimeMillis() - lastCounter;
		lastCounter = System.currentTimeMillis();

		if (player.level().getBlockState(player.blockPosition()).is(HexicalBlocks.CASTING_CARPET)) {
			zoomFactor += (double) deltaTime / 500;
		} else {
			zoomFactor -= (double) deltaTime / 500;
		}
		zoomFactor = Mth.clamp(zoomFactor, 0, 0.35);

		double returnValue = original.call(player, entityAttribute);
		returnValue += zoomFactor;
		return returnValue;
	}
}
