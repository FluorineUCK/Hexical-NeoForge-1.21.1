package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.server.level.ServerPlayer

object OpSetArchLampStorage : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val iota = args[0]
		CastingUtils.assertNoTruename(iota, env)

		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			throw MishapBadCaster()
		if (!hasActiveArchLamp(caster))
			throw NeedsArchLampMishap()
		caster.getArchLampField().storage = miyucomics.hexical.hexcompat.serializeIota(iota)

		return emptyList()
	}
}