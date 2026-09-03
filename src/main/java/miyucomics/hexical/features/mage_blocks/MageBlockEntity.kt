package miyucomics.hexical.features.mage_blocks

import at.petrak.hexcasting.api.utils.putCompound
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup

class MageBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.MAGE_BLOCK_ENTITY, pos, state) {
	val modifiers: MutableMap<ResourceLocation, MageBlockModifier> = mutableMapOf()
	var disguise: BlockState = Blocks.AMETHYST_BLOCK.defaultBlockState()

	fun addModifier(modifier: MageBlockModifier) {
		modifiers[modifier.type.id] = modifier
		this.sync()
	}

	fun clearModifiers() {
		modifiers.clear()
		this.sync()
	}

	fun updateDisguise(state: BlockState) {
		this.disguise = state
		this.sync()
	}

	fun sync() {
		this.setChanged()
		this.level!!.updateNeighborsAt(worldPosition, HexicalBlocks.MAGE_BLOCK)
		this.level!!.sendBlockUpdated(this.blockPos, this.blockState, this.blockState, 3)
	}

	override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)
	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

	fun <T : MageBlockModifier> getModifier(type: MageBlockModifierType<T>): T = modifiers[type.id] as T
	fun hasModifier(type: MageBlockModifierType<*>) = modifiers.containsKey(type.id)

	fun addScryingLensLines(lines: MutableList<Pair<ItemStack, Component>>) {
		modifiers.forEach {
			val line = it.value.getScryingLens()
			if (line != null)
				lines.add(line)
		}
	}

	override fun saveAdditional(compound: CompoundTag, provider: HolderLookup.Provider) {
		super.saveAdditional(compound, provider)
		compound.putCompound("disguise", NbtUtils.writeBlockState(disguise))
		compound.putCompound("modifiers", CompoundTag().apply {
			modifiers.forEach {
				put(it.key.toString(), it.value.serialize())
			}
		})
	}

	override fun loadAdditional(compound: CompoundTag, provider: HolderLookup.Provider) {
		super.loadAdditional(compound, provider)
		this.disguise = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), compound.getCompound("disguise"))
		modifiers.clear()
		val serializedModifiers = compound.getCompound("modifiers")
		serializedModifiers.allKeys.forEach { key ->
			val id = ResourceLocation.tryParse(key) ?: return@forEach
			val modifierType = MageBlockModifierRegistry.MODIFIER_REGISTRY.get(id) ?: return@forEach
			val serialized = serializedModifiers.get(key) ?: return@forEach
			runCatching { modifierType.deserialize(serialized) }.getOrNull()?.let {
				modifiers[modifierType.id] = it
			}
		}
	}
}
