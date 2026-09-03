package miyucomics.hexical.features.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.curios.curios.BaseCurio
import miyucomics.hexical.features.curios.curios.CompassCurio
import miyucomics.hexical.features.curios.curios.FluteCurio
import miyucomics.hexical.features.curios.curios.HandbellCurio
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand

abstract class CurioItem : Item(Properties().stacksTo(1)) {
	open fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {}

	companion object {
		private val specialCurios = mapOf(
			"compass" to CompassCurio,
			"handbell" to HandbellCurio,
			"flute" to FluteCurio
		)

		fun getCurioFromName(name: String): CurioItem {
			if (specialCurios.containsKey(name))
				return specialCurios[name]!!
			return BaseCurio()
		}
	}
}