package miyucomics.hexical.misc

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.hexcompat.deserializeIota
import miyucomics.hexical.hexcompat.serializeIota
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel

object HexSerialization {
	fun serializeHex(hex: List<Iota>) = ListTag().also { out -> hex.forEach { out.add(serializeIota(it)) } }
	fun deserializeHex(list: ListTag, world: ServerLevel) = list.map { deserializeIota(it, world) ?: GarbageIota() }

	fun backwardsCompatibleReadHex(holder: CompoundTag, key: String, world: ServerLevel): List<Iota> {
		val element = holder.get(key) ?: return emptyList()
		if (element is CompoundTag) {
			val elementData = (deserializeIota(element, world) as? ListIota)?.list?.toList()
				?: return emptyList()
			holder.remove(key)
			holder.putList(key, serializeHex(elementData))
			return elementData
		}
		return if (element is ListTag) deserializeHex(element, world) else emptyList()
	}
}
