package miyucomics.hexical.mixin;

import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Squid.class)
public interface SquidInvoker {
	@Invoker("spawnInk")
	void invokeSpawnInk();
}
