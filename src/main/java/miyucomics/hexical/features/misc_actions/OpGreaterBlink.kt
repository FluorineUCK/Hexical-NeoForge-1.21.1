package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

object OpGreaterBlink : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val caster = env.castingEntity ?: throw MishapBadCaster()

		val providedOffset = args.getVec3(0, argc)
		val straightAxis = caster.lookAngle

		val upPitch = (-caster.xRot + 90) * (Math.PI.toFloat() / 180)
		val yaw = -caster.yHeadRot * (Math.PI.toFloat() / 180)
		val h = Mth.cos(yaw).toDouble()
		val j = Mth.cos(upPitch).toDouble()
		val upAxis = Vec3(Mth.sin(yaw).toDouble() * j, Mth.sin(upPitch).toDouble(), h * j)

		val sideAxis = straightAxis.cross(upAxis).normalize()
		val worldOffset = Vec3.ZERO
			.add(sideAxis.scale(providedOffset.x))
			.add(upAxis.scale(providedOffset.y))
			.add(straightAxis.scale(providedOffset.z))

		val destination = caster.position().add(worldOffset)
		if (worldOffset.length() > 128)
			throw MishapBadLocation(destination)
		return SpellAction.Result(Spell(destination), MediaConstants.DUST_UNIT * 2, listOf(ParticleSpray.cloud(destination, 1.0)))
	}

	private data class Spell(val position: Vec3) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			env.castingEntity!!.teleportTo(position.x, position.y, position.z)
		}
	}
}