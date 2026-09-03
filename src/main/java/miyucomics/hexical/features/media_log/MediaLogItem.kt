package miyucomics.hexical.features.media_log

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class MediaLogItem : Item(Properties().stacksTo(1)) {
	override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(hand)
		if (!world.isClientSide)
			(player as ServerPlayer).syncMediaLog()
		player.startUsingItem(hand)
		return InteractionResultHolder.success(stack)
	}

	override fun getUseAnimation(itemStack: ItemStack) = UseAnim.BOW
	override fun getDescriptionId() = "item.hexical.media_log." + ((System.currentTimeMillis() / 2000) % 2).toString()
}