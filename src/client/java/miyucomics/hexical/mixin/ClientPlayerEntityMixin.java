package miyucomics.hexical.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Options;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
	@Shadow
	public Input input;

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;onInput(Lnet/minecraft/client/player/Input;)V"))
	private void onInputUpdate(CallbackInfo info) {
		Minecraft client = Minecraft.getInstance();
		if (client.screen instanceof GuiSpellcasting) {
			KeyMapping.setAll();
			Options keys = client.options;
			input.up = keys.keyUp.isDown();
			input.down = keys.keyDown.isDown();
			input.left = keys.keyLeft.isDown();
			input.right = keys.keyRight.isDown();
			input.jumping = keys.keyJump.isDown();
			input.shiftKeyDown = keys.keyShift.isDown();
		}
	}
}
