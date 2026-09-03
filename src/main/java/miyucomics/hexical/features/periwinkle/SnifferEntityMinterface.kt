package miyucomics.hexical.features.periwinkle

import net.minecraft.world.item.ItemStack

interface SnifferEntityMinterface {
	fun produceItem(stack: ItemStack)
	fun isDiggingCustom(): Boolean
}