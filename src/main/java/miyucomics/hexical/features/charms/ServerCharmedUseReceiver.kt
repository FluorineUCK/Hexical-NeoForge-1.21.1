package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.utils.TreeList
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.CharmedItemUsePayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object ServerCharmedUseReceiver : InitHook() {
	override fun init() = Unit

	@JvmStatic
	fun handle(player: ServerPlayer, payload: CharmedItemUsePayload) {
		val hand = InteractionHand.entries.getOrNull(payload.hand) ?: return
		val stack = player.getItemInHand(hand)
		val vm = CastingVM(CastingImage().copy(stack = TreeList.from(payload.inputMethod.asActionResult)), CharmCastEnv(player, hand, stack))
		vm.queueExecuteAndWrapIotas(CharmUtilities.getHex(stack, player.serverLevel()), player.serverLevel())
		if (stack.item is CurioItem)
			(stack.item as CurioItem).postCharmCast(player, stack, hand, player.serverLevel(), vm.image.stack)
	}
}
