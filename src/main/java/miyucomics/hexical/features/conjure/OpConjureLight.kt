package miyucomics.hexical.features.conjure

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LightBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpConjureLight : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getBlockPos(0, argc)
		env.assertPosInRange(position)
		val power = args.getPositiveIntUnderInclusive(1, 15, argc)
		if (env.world.getBlockState(position).`is`(Blocks.LIGHT))
			return SpellAction.Result(Spell(position, power), 0, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
		if (!env.world.getBlockState(position).canBeReplaced())
			throw MishapBadBlock.of(position, "replaceable")
		return SpellAction.Result(Spell(position, power), MediaConstants.DUST_UNIT / 4, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
	}

	private data class Spell(val position: BlockPos, val power: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			env.world.setBlockAndUpdate(position, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, power))
		}
	}
}