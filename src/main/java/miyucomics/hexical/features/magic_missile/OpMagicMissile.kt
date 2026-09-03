package miyucomics.hexical.features.magic_missile

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.Entity
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.abs

object OpMagicMissile : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = getSpawnPosition(env, args.getVec3(0, argc))
		env.assertVecInRange(position)
		return SpellAction.Result(Spell(position, args.getVec3(1, argc)), MediaConstants.DUST_UNIT, listOf(ParticleSpray.cloud(position, 1.0)))
	}

	private data class Spell(val position: Vec3, val velocity: Vec3) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val missile = MagicMissileEntity(env.world)
			missile.setPosRaw(position.x, position.y, position.z)
			missile.owner = env.castingEntity
			env.world.addFreshEntity(missile)
			missile.tick()
			missile.setDeltaMovement(velocity.x, velocity.y, velocity.z)
		}
	}

	private fun getSpawnPosition(env: CastingEnvironment, relative: Vec3): Vec3 {
		if (env is CircleCastEnv) {
			val impetus = env.impetus ?: throw NoSpatialAxisMishap()
			val (straightAxis, upAxis) = getAxisForCircle(impetus)
			return Vec3.atCenterOf(impetus.blockPos)
				.add(straightAxis.cross(upAxis).normalize().scale(relative.x))
				.add(upAxis.scale(relative.y))
				.add(straightAxis.scale(relative.z))
		}

		if (env.castingEntity != null) {
			val caster = env.castingEntity!!
			val (straightAxis, upAxis) = getAxisForLivingEntity(caster)
			return caster.eyePosition
				.add(straightAxis.cross(upAxis).normalize().scale(relative.x))
				.add(upAxis.scale(relative.y))
				.add(straightAxis.scale(relative.z))
		}

		throw NoSpatialAxisMishap()
	}

	private fun getAxisForCircle(impetus: BlockEntityAbstractImpetus): Pair<Vec3, Vec3> {
		val straightAxis = Vec3.atLowerCornerOf(impetus.startDirection.normal)
		val upAxis = Vec3.atLowerCornerOf(if (abs(
				straightAxis.dot(
					Vec3(
						0.0,
						1.0,
						0.0
					)
				)
			) > 0.9) Direction.NORTH.normal else Direction.UP.normal)
		return straightAxis to upAxis
	}

	private fun getAxisForLivingEntity(entity: Entity): Pair<Vec3, Vec3> {
		val straightAxis = entity.lookAngle
		val upPitch = (-entity.xRot + 90) * (Math.PI.toFloat() / 180)
		val yaw = -entity.yHeadRot * (Math.PI.toFloat() / 180)
		val j = Mth.cos(upPitch).toDouble()
		val upAxis = Vec3(
			Mth.sin(yaw).toDouble() * j,
			Mth.sin(upPitch).toDouble(),
			Mth.cos(yaw).toDouble() * j
		)
		return straightAxis to upAxis
	}
}
