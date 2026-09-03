package miyucomics.hexical.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import miyucomics.hexical.inits.HexicalKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.gui.GuiBook;

@Mixin(Screen.class)
public abstract class ScreenMixin {
	@Unique private static GuiSpellcasting staffScreen;

	@Inject(method = "onClose", at = @At("HEAD"), cancellable = true)
	void returnToStaff(CallbackInfo ci) {
		if ((Screen) (Object) this instanceof GuiBook && staffScreen != null) {
			Minecraft.getInstance().setScreen(staffScreen);
			staffScreen = null;
			ci.cancel();
		}
	}

	@Inject(method = "keyPressed", at = @At("HEAD"))
	void openHexbook(int keycode, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		if ((Screen) (Object) this instanceof GuiSpellcasting screen && HexicalKeybinds.OPEN_HEXBOOK.matches(keycode, scancode)) {
			PatchouliAPI.get().openBookGUI(ResourceLocation.fromNamespaceAndPath("hexcasting", "thehexbook"));
			staffScreen = screen;
		}
	}
}
