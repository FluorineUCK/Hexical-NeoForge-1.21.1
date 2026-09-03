package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.world.phys.Vec3
import net.minecraft.util.FastColor

object OpTranslateDye : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val dye = args.getColoredDye(0, argc).textureDiffuseColor
		return Vec3(
			FastColor.ARGB32.red(dye) / 255.0,
			FastColor.ARGB32.green(dye) / 255.0,
			FastColor.ARGB32.blue(dye) / 255.0
		).asActionResult
	}
}
