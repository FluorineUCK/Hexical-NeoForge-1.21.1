package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.*
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.misc.HexSerialization
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.Level

class HextitoItem : Item(Properties().stacksTo(16).food(FoodProperties.Builder().alwaysEdible().fast().build())) {
	override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = 10
	override fun finishUsingItem(stack: ItemStack, world: Level, user: LivingEntity): ItemStack {
		if (world.isClientSide)
			return super.finishUsingItem(stack, world, user)
		if (user !is ServerPlayer)
			return super.finishUsingItem(stack, world, user)
		val vm = CastingVM(IXplatAbstractions.INSTANCE.getStaffcastVM(user, user.usedItemHand).image.copy(), HextitoCastEnv(user, InteractionHand.MAIN_HAND))
		val data = ItemStackDataCompat.customData(stack)
		if (vm.image.parenCount == 0 && data.contains("hex")) {
			vm.queueExecuteAndWrapIotas(HexSerialization.backwardsCompatibleReadHex(data, "hex", world as ServerLevel), world)
			ItemStackDataCompat.replace(stack, data)
			IXplatAbstractions.INSTANCE.setStaffcastImage(user, vm.image)
		}
		return super.finishUsingItem(stack, world, user)
	}

	override fun appendHoverText(stack: ItemStack, tooltipContext: TooltipContext, list: MutableList<Component>, context: TooltipFlag) {
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("hex"))
			return
		list.add("hexical.hextito.hex".asTranslatedComponent(
			data.getList("hex", Tag.TAG_COMPOUND.toInt())
				.fold(Component.empty()) { acc, curr ->
					acc.append(miyucomics.hexical.hexcompat.displayIota(curr))
				}
		).styledWith(ChatFormatting.GRAY))
	}
}
