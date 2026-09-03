package miyucomics.hexical.features.curios

import dev.kosmx.playerAnim.api.TransformType
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.core.util.Vec3f
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.entity.player.Player
import net.minecraft.world.InteractionHand
import net.minecraft.util.Mth

class FluteCurioPlayerModel(val player: Player) : IAnimation {
	override fun setupAnim(tickDelta: Float) {}
	override fun isActive() = player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_FLUTE) || player.getItemInHand(InteractionHand.OFF_HAND).`is`(HexicalItems.CURIO_FLUTE)
	override fun get3DTransform(modelName: String, type: TransformType, tickDelta: Float, original: Vec3f): Vec3f {
		val time = ClientStorage.ticks + tickDelta
		val pitch = Mth.sin(time * 0.08f + 0.8f)
		val yaw = Mth.sin(time * 0.06f + 0.2f)
		val roll = Mth.sin(time * 0.07f)
		val offset = Vec3f(pitch, yaw, roll)

		if (modelName == "rightArm" && type == TransformType.ROTATION)
			return Vec3f(320f, 0f, 100f).add(offset).scale(Mth.DEG_TO_RAD)
		if (modelName == "leftArm" && type == TransformType.ROTATION)
			return Vec3f(285f, 60f, 10f).add(offset).scale(Mth.DEG_TO_RAD)
		if (modelName == "rightItem" && type == TransformType.POSITION)
			return Vec3f(2f, 5f, 8f)
		if (modelName == "rightItem" && type == TransformType.ROTATION)
			return Vec3f(60f, 0f, -15f).scale(Mth.DEG_TO_RAD)
		return original
	}
}
