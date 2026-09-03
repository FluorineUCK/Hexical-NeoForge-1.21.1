package miyucomics.hexical.mixin;

import net.minecraft.world.entity.animal.Pufferfish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Pufferfish.class)
public interface PufferfishAccessor {
	@Accessor("inflateCounter")
	void hexicalSetInflateCounter(int value);

	@Accessor("deflateTimer")
	void hexicalSetDeflateTimer(int value);
}
