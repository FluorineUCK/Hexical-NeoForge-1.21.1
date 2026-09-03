package miyucomics.hexical.features.pedestal

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Vec3i
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import java.util.*
import kotlin.math.min

class PedestalBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(HexicalBlocks.PEDESTAL_BLOCK_ENTITY, pos, state), Container {
	var heldStack: ItemStack = ItemStack.EMPTY
	private var heldEntity: ItemEntity? = null
	private var persistentUUID: UUID? = null
	val normalVector: Vec3i = blockState.getValue(PedestalBlock.FACING).normal

	fun onBlockBreak() {
		heldEntity?.discard()
	}

	fun onBlockPlace() {
		val level = this.level ?: return
		val entityId = UUID.randomUUID()
		this.persistentUUID = entityId
		val position = Vec3.atCenterOf(this.worldPosition)
		val entity = ItemEntity(level, position.x, position.y, position.z, this.heldStack)
		entity.setUUID(entityId)
		this.heldEntity = entity
		configureItemEntity()
		level.addFreshEntity(entity)
	}

	fun onUse(player: Player, hand: InteractionHand) {
		val playerStack = player.getItemInHand(hand)

		// if can merge, merge
		if (ItemStack.isSameItemSameComponents(this.heldStack, playerStack)) {
			if (!level!!.isClientSide) {
				val amount = min(this.heldStack.maxStackSize - this.heldStack.count, playerStack.count)
				this.heldStack.grow(amount)
				playerStack.shrink(amount)
				updateItemEntity()
			}
			setChanged()
			return
		}

		// else just swap with player hand
		player.setItemInHand(hand, this.heldStack)
		this.heldStack = playerStack
		updateItemEntity()
		setChanged()
	}

	fun tick(world: Level) {
		if (world.isClientSide)
			return

		tryFillItemEntity()
		updateItemStack()
		suckOrMergeItems()
		updateItemEntity()
		configureItemEntity()
	}

	private fun suckOrMergeItems() {
		level!!.getEntitiesOfClass(ItemEntity::class.java, AABB.of(BoundingBox(this.worldPosition)).deflate(0.1)) { it.uuid != persistentUUID && !it.isRemoved && !it.hasPickUpDelay() }.forEach { item ->
			val stack = item.item
			if (this.heldStack.isEmpty) {
				this.heldStack = stack.copyAndClear()
				item.discard()
				updateItemEntity()
				setChanged()
				return
			}

			if (ItemStack.isSameItemSameComponents(this.heldStack, stack)) {
				val toTransfer = min(this.heldStack.maxStackSize - this.heldStack.count, stack.count)
				this.heldStack.grow(toTransfer)
				stack.shrink(toTransfer)
				updateItemEntity()
				setChanged()
				return
			}
		}
	}

	fun modifyImage(image: CastingImage): CastingImage {
		val data = IXplatAbstractions.INSTANCE.findDataHolder(heldStack) ?: return image
		val iota = data.readIota() ?: return image
		return if (image.parenCount == 0) {
			image.copy(stack = image.stack.appended(iota))
		} else {
			image.copy(parenthesized = image.parenthesized.appended(ParenthesizedIota(iota, false)))
		}
	}

	override fun getContainerSize() = 1
	override fun isEmpty() = heldStack.isEmpty
	override fun getItem(slot: Int): ItemStack = if (slot == 0) heldStack else ItemStack.EMPTY
	override fun setItem(slot: Int, stack: ItemStack) {
		if (slot == 0) {
			heldStack = stack
			updateItemEntity()
			setChanged()
		}
	}

	override fun removeItemNoUpdate(slot: Int): ItemStack = if (slot == 0) {
		val removed = heldStack
		heldStack = ItemStack.EMPTY
		updateItemEntity()
		setChanged()
		removed
	} else ItemStack.EMPTY

	override fun removeItem(slot: Int, amount: Int): ItemStack = if (slot == 0) {
		val removed = heldStack.split(amount)
		updateItemEntity()
		setChanged()
		removed
	} else ItemStack.EMPTY

	override fun stillValid(player: Player) = false
	override fun clearContent() {
		heldStack = ItemStack.EMPTY
		updateItemEntity()
		setChanged()
	}

	private fun updateItemStack() {
		if (level!!.isClientSide) return
		if (this.heldEntity == null || this.heldEntity!!.isRemoved) {
			// The serialized stack is authoritative while its display entity is
			// absent (for example during chunk/entity load ordering). The entity
			// is recreated below instead of erasing the pedestal's contents.
			return
		}
		this.heldStack = this.heldEntity!!.item
		setChanged()
	}

	private fun updateItemEntity() {
		if (level!!.isClientSide)
			return

		if (this.heldStack.isEmpty) {
			this.heldEntity?.discard()
			this.heldEntity = null
			setChanged()
			return
		}

		val serverWorld = this.level as? ServerLevel ?: return

		if (!tryFillItemEntity()) {
			val entityId = persistentUUID ?: UUID.randomUUID().also { persistentUUID = it }
			val position = Vec3.atCenterOf(this.worldPosition)
			val entity = ItemEntity(serverWorld, position.x, position.y, position.z, this.heldStack)
			entity.setUUID(entityId)
			this.heldEntity = entity
			configureItemEntity()
			serverWorld.addFreshEntity(entity)
		}

		this.heldEntity?.item = this.heldStack
	}

	fun tryFillItemEntity(): Boolean {
		if (this.persistentUUID == null)
			this.persistentUUID = UUID.randomUUID()
		if (this.heldEntity != null && !this.heldEntity!!.isRemoved)
			return true
		this.heldEntity = null
		val serverWorld = this.level as? ServerLevel ?: return false
		val entityId = persistentUUID ?: return false
		val existing = serverWorld.getEntity(entityId)
		if (existing is ItemEntity) {
			this.heldEntity = existing
			return true
		}
		return false
	}

	fun configureItemEntity() {
		this.heldEntity?.let {
			it.setPos(getItemPosition().subtract(Vec3.atLowerCornerOf(normalVector).scale(0.1)))
			it.boundingBox = AABB(getItemPosition(), getItemPosition()).inflate(0.25)
			it.noPhysics = true
			it.setUnlimitedLifetime()
			it.setNoGravity(true)
			it.isInvisible = true
			it.isInvulnerable = true
			it.deltaMovement = Vec3.ZERO
			it.setNeverPickUp()
		}
	}

	override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
		super.loadAdditional(nbt, provider)
		heldStack = ItemStack.parseOptional(provider, nbt.getCompound("item"))
		if (nbt.hasUUID("persistent_uuid"))
			persistentUUID = nbt.getUUID("persistent_uuid")
	}

	override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
		super.saveAdditional(nbt, provider)
		nbt.put("item", heldStack.saveOptional(provider))
		persistentUUID?.let { nbt.putUUID("persistent_uuid", it) }
	}

	override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)
	override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

	override fun setChanged() {
		level?.sendBlockUpdated(worldPosition, blockState, blockState, 3)
		super.setChanged()
	}

	fun getItemPosition(): Vec3 = Vec3.atCenterOf(this.worldPosition).add(Vec3.atLowerCornerOf(normalVector).scale(HEIGHT))

	companion object {
		const val HEIGHT = 0.75
	}
}
