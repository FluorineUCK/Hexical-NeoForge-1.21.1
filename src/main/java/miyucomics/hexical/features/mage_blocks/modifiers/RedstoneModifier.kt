package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.asInt
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.Tag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component

class RedstoneModifier : MageBlockModifier {
	override val type: MageBlockModifierType<*> = TYPE
	var power = 0

	override fun getScryingLens(): Pair<ItemStack, Component> = Pair(ItemStack(Items.REDSTONE), "hexical.mage_block.redstone".asTranslatedComponent(power))
	override fun serialize(): Tag = IntTag.valueOf(power)

	companion object {
		val TYPE: MageBlockModifierType<RedstoneModifier> = object : MageBlockModifierType<RedstoneModifier>() {
			override val argc: Int = 1
			override val id = HexicalMain.id("redstone")
			override fun construct(args: List<Iota>) = RedstoneModifier().also { it.power = args.getPositiveIntUnderInclusive(1, 15, 2) }
			override fun deserialize(element: Tag) = RedstoneModifier().also { it.power = element.asInt }
		}
	}
}