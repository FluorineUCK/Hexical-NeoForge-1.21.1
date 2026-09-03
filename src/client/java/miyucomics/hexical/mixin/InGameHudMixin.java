package miyucomics.hexical.mixin;

import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.features.periwinkle.WooleyedEffectRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
	@Unique
	private static final ResourceLocation HEARTS = HexicalMain.id("textures/gui/amethyst_hearts.png");

	@Inject(method = "renderHeart", at = @At("HEAD"), cancellable = true)
	private void amethystHearts(GuiGraphics context, Gui.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean halfHeart, CallbackInfo ci) {
		Player player = Minecraft.getInstance().player;
		if (player == null)
			return;
		if (!player.hasEffect(WooleyedEffectRegister.effectHolder()))
			return;
		if (type == Gui.HeartType.NORMAL) {
			context.blit(HEARTS, x, y, halfHeart ? 9 : 0, 0, 9, 9);
			ci.cancel();
		} else if (type == Gui.HeartType.CONTAINER) {
			context.blit(HEARTS, x, y, 18, 0, 9, 9);
			ci.cancel();
		}
	}
}
