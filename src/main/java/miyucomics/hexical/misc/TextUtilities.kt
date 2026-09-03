package miyucomics.hexical.misc

import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec3
import java.math.RoundingMode
import java.text.DecimalFormat

object TextUtilities {
	val PERCENTAGE: DecimalFormat = DecimalFormat("####")
	val DUST_AMOUNT: DecimalFormat = DecimalFormat("###,###.##")

	init {
		PERCENTAGE.roundingMode = RoundingMode.DOWN
	}

	fun getPigmentedText(string: String, pigment: FrozenPigment, offset: Float = 0f): MutableComponent {
		return string.foldIndexed(Component.empty()) { index, acc, char -> acc.append(Component.literal(char.toString()).withStyle { it.withColor(pigment.colorProvider.getColor(offset, Vec3(0.0, index * 0.5, 0.0)) and 0x00FFFFFF) }) }
	}
}