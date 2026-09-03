package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "handleBlockEntityData", at = @At("TAIL"))
    private void rerenderMageBlocks(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null)
            return;
        BlockPos pos = packet.getPos();
        BlockState state = client.level.getBlockState(pos);
        if (state.is(HexicalBlocks.MAGE_BLOCK)) {
            LevelRenderer renderer = client.levelRenderer;
            renderer.setBlocksDirty(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
