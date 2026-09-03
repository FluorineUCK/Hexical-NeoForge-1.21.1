package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.item.ItemStack

class ItemFrameEndpoint(val frame: ItemFrame) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val stack = frame.item
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = frame.item
		if (!ItemStack.isSameItem(existing, stack))
			return false
		if (existing.count < amount)
			return false
		if (amount != 1)
			return false
		frame.setItem(ItemStack.EMPTY, true)
		return true
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val existing = frame.item
		if (!existing.isEmpty)
			return stack

		val single = stack.copy()
		single.count = 1
		frame.setItem(single, true)

		return if (stack.count > 1) {
			val leftover = stack.copy()
			leftover.shrink(1)
			leftover
		} else {
			ItemStack.EMPTY
		}
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val existing = frame.item
		if (!existing.isEmpty)
			return mapOf()
		val firstItem = stacks.getOrNull(0) ?: return mapOf()
		return mapOf(firstItem to 1)
	}
}