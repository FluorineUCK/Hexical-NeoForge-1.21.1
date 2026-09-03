package miyucomics.hexical.mixin;

import miyucomics.hexical.features.shaders.ShaderRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "render", at = @At("RETURN"))
	public void renderShader(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
		ShaderRenderer.render(deltaTracker.getGameTimeDeltaPartialTick(true));
	}
}
