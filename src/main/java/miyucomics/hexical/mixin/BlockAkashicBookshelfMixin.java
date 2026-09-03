package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import miyucomics.hexical.inits.HexicalSounds;
import miyucomics.hexical.misc.CastingUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.sounds.SoundSource.BLOCKS;

@Mixin(BlockAkashicBookshelf.class)
public class BlockAkashicBookshelfMixin {
	@Inject(method = "useWithoutItem", at = @At("TAIL"))
	private void copyIota(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
		if (world.isClientSide || player.isShiftKeyDown() || !player.getMainHandItem().isEmpty())
			return;

		BlockEntity shelf = world.getBlockEntity(pos);
		if (!(shelf instanceof BlockEntityAkashicBookshelf))
			return;

		Iota iota = ((BlockEntityAkashicBookshelf) shelf).getIota();
		if (iota == null)
			return;

		CastingUtils.giveIota((ServerPlayer) player, iota);
		world.playSound(null, pos, HexicalSounds.SUDDEN_REALIZATION, BLOCKS, 1f, 1f);
		player.swing(InteractionHand.MAIN_HAND, true);
	}
}
