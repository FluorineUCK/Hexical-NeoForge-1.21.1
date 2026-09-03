package miyucomics.hexical.features.hopper

import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

class ListBackedInventory(private val stacks: MutableList<ItemStack>, val size: Int = stacks.size) : Container {
	override fun removeItem(slot: Int, amount: Int): ItemStack = stacks[slot].split(amount)

	override fun removeItemNoUpdate(slot: Int): ItemStack {
		val original = stacks[slot]
		stacks[slot] = ItemStack.EMPTY
		return original
	}

	override fun setItem(slot: Int, stack: ItemStack) {
		stacks[slot] = stack
	}

	override fun clearContent() {
		for (i in stacks.indices)
			stacks[i] = ItemStack.EMPTY
	}

	override fun getContainerSize() = size
	override fun setChanged() {}
	override fun getItem(slot: Int) = stacks[slot]
	override fun isEmpty() = stacks.all { it.isEmpty }
	override fun stillValid(player: Player) = true
}
