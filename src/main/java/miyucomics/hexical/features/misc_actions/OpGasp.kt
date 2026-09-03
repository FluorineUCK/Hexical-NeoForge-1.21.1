package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource

object OpGasp : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val target = args.getEntity(env.world, 0, argc)
		env.assertEntityInRange(target)
		return SpellAction.Result(Spell(target), MediaConstants.DUST_UNIT, listOf(ParticleSpray.cloud(target.position(), 1.0)))
	}

	private data class Spell(val target: Entity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			target.airSupply = target.maxAirSupply
			if (target is ServerPlayer)
				env.world.playLocalSound(target.x, target.y, target.z, HexicalSounds.REPLENISH_AIR, SoundSource.MASTER, 1f, 1f, true)
		}
	}
}