package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import net.minecraft.nbt.CompoundTag
import miyucomics.hexical.hexcompat.ItemStackDataCompat

class OpGetHandLampData(private val process: (CastingEnvironment, CompoundTag) -> List<Iota>) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env !is HandLampCastEnv)
			throw NeedsHandLampMishap()
		val stack = env.castingEntity!!.useItem
		if (!ItemStackDataCompat.hasCustomData(stack)) return listOf(NullIota())
		val nbt = ItemStackDataCompat.customData(stack)
		return process(env, nbt)
	}
}
