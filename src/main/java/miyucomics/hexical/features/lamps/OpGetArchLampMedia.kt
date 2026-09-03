package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.server.level.ServerPlayer
import miyucomics.hexical.hexcompat.ItemStackDataCompat

object OpGetArchLampMedia : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return listOf(NullIota())
		if (!hasActiveArchLamp(caster))
			throw NeedsArchLampMishap()
		for (stack in caster.inventory.items)
			if (stack.item == HexicalItems.ARCH_LAMP_ITEM && ItemStackDataCompat.customData(stack).getBoolean("active"))
				return ((stack.item as ArchLampItem).getMedia(stack).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		val offhand = caster.offhandItem
		if (offhand.item == HexicalItems.ARCH_LAMP_ITEM && ItemStackDataCompat.customData(offhand).getBoolean("active"))
			return ((offhand.item as ArchLampItem).getMedia(offhand).toDouble() / MediaConstants.DUST_UNIT).asActionResult
		return listOf(NullIota())
	}
}
