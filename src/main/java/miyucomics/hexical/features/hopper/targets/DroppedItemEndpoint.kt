package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack

class DroppedItemEndpoint(private val entity: ItemEntity) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val stack = entity.item
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = entity.item
		if (!ItemStack.isSameItem(existing, stack)) return false
		if (existing.count < amount) return false
		existing.shrink(amount)
		if (existing.isEmpty) {
			entity.discard()
		}
		return true
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val simulatedTransfers = LinkedHashMap<ItemStack, Int>()
		var existing = entity.item
		for (stack in stacks) {
			if (ItemStack.isSameItemSameComponents(existing, stack)) {
				val space = existing.maxStackSize - existing.count
				val toInsert = stack.count.coerceAtMost(space)
				if (toInsert > 0) {
					existing = existing.copyWithCount(existing.count + toInsert)
					simulatedTransfers[stack] = toInsert
				}
			}
		}
		return simulatedTransfers
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val existing = entity.item
		if (ItemStack.isSameItemSameComponents(existing, stack)) {
			val space = existing.maxStackSize - existing.count
			val toAdd = stack.count.coerceAtMost(space)
			existing.grow(toAdd)

			return if (stack.count > toAdd) {
				stack.copy().apply { shrink(toAdd) }
			} else {
				ItemStack.EMPTY
			}
		}
		return ItemStack.EMPTY
	}
}