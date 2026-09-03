package miyucomics.hexical.misc

import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.phys.Vec2
import kotlin.math.max

object PatternUtils {
	fun getNormalizedStrokes(pattern: HexPattern, flipHor: Boolean = false): List<Vec2> {
		val lines = pattern.toLines(1f, pattern.getCenter(1f).negated()).toMutableList()
		val scaling = max(
			lines.maxBy { vector -> vector.x }.x - lines.minBy { vector -> vector.x }.x,
			lines.maxBy { vector -> vector.y }.y - lines.minBy { vector -> vector.y }.y
		)
		val xScale = if (flipHor) -1 else 1
		for (i in lines.indices)
			lines[i] = Vec2(lines[i].x * xScale, -lines[i].y).scale(1 / scaling)
		return lines.toList()
	}
}