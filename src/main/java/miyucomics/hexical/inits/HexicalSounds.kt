package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import net.minecraft.core.registries.Registries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalSounds {
	private val SOUNDS = linkedMapOf<net.minecraft.resources.ResourceLocation, SoundEvent>()
	val AMETHYST_MELT: SoundEvent = register("amethyst_melt")
	val ITEM_DUNKS: SoundEvent = register("item_dunks")
	val EVOKING_MURMUR: SoundEvent = register("evoking_murmur")
	val EVOKING_CAST: SoundEvent = register("evoking_casts")
	val LAMP_ACTIVATE: SoundEvent = register("lamp_activate")
	val LAMP_DEACTIVATE: SoundEvent = register("lamp_deactivate")
	@JvmField
	val SUDDEN_REALIZATION: SoundEvent = register("sudden_realization")
	val REPLENISH_AIR: SoundEvent = register("replenish_air")
	val SCARAB_CHIRPS: SoundEvent = register("scarab_chirps")
	val HANDBELL_CHIMES: SoundEvent = register("handbell_chimes")

	fun register(event: RegisterEvent) {
		if (event.registryKey != Registries.SOUND_EVENT) return
		SOUNDS.forEach { (id, sound) -> event.register(Registries.SOUND_EVENT, id) { sound } }
	}

	private fun register(name: String): SoundEvent {
		val id = HexicalMain.id(name)
		val event = SoundEvent.createVariableRangeEvent(id)
		check(SOUNDS.put(id, event) == null) { "Duplicate Hexical sound id: $id" }
		return event
	}
}
