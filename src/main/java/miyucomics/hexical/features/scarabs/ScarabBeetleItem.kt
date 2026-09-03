package miyucomics.hexical.features.scarabs

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.api.utils.*
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Rarity
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level
import miyucomics.hexical.hexcompat.ItemStackDataCompat

class ScarabBeetleItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IotaHolderItem {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		ItemStackDataCompat.update(stack) { it.putBoolean("active", !it.getBoolean("active")) }
		if (world.isClientSide)
			world.playLocalSound(user.x, user.y, user.z, HexicalSounds.SCARAB_CHIRPS, SoundSource.MASTER, 1f, 1f, true)
		return InteractionResultHolder.success(stack)
	}

	override fun appendHoverText(stack: ItemStack, tooltipContext: TooltipContext, tooltip: MutableList<Component>, context: TooltipFlag) {
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("hex"))
			return
		val display = data.getList("hex", Tag.TAG_COMPOUND.toInt()).fold(Component.empty()) { acc, curr ->
			acc.append(miyucomics.hexical.hexcompat.displayIota(curr))
		}
		tooltip.add("hexical.scarab.hex".asTranslatedComponent(display).styledWith(ChatFormatting.GRAY))
	}

	override fun readIota(stack: ItemStack): Iota? = null
	override fun writeable(stack: ItemStack) = true
	override fun canWrite(stack: ItemStack, iota: Iota?) = iota == null || iota is ListIota
	override fun writeDatum(stack: ItemStack, iota: Iota?) {
		if (iota == null)
			ItemStackDataCompat.update(stack) { it.remove("hex") }
		else
			ItemStackDataCompat.update(stack) { it.put("hex", HexSerialization.serializeHex((iota as ListIota).list.toList())) }
	}
}
