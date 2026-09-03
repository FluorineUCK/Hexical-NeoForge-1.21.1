package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import miyucomics.hexical.features.periwinkle.SnifferEntityMinterface;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sniffer.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public abstract class SnifferEntityMixin implements SnifferEntityMinterface {
	@Shadow public abstract Sniffer transitionTo(Sniffer.State state);
	@Shadow public abstract Brain<Sniffer> getBrain();
	@Unique private ItemStack customItem = null;
	@Unique private boolean isDiggingCustom = false;

	@Override
	public boolean isDiggingCustom() {
		return isDiggingCustom;
	}

	@Override
	public void produceItem(@NotNull ItemStack stack) {
		this.customItem = stack;
		this.isDiggingCustom = true;
		getBrain().eraseMemory(MemoryModuleType.SNIFF_COOLDOWN);
		getBrain().eraseMemory(MemoryModuleType.DIG_COOLDOWN);
		getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		getBrain().eraseMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
		getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);

		getBrain().setMemory(MemoryModuleType.SNIFFER_DIGGING, true);

		getBrain().stopAll((ServerLevel) ((Sniffer) (Object) this).level(), ((Sniffer) (Object) this));
		getBrain().useDefaultActivity();
		transitionTo(Sniffer.State.DIGGING);
	}

	@Inject(method = "canSniff", at = @At("HEAD"), cancellable = true)
	public void youWantToDig(CallbackInfoReturnable<Boolean> cir) {
		if (isDiggingCustom)
			cir.setReturnValue(true);
	}

	@Inject(method = "canDig", at = @At("HEAD"), cancellable = true)
	public void youCanDig(CallbackInfoReturnable<Boolean> cir) {
		if (isDiggingCustom)
			cir.setReturnValue(true);
	}

	@WrapOperation(method = "dropSeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
	public ObjectArrayList<ItemStack> alterDrops(LootTable instance, LootParams lootContextParameterSet, Operation<ObjectArrayList<ItemStack>> original) {
		if (isDiggingCustom) {
			ObjectArrayList<ItemStack> newDrops = new ObjectArrayList<>();
			newDrops.add(customItem);
			customItem = null;
			isDiggingCustom = false;
			return newDrops;
		}
		return original.call(instance, lootContextParameterSet);
	}
}
