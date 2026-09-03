package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object CharmedItemTooltip : InitHook() {
	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onTooltip)
	}

	private fun onTooltip(event: ItemTooltipEvent) {
		val stack = event.itemStack
		if (!CharmUtilities.isStackCharmed(stack)) return
		val media = CharmUtilities.getMedia(stack)
		val maxMedia = CharmUtilities.getMaxMedia(stack)
		val lines = event.toolTip
		lines.add(Component.translatable("hexical.charmed").withStyle { style -> style.withColor(CharmUtilities.CHARMED_COLOR) })
		lines.add(
				Component.translatable("hexcasting.tooltip.media_amount.advanced",
				Component.literal(TextUtilities.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(
					ItemMediaHolder.HEX_COLOR) },
				Component.translatable("hexcasting.tooltip.media", TextUtilities.DUST_AMOUNT.format((maxMedia / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(
					ItemMediaHolder.HEX_COLOR) },
				Component.literal(TextUtilities.PERCENTAGE.format((100f * media / maxMedia).toDouble()) + "%").withStyle { style -> style.withColor(
					TextColor.fromRgb(mediaBarColor(media, maxMedia))) }
			))
	}
}
