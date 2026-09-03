package miyucomics.hexical.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Parrot.class)
public interface ParrotAccessor {
    @Invoker("getImitatedSound")
    static SoundEvent hexical$invokeGetImitatedSound(EntityType<?> type) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
