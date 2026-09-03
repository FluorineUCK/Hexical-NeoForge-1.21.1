package miyucomics.hexical.features.curios

import dev.kosmx.playerAnim.api.TransformType
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.core.util.Vec3f
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.util.Mth

class HandbellCurioPlayerModel(val player: Player) : IAnimation {
	var shakingBellTimer = 0

	override fun setupAnim(tickDelta: Float) {}
	override fun isActive() = player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_HANDBELL) || player.getItemInHand(InteractionHand.OFF_HAND).`is`(HexicalItems.CURIO_HANDBELL)

	override fun tick() {
		if (shakingBellTimer > 0)
			shakingBellTimer -= 1
	}

	override fun get3DTransform(modelName: String, type: TransformType, tickDelta: Float, original: Vec3f): Vec3f {
		if (modelName == "rightArm" && type == TransformType.ROTATION) {
			val time = ClientStorage.ticks + tickDelta
			val pitch = Mth.sin(time * 0.08f + 0.8f) * 2f
			val yaw = Mth.sin(time * 0.06f + 0.2f) * 2f
			var roll = Mth.sin(time * 0.07f) * 2f

			if (shakingBellTimer > 0) {
				val progress = ((1f - shakingBellTimer + tickDelta) / 10f)
				val eased = (1f - Mth.cos(progress * Mth.PI)) / 2f
				roll += Mth.sin(eased * Mth.PI * 2) * 20f
			}
			return Vec3f(-90f + pitch, yaw, roll).scale(Mth.DEG_TO_RAD)
		}
		return original
	}
}
