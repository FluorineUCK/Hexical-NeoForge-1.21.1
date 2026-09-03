package miyucomics.hexical.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Creeper.class)
public interface CreeperAccessor {
	@Accessor("DATA_IS_IGNITED")
	static EntityDataAccessor<Boolean> hexicalGetIgnitedAccessor() {
		throw new AssertionError();
	}
}
