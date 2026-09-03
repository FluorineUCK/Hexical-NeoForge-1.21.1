package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.hexcompat.deserializePattern
import miyucomics.hexical.hexcompat.serializePattern
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.gameevent.GameEvent
import java.util.Optional

class AnimatedScrollItem(private val size: Int) : Item(Properties()), IotaHolderItem {
	private fun canPlaceOn(player: Player, side: Direction, stack: ItemStack, pos: BlockPos) =
		!side.axis.isVertical && player.mayUseItemAt(pos, side, stack)

	override fun useOn(context: UseOnContext): InteractionResult {
		val direction = context.clickedFace
		val position = context.clickedPos.relative(direction)
		val player = context.player
		val stack = context.itemInHand
		val level = context.level

		if (!level.isClientSide && level.getBlockState(context.clickedPos).`is`(HexBlocks.AKASHIC_BOOKSHELF.get())) {
			val key = (level.getBlockEntity(context.clickedPos) as BlockEntityAkashicBookshelf).pattern
			if (key != null) {
				player!!.swing(context.hand)
				level.playSound(null, context.clickedPos, HexSounds.SCROLL_SCRIBBLE.value(), SoundSource.BLOCKS, 1f, 1f)
				writeDatum(stack, PatternIota(key))
				return InteractionResult.SUCCESS
			}
		}

		if (player != null && !canPlaceOn(player, direction, stack, position)) return InteractionResult.FAIL

		val data = ItemStackDataCompat.customData(stack)
		val patterns = data.getList("patterns", Tag.TAG_COMPOUND.toInt()).map { (it as CompoundTag).copy() }
		val scrollStack = stack.copyWithCount(1)
		val scroll = AnimatedScrollEntity(level, position, direction, size, patterns, scrollStack)
		scroll.setState(data.getInt("state"))
		if (data.getBoolean("glow")) scroll.toggleGlow()
		if (data.contains("color", Tag.TAG_INT.toInt())) scroll.setColor(data.getInt("color"))

		if (scroll.survives()) {
			if (!level.isClientSide) {
				scroll.playPlacementSound()
				level.gameEvent(player, GameEvent.ENTITY_PLACE, scroll.position())
				level.addFreshEntity(scroll)
			}
			stack.shrink(1)
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		return InteractionResult.CONSUME
	}

	override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("patterns", Tag.TAG_LIST.toInt())) return Optional.empty()
		val patterns = data.getList("patterns", Tag.TAG_COMPOUND.toInt()).mapNotNull(::deserializePattern)
		return Optional.of(AnimatedPatternTooltip(
			if (data.contains("color", Tag.TAG_INT.toInt())) data.getInt("color") else 0xff_000000.toInt(),
			patterns,
			data.getInt("state"),
			data.getBoolean("glow")
		))
	}

	override fun readIota(stack: ItemStack): Iota {
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("patterns", Tag.TAG_LIST.toInt())) return NullIota()
		return ListIota(data.getList("patterns", Tag.TAG_COMPOUND.toInt())
			.mapNotNull(::deserializePattern)
			.map(::PatternIota))
	}

	override fun writeable(stack: ItemStack) = true

	override fun canWrite(stack: ItemStack, iota: Iota?): Boolean = when {
		iota == null || iota is NullIota -> ItemStackDataCompat.contains(stack, "patterns")
		iota is PatternIota -> true
		iota is ListIota -> iota.list.all { it.type == HexIotaTypes.PATTERN.get() }
		else -> false
	}

	override fun writeDatum(stack: ItemStack, iota: Iota?) {
		ItemStackDataCompat.update(stack) { data ->
			if (iota == null || iota is NullIota) {
				data.remove("patterns")
				return@update
			}
			val patterns = when (iota) {
				is PatternIota -> listOf(iota.pattern)
				is ListIota -> iota.list.map { (it as PatternIota).pattern }
				else -> return@update
			}
			data.put("patterns", ListTag().also { out -> patterns.forEach { out.add(serializePattern(it)) } })
		}
	}
}
