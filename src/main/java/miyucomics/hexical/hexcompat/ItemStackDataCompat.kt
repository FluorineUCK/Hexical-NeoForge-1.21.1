package miyucomics.hexical.hexcompat

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

/** Bridges Hexical's 1.20 root-NBT payloads to Minecraft 1.21 data components. */
object ItemStackDataCompat {
	@JvmStatic
	fun customData(stack: ItemStack): CompoundTag =
		stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()

	@JvmStatic
	fun hasCustomData(stack: ItemStack): Boolean =
		!stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).isEmpty

	@JvmStatic
	fun contains(stack: ItemStack, key: String): Boolean =
		stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).contains(key)

	@JvmStatic
	fun update(stack: ItemStack, mutator: (CompoundTag) -> Unit) {
		val tag = customData(stack)
		mutator(tag)
		if (tag.isEmpty) stack.remove(DataComponents.CUSTOM_DATA)
		else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
	}

	@JvmStatic
	fun replace(stack: ItemStack, tag: CompoundTag?) {
		if (tag == null || tag.isEmpty) stack.remove(DataComponents.CUSTOM_DATA)
		else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag.copy()))
	}

	@JvmStatic
	fun blockEntityData(stack: ItemStack): CompoundTag? {
		val current = stack.get(DataComponents.BLOCK_ENTITY_DATA)
		if (current != null && !current.isEmpty) return current.copyTag()

		// Read-only migration fallback for stacks saved by Hexical 1.20.
		val legacy = customData(stack)
		return if (legacy.contains("BlockEntityTag", Tag.TAG_COMPOUND.toInt()))
			legacy.getCompound("BlockEntityTag")
		else null
	}

	@JvmStatic
	fun setBlockEntityData(stack: ItemStack, tag: CompoundTag) {
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag.copy()))
	}
}
