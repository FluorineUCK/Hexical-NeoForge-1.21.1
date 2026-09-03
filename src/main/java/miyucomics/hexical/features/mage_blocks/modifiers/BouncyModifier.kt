package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component

class BouncyModifier : MageBlockModifier {
	override val type: MageBlockModifierType<*> = TYPE

	override fun getScryingLens(): Pair<ItemStack, Component> = Pair(ItemStack(Items.SLIME_BALL), "hexical.mage_block.bouncy".asTranslatedComponent)
	override fun serialize(): Tag = ByteTag.valueOf(false)

	companion object {
		val TYPE: MageBlockModifierType<BouncyModifier> = object : MageBlockModifierType<BouncyModifier>() {
			override val argc: Int = 0
			override val id = HexicalMain.id("bouncy")
			override fun construct(args: List<Iota>) = BouncyModifier()
			override fun deserialize(element: Tag) = BouncyModifier()
		}
	}
}