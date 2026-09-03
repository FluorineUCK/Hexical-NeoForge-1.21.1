package miyucomics.hexical.features.jailbreak

import at.petrak.hexcasting.api.utils.asTranslatedComponent
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import at.petrak.hexcasting.common.lib.HexDataComponents
import miyucomics.hexical.features.charms.CharmUtilities
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.hexcompat.deserializeIota
import net.minecraft.nbt.Tag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object JailbrokenItemTooltip : InitHook() {
	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onTooltip)
	}

	private fun onTooltip(event: ItemTooltipEvent) {
		val stack = event.itemStack
		val nbt = ItemStackDataCompat.customData(stack)
		if (!nbt.getBoolean("cracked")) return
		when (stack.item) {
			is ItemPackagedHex -> {
				val hex = stack.get(HexDataComponents.HEX_HOLDER_PATTERNS.get())
				if (!hex.isNullOrEmpty()) event.toolTip.add("hexical.cracked.hex".asTranslatedComponent(hex.fold(Component.empty()) { acc, iota -> acc.append(iota.display()) }))
				else event.toolTip.add("hexical.cracked.cracked".asTranslatedComponent.withStyle(ChatFormatting.GOLD))
			}
			is CurioItem -> {
				if (CharmUtilities.isStackCharmed(stack)) event.toolTip.add("hexical.cracked.hex".asTranslatedComponent(getText(CharmUtilities.getCompound(stack).getList("hex", Tag.TAG_COMPOUND.toInt()))))
				else event.toolTip.add("hexical.cracked.cracked".asTranslatedComponent.withStyle(ChatFormatting.GOLD))
			}
		}
	}

	private fun getText(hex: ListTag) = hex.fold(Component.empty()) { acc, curr ->
		deserializeIota(curr)?.let { acc.append(it.display()) } ?: acc
	}
}
