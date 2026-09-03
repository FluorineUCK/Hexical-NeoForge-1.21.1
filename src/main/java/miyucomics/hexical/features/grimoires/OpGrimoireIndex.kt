package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag

object OpGrimoireIndex : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM) }
		if (itemInfo == null)
			throw MishapBadOffhandItem.of(null, "grimoire")

		val stack = itemInfo.stack
		populateGrimoireMetadata(stack)
		val metadata = ItemStackDataCompat.customData(stack).getCompound("metadata")

		val result = mutableListOf<PatternIota>()
		for (pattern in metadata.allKeys)
			result.add(PatternIota(HexPattern.fromAngles(pattern, HexDir.values()[metadata.getCompound(pattern).getInt("direction")])))
		return listOf(ListIota(result.toList()))
	}

	fun populateGrimoireMetadata(grimoire: ItemStack) {
		val root = ItemStackDataCompat.customData(grimoire)
		if (root.contains("metadata"))
			return
		val metadata = CompoundTag()
		val expansions = if (root.contains("expansions")) root.getCompound("expansions") else CompoundTag()
		for (key in expansions.allKeys) {
			val data = CompoundTag()
			data.putInt("direction", HexDir.EAST.ordinal)
			metadata.put(key, data)
		}
		root.put("metadata", metadata)
		ItemStackDataCompat.replace(grimoire, root)
	}
}
