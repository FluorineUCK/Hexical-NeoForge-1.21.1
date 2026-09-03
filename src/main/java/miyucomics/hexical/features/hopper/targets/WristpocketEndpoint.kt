package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import miyucomics.hexical.features.hopper.HopperSource
import miyucomics.hexical.features.wristpocket.wristpocket
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class WristpocketEndpoint(private val player: Player) : HopperSource, HopperDestination {
	override fun getItems(): List<ItemStack> {
		val stack = player.wristpocket
		return if (stack.isEmpty) emptyList() else listOf(stack.copy())
	}

	override fun withdraw(stack: ItemStack, amount: Int): Boolean {
		val existing = player.wristpocket
		if (!ItemStack.isSameItem(existing, stack)) return false
		if (existing.count < amount) return false

		existing.shrink(amount)
		player.wristpocket = if (existing.isEmpty) ItemStack.EMPTY else existing
		return true
	}

	override fun deposit(stack: ItemStack): ItemStack {
		val current = player.wristpocket

		if (current.isEmpty) {
			val toInsert = stack.copy()
			val insertCount = toInsert.count.coerceAtMost(toInsert.maxStackSize)
			toInsert.count = insertCount
			player.wristpocket = toInsert
			return stack.copy().apply { shrink(insertCount) }.takeIf { !it.isEmpty } ?: ItemStack.EMPTY
		}

		if (ItemStack.isSameItemSameComponents(current, stack)) {
			val space = current.maxStackSize - current.count
			if (space > 0) {
				val toAdd = stack.count.coerceAtMost(space)
				current.grow(toAdd)
				stack.shrink(toAdd)
				player.wristpocket = current
			}
		}

		return stack
	}

	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> {
		val simulatedTransfers = LinkedHashMap<ItemStack, Int>()
		var current = player.wristpocket
		for (stack in stacks) {
			var remaining = stack.count
			if (current.isEmpty) {
				val toInsert = stack.count
				remaining -= toInsert
				current = stack.copyWithCount(toInsert)
			} else if (ItemStack.isSameItemSameComponents(current, stack)) {
				val toInsert = (current.maxStackSize - current.count).coerceAtLeast(0).coerceAtMost(stack.count)
				remaining -= toInsert
				current = stack.copyWithCount(current.count + toInsert)
			}
			if (remaining < stack.count)
				simulatedTransfers[stack] = stack.count - remaining
		}
		return simulatedTransfers
	}
}