package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.inventory.Slot
import net.minecraft.sounds.SoundSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.level.Level
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import kotlin.math.max
import kotlin.math.min

class MediaJarItem : BlockItem(HexicalBlocks.MEDIA_JAR_BLOCK, Properties().stacksTo(1)) {
	override fun appendHoverText(stack: ItemStack, context: TooltipContext, list: MutableList<Component>, tooltipFlag: TooltipFlag) {
		val tag = ItemStackDataCompat.blockEntityData(stack)
		val media = tag?.getLong("media") ?: 0
		list.add(Component.translatable("hexcasting.tooltip.media_amount.advanced",
			Component.literal(TextUtilities.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.translatable("hexcasting.tooltip.media", TextUtilities.DUST_AMOUNT.format((MediaJarBlock.MAX_CAPACITY / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { style -> style.withColor(ItemMediaHolder.HEX_COLOR) },
			Component.literal(TextUtilities.PERCENTAGE.format((100f * media / MediaJarBlock.MAX_CAPACITY).toDouble()) + "%").withStyle { style -> style.withColor(TextColor.fromRgb(mediaBarColor(media, MediaJarBlock.MAX_CAPACITY.toLong()))) }
		))
	}

	override fun overrideStackedOnOther(jar: ItemStack, slot: Slot, clickType: ClickAction, player: Player): Boolean {
		if (clickType != ClickAction.SECONDARY)
			return false
		val stack = slot.item
		if (stack.isEmpty)
			return false
		val world = player.level()
		val jarData = ItemStackDataCompat.blockEntityData(jar) ?: return false

		val handled = when (val result = TransmutingHelper.transmuteItem(world, stack, jarData.getLong("media"), { insertMedia(jarData, it) }, { withdrawMedia(jarData, it) })) {
			is TransmutationResult.AbsorbedMedia -> {
				world.playLocalSound(player.x, player.y, player.z, HexicalSounds.AMETHYST_MELT, SoundSource.BLOCKS, 1f, 1f, true)
				true
			}
			is TransmutationResult.TransmutedItems -> {
				world.playLocalSound(player.x, player.y, player.z, HexicalSounds.ITEM_DUNKS, SoundSource.BLOCKS, 1f, 1f, true)
				val output = result.output.toMutableList()
				if (slot.item.isEmpty)
					slot.setByPlayer(output.removeAt(0))
				output.forEach(player::addItem)
				true
			}
			is TransmutationResult.RefilledHolder -> {
				world.playLocalSound(player.x, player.y, player.z, HexicalSounds.ITEM_DUNKS, SoundSource.BLOCKS, 1f, 1f, true)
				true
			}
			is TransmutationResult.Pass -> false
		}
		if (handled) ItemStackDataCompat.setBlockEntityData(jar, jarData)
		return handled
	}

	companion object {
		fun getMedia(jarData: CompoundTag) = jarData.getLong("media")
		fun setMedia(jarData: CompoundTag, media: Long) {
			jarData.putLong("media", max(min(media, MediaJarBlock.MAX_CAPACITY), 0))
		}

		fun insertMedia(jarData: CompoundTag, media: Long): Long {
			val currentMedia = getMedia(jarData)
			setMedia(jarData, currentMedia + media)
			return this.getMedia(jarData) - currentMedia
		}

		fun withdrawMedia(jarData: CompoundTag, media: Long): Boolean {
			if (getMedia(jarData) >= media) {
				setMedia(jarData, getMedia(jarData) - media)
				return true
			} else {
				setMedia(jarData, 0)
				return false
			}
		}
	}
}
