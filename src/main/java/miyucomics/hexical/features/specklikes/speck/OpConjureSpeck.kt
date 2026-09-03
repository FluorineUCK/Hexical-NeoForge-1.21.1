package miyucomics.hexical.features.specklikes.speck

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalAdvancements
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3

object OpConjureSpeck : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(1, argc)
		env.assertVecInRange(position)
		return SpellAction.Result(Spell(position, args.getVec3(2, argc), args[0]), MediaConstants.DUST_UNIT / 100, listOf())
	}

	private data class Spell(val position: Vec3, val rotation: Vec3, val iota: Iota) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
			if (env.castingEntity is ServerPlayer) {
				HexicalAdvancements.AR.trigger(env.castingEntity as ServerPlayer)
				if (iota is PatternIota && iota.pattern == HexPattern.fromAngles("deaqq", HexDir.SOUTH_EAST))
					HexicalAdvancements.HEXXY.trigger(env.castingEntity as ServerPlayer)
			}

			val speck = SpeckEntity(env.world)
			speck.setPos(position.subtract(0.0, speck.eyeHeight.toDouble(), 0.0))
			speck.lookAt(EntityAnchorArgument.Anchor.FEET, speck.position().add(rotation))
			speck.setPigment(env.pigment)
			speck.setIota(iota)
			env.world.addFreshEntity(speck)

			return image.copy(stack = image.stack.appended(EntityIota(speck)))
		}
	}
}
