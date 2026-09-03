package miyucomics.hexical.misc

import at.petrak.hexcasting.api.HexAPI
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage.ParenthesizedIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object CastingUtils {
	fun assertNoTruename(iota: Iota, env: CastingEnvironment) {
		MishapOthersName
			.getTrueNameMishapFromDatum(env.world, iota, env.castingEntity as? ServerPlayer)
			?.let { throw it }
	}

	fun isEnlightened(player: ServerPlayer): Boolean {
		val advancement = player.server.advancements[HexAPI.modLoc("enlightenment")] ?: return false
		val tracker = player.advancements
		return tracker.getOrStartProgress(advancement).isDone
	}

	@JvmStatic
	fun giveIota(player: ServerPlayer, iota: Iota) {
		val image = IXplatAbstractions.INSTANCE.getStaffcastVM(player, InteractionHand.MAIN_HAND).image
		val newImage = if (image.parenCount == 0) {
			image.copy(stack = image.stack.appended(iota))
		} else {
			image.copy(parenthesized = image.parenthesized.appended(ParenthesizedIota(iota, false)))
		}
		IXplatAbstractions.INSTANCE.setStaffcastImage(player, newImage)
	}
}
