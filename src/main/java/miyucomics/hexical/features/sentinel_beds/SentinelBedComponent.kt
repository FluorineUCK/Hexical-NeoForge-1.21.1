package miyucomics.hexical.features.sentinel_beds

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

class SentinelBedComponent(val env: CastingEnvironment) : CastingEnvironmentComponent.IsVecInRange {
	override fun getKey() = SentinelBedKey()
	class SentinelBedKey : CastingEnvironmentComponent.Key<CastingEnvironmentComponent.IsVecInRange>

	override fun onIsVecInRange(vec: Vec3, current: Boolean): Boolean {
		return current || SentinelBedPoi.isSentinelBed(env.world, BlockPos.containing(vec))
	}
}