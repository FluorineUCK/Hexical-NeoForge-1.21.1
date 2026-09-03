package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Rarity
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import miyucomics.hexical.hexcompat.ItemStackDataCompat

class HandLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.RARE)) {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = user.getItemInHand(hand)
		if (!hasHex(stack)) return InteractionResultHolder.fail(stack)
		world.playLocalSound(user.x, user.y, user.z, HexicalSounds.LAMP_ACTIVATE, SoundSource.MASTER, 1f, 1f, true)
		if (!world.isClientSide) {
			ItemStackDataCompat.update(stack) { data ->
				data.put("position", user.eyePosition.serializeToNBT())
				data.put("rotation", user.lookAngle.serializeToNBT())
				data.put("velocity", user.deltaMovement.serializeToNBT())
				data.put("storage", miyucomics.hexical.hexcompat.serializeIota(NullIota()))
				data.putLong("start_time", world.gameTime)
			}
		}
		user.startUsingItem(hand)
		return InteractionResultHolder.success(stack)
	}

	override fun onUseTick(world: Level, user: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
		if (world.isClientSide) return
		if (getMedia(stack) == 0L) return
		val vm = CastingVM(CastingImage(), HandLampCastEnv(user as ServerPlayer, InteractionHand.MAIN_HAND, false, stack))
		vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, world as ServerLevel)!!, world)
	}

	override fun releaseUsing(stack: ItemStack, world: Level, user: LivingEntity, remainingUseTicks: Int) {
		if (!world.isClientSide) {
			val vm = CastingVM(CastingImage(), HandLampCastEnv(user as ServerPlayer, InteractionHand.MAIN_HAND, true, stack))
			vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, world as ServerLevel)!!, world)
		}
		world.playLocalSound(user.x, user.y, user.z, HexicalSounds.LAMP_DEACTIVATE, SoundSource.MASTER, 1f, 1f, true)
	}

	override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = Int.MAX_VALUE
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun getUseAnimation(stack: ItemStack) = UseAnim.BOW
	override fun canRecharge(stack: ItemStack?) = false
	override fun breakAfterDepletion() = false
	override fun cooldown() = 0
}
