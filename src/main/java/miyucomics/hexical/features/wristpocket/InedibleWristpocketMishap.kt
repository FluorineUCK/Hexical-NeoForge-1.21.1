package miyucomics.hexical.features.wristpocket

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.TreeList
import miyucomics.hexical.HexicalMain
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class InedibleWristpocketMishap : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.YELLOW)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component = error(HexicalMain.MOD_ID + ":inedible_wristpocket")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>): TreeList<Iota> = stack
}
