package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.item.ItemStack
import miyucomics.hexical.hexcompat.ItemStackDataCompat

object OpGrimoireErase : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")
		val stack = itemInfo.stack
		OpGrimoireIndex.populateGrimoireMetadata(stack)
		val pattern = args.getPattern(0, argc)
		return SpellAction.Result(Spell(stack, pattern), 0, listOf())
	}

	private data class Spell(val stack: ItemStack, val key: HexPattern) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			ItemStackDataCompat.update(stack) { root ->
				if (!root.contains("expansions")) return@update
				val expansions = root.getCompound("expansions")
				expansions.remove(key.anglesSignature())
				root.put("expansions", expansions)
				if (root.contains("metadata")) {
					val metadata = root.getCompound("metadata")
					metadata.remove(key.anglesSignature())
					root.put("metadata", metadata)
				}
			}
		}
	}
}
