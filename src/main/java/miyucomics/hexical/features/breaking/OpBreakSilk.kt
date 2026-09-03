package miyucomics.hexical.features.breaking

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.utils.isCorrectTierForDrops
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.level.block.Block
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries

object OpBreakSilk : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val pos = args.getBlockPos(0, argc)
		env.assertPosInRange(pos)
		return SpellAction.Result(Spell(pos), MediaConstants.DUST_UNIT / 2, listOf())
	}

	private data class Spell(val pos: BlockPos) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val state = env.world.getBlockState(pos)
			val tier = HexConfig.server().opBreakHarvestLevel()
			if (
				!state.isAir
                && state.getDestroySpeed(env.world, pos) >= 0f
                && isCorrectTierForDrops(tier, state)
                && IXplatAbstractions.INSTANCE.isBreakingAllowed(env.world, pos, state, env.castingEntity as? ServerPlayer)
			) {
				val tool = ItemStack(Items.DIAMOND_PICKAXE)
				tool.enchant(env.world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH), 1)
				Block.dropResources(state, env.world, pos, null, null, tool)
				env.world.destroyBlock(pos, false)
			}
		}
	}
}
