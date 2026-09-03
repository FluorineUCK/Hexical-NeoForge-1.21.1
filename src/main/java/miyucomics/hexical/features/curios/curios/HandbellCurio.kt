package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.network.PlayerUuidPayload
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

object HandbellCurio : CurioItem() {
	@JvmField val ANIMATION_ID = HexicalMain.id("handbell")

	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.pass(user.getItemInHand(hand))
		playSound(world as ServerLevel, user as ServerPlayer)
		return InteractionResultHolder.pass(user.getItemInHand(hand))
	}

	override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
		playSound(world, user)
	}

	private fun playSound(world: ServerLevel, user: ServerPlayer) {
		PacketDistributor.sendToPlayer(user, PlayerUuidPayload(user.uuid, PlayerUuidPayload.HANDBELL_TYPE))
		world.playSound(null, user.x, user.y, user.z, HexicalSounds.HANDBELL_CHIMES, SoundSource.MASTER, 1f, 0.8f + HexicalMain.RANDOM.nextFloat() * 0.3f)
	}
}
