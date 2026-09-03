package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.hexcompat.deserializePigment
import miyucomics.hexical.hexcompat.serializePigment
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup

class HexCandleCakeBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.HEX_CANDLE_CAKE_BLOCK_ENTITY, pos, state) {
	private var pigment: FrozenPigment = FrozenPigment.DEFAULT.get()

	fun getPigment() = this.pigment
	fun setPigment(pigment: FrozenPigment) {
		this.pigment = pigment
		setChanged()
	}

	override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
		super.saveAdditional(nbt, provider)
		nbt.put("pigment", serializePigment(pigment))
	}

	override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
		super.loadAdditional(nbt, provider)
		pigment = deserializePigment(nbt.getCompound("pigment")) ?: FrozenPigment.DEFAULT.get()
	}

	override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)
	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)
}
