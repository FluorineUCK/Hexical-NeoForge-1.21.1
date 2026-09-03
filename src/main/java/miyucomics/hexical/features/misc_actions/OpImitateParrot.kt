package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.animal.Parrot
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import miyucomics.hexical.mixin.ParrotAccessor

object OpImitateParrot : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val pos = args.getVec3(0, argc)
		env.assertVecInRange(pos)
		val id = args.getIdentifier(1, argc)
		if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id))
			throw MishapInvalidIota.of(args[0], 0, "entity_id")
		return SpellAction.Result(Spell(pos, BuiltInRegistries.ENTITY_TYPE.get(id)), MediaConstants.DUST_UNIT / 2, listOf())
	}

	private data class Spell(val pos: Vec3, val type: EntityType<*>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val sound = ParrotAccessor.`hexical$invokeGetImitatedSound`(type)
			env.world.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.MASTER, 1.0f, Parrot.getPitch(env.world.random))
		}
	}
}
