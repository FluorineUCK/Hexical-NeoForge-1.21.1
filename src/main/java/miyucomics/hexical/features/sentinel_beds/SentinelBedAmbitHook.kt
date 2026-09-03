package miyucomics.hexical.features.sentinel_beds

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import miyucomics.hexical.misc.InitHook
import net.minecraft.nbt.CompoundTag

object SentinelBedAmbitHook : InitHook() {
	override fun init() {
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment, _: CompoundTag ->
			env.addExtension(SentinelBedComponent(env))
		}
	}
}