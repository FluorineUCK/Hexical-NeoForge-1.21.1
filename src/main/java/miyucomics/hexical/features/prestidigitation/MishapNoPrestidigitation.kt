package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.TreeList
import miyucomics.hexical.HexicalMain
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

class MishapNoPrestidigitation(val position: Vec3) : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.RED)
    override fun particleSpray(env: CastingEnvironment) = ParticleSpray.burst(position, 1.0)
    override fun errorMessage(env: CastingEnvironment, errorCtx: Context) = error(HexicalMain.MOD_ID + ":no_prestidigitation")
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: TreeList<Iota>): TreeList<Iota> = stack
}
