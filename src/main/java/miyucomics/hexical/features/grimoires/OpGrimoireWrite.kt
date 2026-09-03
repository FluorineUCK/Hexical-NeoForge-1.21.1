package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

object OpGrimoireWrite : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")

		val stack = itemInfo.stack
		val root = ItemStackDataCompat.customData(stack)
		if (root.contains("expansions") && root.getCompound("expansions").size() > 512)
			throw MishapBadOffhandItem.of(null, "nonfull_grimoire")

		OpGrimoireIndex.populateGrimoireMetadata(stack)
		CastingUtils.assertNoTruename(args[1], env)

		return SpellAction.Result(Spell(stack, args.getPattern(0, argc), args.getList(1, argc).toList()), 0, listOf())
	}

	private data class Spell(val stack: ItemStack, val key: HexPattern, val expansion: List<Iota>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			ItemStackDataCompat.update(stack) { root ->
				val expansions = if (root.contains("expansions")) root.getCompound("expansions") else CompoundTag()
				expansions.put(key.anglesSignature(), HexSerialization.serializeHex(expansion))
				root.put("expansions", expansions)

				val metadata = if (root.contains("metadata")) root.getCompound("metadata") else CompoundTag()
				val data = CompoundTag()
				data.putInt("direction", key.startDir.ordinal)
				metadata.put(key.anglesSignature(), data)
				root.put("metadata", metadata)
			}
		}
	}
}
