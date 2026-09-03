package miyucomics.hexical.features.pigments

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.pigment.FrozenPigment
import com.mojang.serialization.MapCodec
import miyucomics.hexical.misc.TextUtilities.getPigmentedText
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

class PigmentIota(val pigment: FrozenPigment) : Iota({ TYPE }) {
	override fun isTruthy() = true
	override fun toleratesOther(that: Iota) =
		typesMatch(this, that) && that is PigmentIota &&
			pigment.owner == that.pigment.owner &&
			ItemStack.isSameItemSameComponents(pigment.item, that.pigment.item)
	override fun display(): Component = getPigmentedText(pigment.item.hoverName.string, pigment)
	override fun hashCode(): Int = 31 * ItemStack.hashItemAndComponents(pigment.item) + pigment.owner.hashCode()

	companion object {
		val TYPE: IotaType<PigmentIota> = object : IotaType<PigmentIota>() {
			override fun codec(): MapCodec<PigmentIota> = CODEC
			override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, PigmentIota> = STREAM_CODEC
			override fun color() = 0xff_c466e3.toInt()
		}

		private val CODEC: MapCodec<PigmentIota> = FrozenPigment.CODEC
			.fieldOf("pigment")
			.xmap(::PigmentIota, PigmentIota::pigment)

		private val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PigmentIota> =
			FrozenPigment.STREAM_CODEC.map(::PigmentIota, PigmentIota::pigment)
	}
}

fun List<Iota>.getPigment(idx: Int, argc: Int = 0): FrozenPigment {
	val x = getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, size) }
	if (x is PigmentIota) return x.pigment
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "pigment")
}
