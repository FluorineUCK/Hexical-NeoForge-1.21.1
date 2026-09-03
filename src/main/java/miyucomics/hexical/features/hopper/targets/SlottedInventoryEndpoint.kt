package miyucomics.hexical.features.hopper.targets

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import miyucomics.hexical.features.hopper.InvalidSlotMishap
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

class SlottedInventoryEndpoint(private val inventory: Container, private val slot: Int, iota: Iota) : HopperSource, HopperDestination {
	init {
		if (slot !in 0 until inventory.getContainerSize())
			throw InvalidSlotMishap(iota, slot)
	}

	override fun getItems(): List<ItemStack> {
		val stack = inventory.getItem(slot)
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = inventory.getItem(slot)
		if (!ItemStack.isSameItem(existing, stack)) return false
		if (existing.count < amount) return false
		if (!inventory.canPlaceItem(slot, stack)) return false

		existing.shrink(amount)
		return true
	}

	override fun deposit(stack: ItemStack): ItemStack {
		if (!inventory.canPlaceItem(slot, stack)) return stack.copy()
		val slotLimit = minOf(stack.maxStackSize, inventory.maxStackSize)

		val existingStack = inventory.getItem(slot)
		if (existingStack.isEmpty) {
			val insertedStack = stack.copy()
			val amount = insertedStack.count.coerceAtMost(slotLimit)
			insertedStack.count = amount
			inventory.setItem(slot, insertedStack)
			return if (stack.count > amount) stack.copy().apply { shrink(amount) } else ItemStack.EMPTY
		}

		if (!ItemStack.isSameItemSameComponents(existingStack, stack)) return stack.copy()
		val amountToInsert = slotLimit - existingStack.count
		if (amountToInsert <= 0) return stack.copy()
		val insertedStack = stack.count.coerceAtMost(amountToInsert)
		existingStack.grow(insertedStack)
		return if (stack.count > insertedStack) stack.copy().apply { shrink(insertedStack) } else ItemStack.EMPTY
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val simulatedTransfers = LinkedHashMap<ItemStack, Int>()
		var target = inventory.getItem(slot)
		for (stack in stacks) {
			var remaining = stack.count
			if (!inventory.canPlaceItem(slot, stack))
				continue
			val slotLimit = minOf(stack.maxStackSize, inventory.maxStackSize)
			if (target.isEmpty) {
				val toInsert = stack.count.coerceAtMost(slotLimit)
				remaining -= toInsert
				target = stack.copyWithCount(toInsert)
			} else if (ItemStack.isSameItemSameComponents(target, stack)) {
				val space = slotLimit - target.count
				val toInsert = stack.count.coerceAtMost(space)
				remaining -= toInsert
				target = target.copyWithCount(target.count + toInsert)
			}
			if (remaining < stack.count)
				simulatedTransfers[stack] = stack.count - remaining
		}
		return simulatedTransfers
	}
}