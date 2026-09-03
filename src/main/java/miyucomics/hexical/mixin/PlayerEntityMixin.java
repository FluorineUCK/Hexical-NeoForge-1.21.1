package miyucomics.hexical.mixin;

import miyucomics.hexical.features.player.PlayerEntityMinterface;
import miyucomics.hexical.features.player.PlayerManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique
	private final PlayerManager hexicalPlayerManager = new PlayerManager();

	@Inject(method = "tick", at = @At("TAIL"))
	void tick(CallbackInfo ci) {
		hexicalPlayerManager.tick((Player) (Object) this);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
	void reaadPlayerData(CompoundTag compound, CallbackInfo ci) {
		hexicalPlayerManager.readNbt(compound, ((Player) (Object) this).registryAccess());
	}

	@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
	void writePlayerData(CompoundTag compound, CallbackInfo ci) {
		hexicalPlayerManager.writeNbt(compound, ((Player) (Object) this).registryAccess());
	}

	@Override
	public @NotNull PlayerManager getPlayerManager() {
		return hexicalPlayerManager;
	}
}
