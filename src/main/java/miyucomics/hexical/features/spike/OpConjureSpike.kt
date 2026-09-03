package miyucomics.hexical.features.spike

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.world.entity.player.Player
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

object OpConjureSpike : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getBlockPos(0, argc)
		env.assertPosInRange(position)
		val offset = args.getBlockPos(1, argc)
		val direction = Direction.fromDelta(offset.x, offset.y, offset.z) ?: throw MishapInvalidIota.of(args[1], 1, "axis_vector")
		if (!env.world.getBlockState(position).isFaceSturdy(env.world, position, direction))
			throw MishapBadBlock.of(position, "solid_platform")
		if (env.world.getEntities(HexicalEntities.SPIKE_ENTITY, AABB.ofSize(Vec3.atCenterOf(position.offset(offset)), 0.9, 0.9, 0.9)) { true }.isNotEmpty())
			return SpellAction.Result(Noop(position), 0, listOf())
		val delay = floor(args.getPositiveDoubleUnderInclusive(2, 10.0, argc) * 20.0).toInt()
		val spawn = Vec3.atBottomCenterOf(position).add(Vec3.atLowerCornerOf(direction.normal))
		return SpellAction.Result(Spell(spawn, direction, delay), MediaConstants.DUST_UNIT, listOf(ParticleSpray.cloud(spawn, 1.0)))
	}

	private data class Noop(val position: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
	}

	private data class Spell(val position: Vec3, val direction: Direction, val delay: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val spike = SpikeEntity(env.world, position.x, position.y, position.z, direction, delay)
			val caster = env.castingEntity
			if (caster is Player)
				spike.setConjurer(caster)
			env.world.addFreshEntity(spike)
		}
	}
}