package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction

class SidedInventoryEndpoint(private val inventory: WorldlyContainer, private val direction: Direction) : HopperSource, HopperDestination {
	override fun getItems() = inventory.getSlotsForFace(direction).map { inventory.getItem(it).copy() }.filterNot { it.isEmpty }

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		var remaining = amount
		val slots = inventory.getSlotsForFace(direction)

		for (slot in slots) {
			val existing = inventory.getItem(slot)

			if (!ItemStack.isSameItem(existing, stack)) continue
			if (!inventory.canTakeItemThroughFace(slot, stack, direction)) continue

			val toTake = remaining.coerceAtMost(existing.count)
			existing.shrink(toTake)
			remaining -= toTake

			if (remaining <= 0) return true
		}

		return false
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val simulatedTransfers = LinkedHashMap<ItemStack, Int>()
		val modifiedSlotStacks = HashMap<Int, ItemStack>()
		stackloop@ for (stack in stacks) {
			if (stack.isEmpty)
				continue
			var remaining = stack.count
			val slots = inventory.getSlotsForFace(direction)
			val slotLimit = minOf(stack.maxStackSize, inventory.maxStackSize)

			for (slot in slots) {
				if (!inventory.canPlaceItemThroughFace(slot, stack, direction)) continue

				val existing = modifiedSlotStacks[slot] ?: inventory.getItem(slot)

				if (existing.isEmpty) {
					val toInsert = remaining.coerceAtMost(slotLimit)
					remaining -= toInsert
					modifiedSlotStacks[slot] = stack.copyWithCount(toInsert)
				} else if (ItemStack.isSameItemSameComponents(existing, stack)) {
					val space = slotLimit - existing.count
					val toInsert = remaining.coerceAtMost(space)
					remaining -= toInsert
					modifiedSlotStacks[slot] = existing.copyWithCount(existing.count + toInsert)
				}

				if (remaining <= 0) {
					simulatedTransfers[stack] = stack.count
					continue@stackloop
				}
			}

			simulatedTransfers[stack] = stack.count - remaining
		}
		return simulatedTransfers	
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val working = stack.copy()
		val slots = inventory.getSlotsForFace(direction)
		val slotLimit = minOf(stack.maxStackSize, inventory.maxStackSize)

		for (slot in slots) {
			if (!inventory.canPlaceItemThroughFace(slot, working, direction)) continue

			val existing = inventory.getItem(slot)

			if (existing.isEmpty) {
				val placed = working.copy()
				val toPlace = working.count.coerceAtMost(slotLimit)
				placed.count = toPlace
				inventory.setItem(slot, placed)
				working.shrink(toPlace)
			} else if (ItemStack.isSameItemSameComponents(existing, working)) {
				val space = slotLimit - existing.count
				val toAdd = working.count.coerceAtMost(space)
				existing.grow(toAdd)
				working.shrink(toAdd)
			}

			if (working.isEmpty) break
		}

		return if (working.isEmpty) ItemStack.EMPTY else working
	}
}