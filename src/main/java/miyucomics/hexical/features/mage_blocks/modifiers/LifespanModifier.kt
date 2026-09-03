package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.utils.asInt
import at.petrak.hexcasting.api.utils.asTranslatedComponent
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.Tag
import net.minecraft.nbt.IntTag
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class LifespanModifier : MageBlockModifier {
	override val type: MageBlockModifierType<*> = TYPE
	var lifespan = 0

	override fun getScryingLens(): Pair<ItemStack, Component> = Pair(ItemStack(Items.CLOCK), "hexical.mage_block.lifespan".asTranslatedComponent(lifespan / 20f))
	override fun serialize(): Tag = IntTag.valueOf(lifespan)
	override fun tick(world: Level, pos: BlockPos, state: BlockState) {
		lifespan--
		if (lifespan <= 0)
			HexicalBlocks.MAGE_BLOCK.destroyMageBlock(world, pos, state)
	}

	companion object {
		val TYPE: MageBlockModifierType<LifespanModifier> = object : MageBlockModifierType<LifespanModifier>() {
			override val argc: Int = 1
			override val id = HexicalMain.id("lifespan")
			override fun construct(args: List<Iota>) = LifespanModifier().also { it.lifespan = args.getPositiveInt(1, 2) }
			override fun deserialize(element: Tag) = LifespanModifier().also { it.lifespan = element.asInt }
		}
	}
}
