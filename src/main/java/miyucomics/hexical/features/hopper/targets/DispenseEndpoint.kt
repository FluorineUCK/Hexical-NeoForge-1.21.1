package miyucomics.hexical.features.hopper.targets

import miyucomics.hexical.features.hopper.HopperDestination
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3

class DispenseEndpoint(private val pos: Vec3, private val world: ServerLevel) : HopperDestination {
	override fun simulateDeposits(stacks: List<ItemStack>): Map<ItemStack, Int> = stacks.associateBy({it}, {it.count})

	override fun deposit(stack: ItemStack): ItemStack {
		if (stack.isEmpty)
			return ItemStack.EMPTY
		val dropped = stack.copy()
		val entity = ItemEntity(world, pos.x, pos.y, pos.z, dropped)
		entity.setDefaultPickUpDelay()
		world.addFreshEntity(entity)
		return ItemStack.EMPTY
	}
}