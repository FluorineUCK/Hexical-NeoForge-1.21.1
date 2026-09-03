package miyucomics.hexical.features.dance

import dev.kosmx.playerAnim.api.TransformType
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.core.util.Vec3f
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.features.evocation.evocationActive
import net.minecraft.world.entity.player.Player
import net.minecraft.util.Mth

class DanceAnimation(val player: Player) : IAnimation {
	override fun setupAnim(tickDelta: Float) {}
	override fun isActive() = player.evocationActive
	override fun getFirstPersonMode(tickDelta: Float) = FirstPersonMode.THIRD_PERSON_MODEL
	override fun getFirstPersonConfiguration(tickDelta: Float): FirstPersonConfiguration = FirstPersonConfiguration().setShowLeftArm(true).setShowRightArm(true)
	override fun get3DTransform(modelName: String, type: TransformType, tickDelta: Float, original: Vec3f): Vec3f {
		val rotation = -Mth.PI * (1 + Mth.sin(ClientStorage.ticks.toFloat() + tickDelta) / 6)
		if (modelName == "leftArm" && type == TransformType.ROTATION)
			return Vec3f(rotation, 0f, Mth.PI / 6)
		if (modelName == "rightArm" && type == TransformType.ROTATION)
			return Vec3f(rotation, 0f, -Mth.PI / 6)
		return original
	}
}