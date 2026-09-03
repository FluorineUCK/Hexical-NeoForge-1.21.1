package miyucomics.hexical.features.pigments

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.util.FastColor
import net.minecraft.world.phys.Vec3

object OpSamplePigment : ConstMediaAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val pigment = args.getPigment(0, argc)
		val location = args.getVec3(1, argc)
		val time = args.getDouble(2, argc)
		val color = pigment.colorProvider.getColor(time.toFloat(), location)
		return Vec3(FastColor.ARGB32.red(color).toDouble() / 255.0, FastColor.ARGB32.green(color).toDouble() / 255.0, FastColor.ARGB32.blue(color).toDouble() / 255.0).asActionResult
	}
}
