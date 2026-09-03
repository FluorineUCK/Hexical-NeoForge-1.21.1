package miyucomics.hexical.features.circle

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.TreeList
import miyucomics.hexical.HexicalMain
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class OutsideCircleMishap : Mishap() {
	override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.GREEN)
	override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component = error(HexicalMain.MOD_ID + ":outside_circle")
	override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>): TreeList<Iota> {
		if (env.castingEntity == null)
			return stack
		env.castingEntity!!.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60))
		return stack
	}
}
