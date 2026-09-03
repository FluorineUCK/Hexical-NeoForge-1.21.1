package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.level.Level

class HexburstItem : Item(Properties().stacksTo(16).food(FoodProperties.Builder().alwaysEdible().fast().build())) {
	override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = 10
	override fun finishUsingItem(stack: ItemStack, world: Level, user: LivingEntity): ItemStack {
		if (world.isClientSide)
			return super.finishUsingItem(stack, world, user)
		if (user !is ServerPlayer)
			return super.finishUsingItem(stack, world, user)
		val data = ItemStackDataCompat.customData(stack)
		CastingUtils.giveIota(user, if (data.contains("iota"))
			miyucomics.hexical.hexcompat.deserializeIotaOrThrow(data.getCompound("iota"), world as ServerLevel)
		else
			GarbageIota())
		return super.finishUsingItem(stack, world, user)
	}

	override fun appendHoverText(stack: ItemStack, tooltipContext: TooltipContext, list: MutableList<Component>, context: TooltipFlag) {
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("iota"))
			return
		list.add("hexical.hexburst.iota".asTranslatedComponent(
			miyucomics.hexical.hexcompat.displayIota(data.getCompound("iota"))
		).withStyle(ChatFormatting.GRAY))
	}
}
