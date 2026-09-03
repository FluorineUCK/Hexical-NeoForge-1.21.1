package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

object OpConjureHexburst : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)
		val iota = args[1]
		CastingUtils.assertNoTruename(iota, env)
		return SpellAction.Result(Spell(position, iota), MediaConstants.DUST_UNIT, listOf(ParticleSpray.burst(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val iota: Iota) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val stack = ItemStack(HexicalItems.HEXBURST_ITEM, 1)
			ItemStackDataCompat.update(stack) {
				it.put("iota", miyucomics.hexical.hexcompat.serializeIota(iota))
			}
			env.world.addFreshEntity(ItemEntity(env.world, position.x, position.y, position.z, stack))
		}
	}
}
