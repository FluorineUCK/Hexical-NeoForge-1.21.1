package miyucomics.hexical.features.mage_blocks

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.world.level.block.Block
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpDisguiseMageBlock : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getBlockPos(0, argc)
		env.assertPosInRange(position)
		if (env.world.getBlockState(position).block !is MageBlock)
			throw MishapBadBlock.of(position, "mage_block")

		val id = args.getIdentifier(1, argc)
		if (!BuiltInRegistries.BLOCK.containsKey(id))
			throw MishapInvalidIota.of(args[1], 0, "block_id")

		return SpellAction.Result(Spell(position, BuiltInRegistries.BLOCK.get(id)), 0, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
	}

	private data class Spell(val pos: BlockPos, val disguise: Block) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			if (env.castingEntity is ServerPlayer)
				HexicalAdvancements.DIY.trigger(env.castingEntity as ServerPlayer)
			(env.world.getBlockEntity(pos) as MageBlockEntity).updateDisguise(disguise.defaultBlockState())
		}
	}
}
