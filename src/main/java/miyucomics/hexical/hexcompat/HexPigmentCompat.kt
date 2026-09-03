package miyucomics.hexical.hexcompat

import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.ItemLike

/** Isolates the pre34 -> pre39 supplier change in Hex Casting's pigment maps. */
object HexPigmentCompat {
	fun dyePigmentItem(dye: DyeColor): ItemLike =
		requireNotNull(HexItems.DYE_PIGMENTS[dye]) {
			"Hex Casting did not register a dye pigment for $dye"
		}.get()
}
