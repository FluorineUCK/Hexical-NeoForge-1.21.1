package miyucomics.hexical.features.mage_blocks

import com.mojang.datafixers.util.Pair
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

interface MageBlockModifier {
	val type: MageBlockModifierType<*>
	fun serialize(): Tag
	fun getScryingLens(): Pair<ItemStack, Component>? = null
	fun tick(world: Level, pos: BlockPos, state: BlockState) {}
}