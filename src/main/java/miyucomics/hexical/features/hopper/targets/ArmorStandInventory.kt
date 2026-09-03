package miyucomics.hexical.features.hopper.targets

import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.EquipmentSlot

class ArmorStandInventory(private val stand: ArmorStand) : Container {
	private val slots = arrayOf(
		EquipmentSlot.FEET,
		EquipmentSlot.LEGS,
		EquipmentSlot.CHEST,
		EquipmentSlot.HEAD,
		EquipmentSlot.MAINHAND,
		EquipmentSlot.OFFHAND
	)

	override fun getItem(slot: Int): ItemStack {
		if (slot !in slots.indices) throw IndexOutOfBoundsException("$slot out of bounds for ArmorStandInventory")
		return stand.getItemBySlot(slots[slot])
	}

	override fun removeItem(slot: Int, amount: Int): ItemStack {
		val stack = getItem(slot)
		return if (stack.isEmpty) ItemStack.EMPTY else stack.split(amount)
	}

	override fun removeItemNoUpdate(slot: Int): ItemStack {
		val result = getItem(slot)
		setItem(slot, ItemStack.EMPTY)
		return result
	}

	override fun setItem(slot: Int, stack: ItemStack) {
		if (slot !in slots.indices) throw IndexOutOfBoundsException("$slot out of bounds for ArmorStandInventory")
		stand.setItemSlot(slots[slot], stack)
	}

	override fun clearContent() {
		for (slot in slots) stand.setItemSlot(slot, ItemStack.EMPTY)
	}

	override fun getContainerSize() = 6
	override fun setChanged() {}
	override fun stillValid(player: Player): Boolean = true
	override fun isEmpty() = slots.all { stand.getItemBySlot(it).isEmpty }
}
