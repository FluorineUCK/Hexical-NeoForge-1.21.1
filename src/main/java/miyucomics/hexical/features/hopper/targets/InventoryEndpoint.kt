package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

class InventoryEndpoint(val inventory: Container) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		return (0 until inventory.getContainerSize()).map { inventory.getItem(it).copy() }.filterNot { it.isEmpty }
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		var remaining = amount

		for (i in 0 until inventory.getContainerSize()) {
			val existing = inventory.getItem(i)
			if (!ItemStack.isSameItem(existing, stack)) continue
			if (existing.isEmpty) continue
			val toTake = remaining.coerceAtMost(existing.count)
			remaining -= inventory.removeItem(i, toTake).count
			if (remaining <= 0) return true
		}

		return false
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val remaining = stack.copy()

		// First, try to merge into existing stacks
		for (slot in 0 until inventory.getContainerSize()) {
			if (!inventory.canPlaceItem(slot, remaining)) continue
			val existing = inventory.getItem(slot)
			if (!ItemStack.isSameItemSameComponents(existing, remaining)) continue

			val slotLimit = minOf(inventory.maxStackSize, remaining.maxStackSize)
			val canAdd = slotLimit - existing.count
			val toAdd = remaining.count.coerceAtMost(canAdd)
			if (toAdd > 0) {
				existing.grow(toAdd)
				remaining.shrink(toAdd)
				if (remaining.isEmpty) return ItemStack.EMPTY
			}
		}

		for (i in 0 until inventory.getContainerSize()) {
			if (!inventory.canPlaceItem(i, remaining)) continue
			val existing = inventory.getItem(i)
			if (!existing.isEmpty) continue

			val slotLimit = minOf(inventory.maxStackSize, remaining.maxStackSize)
			val toPlace = remaining.copy()
			val placedCount = toPlace.count.coerceAtMost(slotLimit)
			toPlace.count = placedCount
			inventory.setItem(i, toPlace)
			remaining.shrink(placedCount)

			if (remaining.isEmpty) return ItemStack.EMPTY
		}

		return remaining
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val simulatedTransfers = LinkedHashMap<ItemStack, Int>()
		val modifiedSlotStacks = HashMap<Int, ItemStack>()
		stackloop@ for (stack in stacks) {
			if (stack.isEmpty)
				continue
			var remaining = stack.count

			// First, try to merge into existing stacks
			for (i in 0 until inventory.getContainerSize()) {
				val existing = modifiedSlotStacks[i] ?: inventory.getItem(i)
				if (!inventory.canPlaceItem(i, stack)) continue
				if (ItemStack.isSameItemSameComponents(existing, stack)) {
					val slotLimit = minOf(inventory.maxStackSize, existing.maxStackSize)
					val space = slotLimit - existing.count
					val toInsert = remaining.coerceAtMost(space)
					remaining -= toInsert
					modifiedSlotStacks[i] = existing.copyWithCount(existing.count + toInsert)
					if (remaining <= 0) {
						simulatedTransfers[stack] = stack.count
						continue@stackloop
					}
				}
			}

			val effectiveMax = minOf(stack.maxStackSize, inventory.maxStackSize)
			for (i in 0 until inventory.getContainerSize()) {
				val existing = modifiedSlotStacks[i] ?: inventory.getItem(i)
				if (!inventory.canPlaceItem(i, stack)) continue
				if (!existing.isEmpty) continue

				val toInsert = remaining.coerceAtMost(effectiveMax)
				remaining -= toInsert
				modifiedSlotStacks[i] = stack.copyWithCount(toInsert)
				if (remaining <= 0) {
					simulatedTransfers[stack] = stack.count
					continue@stackloop
				}
			}

			simulatedTransfers[stack] = stack.count - remaining
		}
		return simulatedTransfers
	}
}