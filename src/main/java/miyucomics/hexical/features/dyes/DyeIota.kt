package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import io.netty.handler.codec.DecoderException
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.DyeColor

class DyeIota(val dye: DyeOption) : Iota({ TYPE }) {
	override fun isTruthy() = true
	override fun toleratesOther(that: Iota) = typesMatch(this, that) && that is DyeIota && dye == that.dye
	override fun display(): Component = dye.coloredText
	override fun hashCode(): Int = dye.hashCode()

	companion object {
		val TYPE: IotaType<DyeIota> = object : IotaType<DyeIota>() {
			override fun codec(): MapCodec<DyeIota> = CODEC
			override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DyeIota> = STREAM_CODEC
			override fun color() = -1
		}

		private val CODEC: MapCodec<DyeIota> = Codec.STRING
			.comapFlatMap(
				{ name ->
					DyeOption.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
						?.let { dye -> DataResult.success(dye) }
						?: DataResult.error { "Unknown Hexical dye '$name'" }
				},
				{ it.name.lowercase() }
			)
			.fieldOf("dye")
			.xmap(::DyeIota, DyeIota::dye)

		private val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DyeIota> = StreamCodec.of(
			{ buffer, value -> buffer.writeVarInt(value.dye.ordinal) },
			{ buffer ->
				val ordinal = buffer.readVarInt()
				val dye = DyeOption.entries.getOrNull(ordinal)
					?: throw DecoderException("Invalid Hexical dye ordinal $ordinal")
				DyeIota(dye)
			}
		)
	}
}

fun List<Iota>.getDye(idx: Int, argc: Int = 0): DyeOption {
	val x = getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, size) }
	if (x is DyeIota) return x.dye
	throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "dye")
}

fun List<Iota>.getColoredDye(idx: Int, argc: Int = 0): DyeColor {
	val dye = getDye(idx, argc)
	if (dye != DyeOption.UNCOLORED) return dye.dyeColor!!
	throw MishapInvalidIota.of(this[idx], if (argc == 0) idx else argc - (idx + 1), "colored_dye")
}
