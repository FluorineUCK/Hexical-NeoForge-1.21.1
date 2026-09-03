package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Rarity
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level
import miyucomics.hexical.hexcompat.ItemStackDataCompat

class ArchLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.EPIC)) {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		if (!hasHex(stack))
			return InteractionResultHolder.fail(stack)

		val active = ItemStackDataCompat.customData(stack).getBoolean("active")

		if (world.isClientSide) {
			world.playLocalSound(user.x, user.y, user.z, if (active) HexicalSounds.LAMP_DEACTIVATE else HexicalSounds.LAMP_ACTIVATE, SoundSource.MASTER, 1f, 1f, true)
			return InteractionResultHolder.success(stack)
		}

		if (active) {
			val vm = CastingVM(CastingImage(), ArchLampCastEnv(user as ServerPlayer, hand, true, stack))
			vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, world as ServerLevel)!!, world)
			ItemStackDataCompat.update(stack) { it.putBoolean("active", false) }
			return InteractionResultHolder.success(stack)
		}

		ItemStackDataCompat.update(stack) { it.putBoolean("active", true) }

		val state = user.getArchLampField()
		state.position = user.eyePosition
		state.rotation = user.lookAngle
		state.velocity = user.deltaMovement
		state.storage = miyucomics.hexical.hexcompat.serializeIota(NullIota())
		state.time = world.gameTime

		return InteractionResultHolder.success(stack)
	}

	override fun inventoryTick(stack: ItemStack, world: Level, user: Entity, slot: Int, selected: Boolean) {
		if (world.isClientSide) return
		if (getMedia(stack) == 0L) return
		if (user !is ServerPlayer) return
		if (!ItemStackDataCompat.customData(stack).getBoolean("active")) return
		if (user.gameMode.gameModeForPlayer == GameType.SPECTATOR) return
		val vm = CastingVM(CastingImage(), ArchLampCastEnv(user, InteractionHand.MAIN_HAND, false, stack))
		vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, world as ServerLevel)!!, world)
	}

	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = false
	override fun cooldown() = 0
}

fun hasActiveArchLamp(player: ServerPlayer): Boolean {
	for (stack in player.inventory.items)
		if (stack.item == HexicalItems.ARCH_LAMP_ITEM && ItemStackDataCompat.customData(stack).getBoolean("active"))
			return true
	for (stack in player.inventory.offhand)
		if (stack.item == HexicalItems.ARCH_LAMP_ITEM && ItemStackDataCompat.customData(stack).getBoolean("active"))
			return true
	return false
}
