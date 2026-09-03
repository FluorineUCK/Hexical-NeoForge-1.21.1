package miyucomics.hexical.features.evocation

import dev.kosmx.playerAnim.api.TransformType
import dev.kosmx.playerAnim.api.layered.IAnimation
import dev.kosmx.playerAnim.core.util.Vec3f
import miyucomics.hexical.ClientStorage
import net.minecraft.world.entity.player.Player
import net.minecraft.util.Mth

class EvocationAnimation(val player: Player) : IAnimation {
	override fun setupAnim(tickDelta: Float) {}
	override fun isActive() = player.evocationActive
	override fun get3DTransform(modelName: String, type: TransformType, tickDelta: Float, original: Vec3f): Vec3f {
		val rotation = -Mth.PI * (1 + Mth.sin(ClientStorage.ticks.toFloat() + tickDelta) / 6)
		if (modelName == "leftArm" && type == TransformType.ROTATION)
			return Vec3f(rotation, 0f, Mth.PI / 6)
		if (modelName == "rightArm" && type == TransformType.ROTATION)
			return Vec3f(rotation, 0f, -Mth.PI / 6)
		return original
	}
}