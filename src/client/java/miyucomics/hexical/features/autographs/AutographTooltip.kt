package miyucomics.hexical.features.autographs

import miyucomics.hexical.ClientStorage
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.hexcompat.deserializePigment
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import java.util.function.Consumer
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object AutographTooltip : InitHook() {
	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onTooltip)
	}

	private fun onTooltip(event: ItemTooltipEvent) {
		val nbt = ItemStackDataCompat.customData(event.itemStack)
		if (!nbt.contains("autographs")) return
		val lines = event.toolTip

		lines.add(Component.translatable("hexical.autograph.header").withStyle { style -> style.withColor(ChatFormatting.GRAY) })

		nbt.getList("autographs", Tag.TAG_COMPOUND.toInt()).forEach(Consumer { element: Tag? ->
			val compound = element as? CompoundTag ?: return@Consumer
			val pigment = deserializePigment(compound.getCompound("pigment")) ?: return@Consumer
			lines.add(TextUtilities.getPigmentedText(compound.getString("name"), pigment, offset = ClientStorage.ticks * 3f))
		})
	}
}
