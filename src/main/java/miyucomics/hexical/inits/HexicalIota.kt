package miyucomics.hexical.inits

import at.petrak.hexcasting.common.lib.HexRegistries
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.pigments.PigmentIota
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalIota {
	fun register(event: RegisterEvent) {
		event.register(HexRegistries.IOTA_TYPE, HexicalMain.id("dye")) { DyeIota.TYPE }
		event.register(HexRegistries.IOTA_TYPE, HexicalMain.id("pigment")) { PigmentIota.TYPE }
	}
}
