package miyucomics.hexical.network

import miyucomics.hexical.HexicalMain
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import io.netty.handler.codec.DecoderException
import java.util.UUID

private fun <T> payloadCodec(
	encode: (RegistryFriendlyByteBuf, T) -> Unit,
	decode: (RegistryFriendlyByteBuf) -> T
): StreamCodec<RegistryFriendlyByteBuf, T> = StreamCodec.of(encode, decode)

data class CharmedItemUsePayload(val inputMethod: Int, val hand: Int) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<CharmedItemUsePayload>(HexicalMain.id("charmed_item"))
		@JvmField val STREAM_CODEC = payloadCodec<CharmedItemUsePayload>(
			{ buf, value -> buf.writeVarInt(value.inputMethod); buf.writeVarInt(value.hand) },
			{ buf -> CharmedItemUsePayload(buf.readVarInt(), buf.readVarInt()) }
		)
	}
}

data class KeyStatePayload(val key: String, val pressed: Boolean) : CustomPacketPayload {
	override fun type() = if (pressed) PRESSED_TYPE else RELEASED_TYPE
	companion object {
		@JvmField val PRESSED_TYPE = CustomPacketPayload.Type<KeyStatePayload>(HexicalMain.id("press_key"))
		@JvmField val RELEASED_TYPE = CustomPacketPayload.Type<KeyStatePayload>(HexicalMain.id("release_key"))
		@JvmField val PRESSED_STREAM_CODEC = payloadCodec<KeyStatePayload>(
			{ buf, value -> buf.writeUtf(value.key, 256) },
			{ buf -> KeyStatePayload(buf.readUtf(256), true) }
		)
		@JvmField val RELEASED_STREAM_CODEC = payloadCodec<KeyStatePayload>(
			{ buf, value -> buf.writeUtf(value.key, 256) },
			{ buf -> KeyStatePayload(buf.readUtf(256), false) }
		)
	}
}

data class ScrollPayload(val delta: Int) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<ScrollPayload>(HexicalMain.id("scroll"))
		@JvmField val STREAM_CODEC = payloadCodec<ScrollPayload>(
			{ buf, value -> buf.writeVarInt(value.delta) },
			{ buf -> ScrollPayload(buf.readVarInt()) }
		)
	}
}

data class ConfettiPayload(val seed: Long, val pos: Vec3, val direction: Vec3, val speed: Double) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<ConfettiPayload>(HexicalMain.id("confetti"))
		@JvmField val STREAM_CODEC = payloadCodec<ConfettiPayload>(
			{ buf, value ->
				buf.writeLong(value.seed)
				buf.writeVec3(value.pos)
				buf.writeVec3(value.direction)
				buf.writeDouble(value.speed)
			},
			{ buf -> ConfettiPayload(buf.readLong(), buf.readVec3(), buf.readVec3(), buf.readDouble()) }
		)
	}
}

data class PlayerUuidPayload(val playerId: UUID, private val payloadType: CustomPacketPayload.Type<PlayerUuidPayload>) : CustomPacketPayload {
	override fun type() = payloadType
	companion object {
		@JvmField val HANDBELL_TYPE = CustomPacketPayload.Type<PlayerUuidPayload>(HexicalMain.id("handbell"))
		@JvmField val EVOCATION_START_TYPE = CustomPacketPayload.Type<PlayerUuidPayload>(HexicalMain.id("start_evoking"))
		@JvmField val EVOCATION_END_TYPE = CustomPacketPayload.Type<PlayerUuidPayload>(HexicalMain.id("end_evoking"))

		private fun codec(type: CustomPacketPayload.Type<PlayerUuidPayload>) = payloadCodec<PlayerUuidPayload>(
			{ buf, value -> buf.writeUUID(value.playerId) },
			{ buf -> PlayerUuidPayload(buf.readUUID(), type) }
		)

		@JvmField val HANDBELL_STREAM_CODEC = codec(HANDBELL_TYPE)
		@JvmField val EVOCATION_START_STREAM_CODEC = codec(EVOCATION_START_TYPE)
		@JvmField val EVOCATION_END_STREAM_CODEC = codec(EVOCATION_END_TYPE)
	}
}

data class MediaLogPayload(val data: CompoundTag) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<MediaLogPayload>(HexicalMain.id("media_log"))
		@JvmField val STREAM_CODEC = payloadCodec<MediaLogPayload>(
			{ buf, value -> buf.writeNbt(value.data) },
			{ buf -> MediaLogPayload(buf.readNbt() ?: CompoundTag()) }
		)
	}
}

data class LesserSentinelsPayload(val positions: List<Vec3>) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<LesserSentinelsPayload>(HexicalMain.id("lesser_sentinels"))
		@JvmField val STREAM_CODEC = payloadCodec<LesserSentinelsPayload>(
			{ buf, value ->
				require(value.positions.size <= 1024) { "Too many lesser sentinels: ${value.positions.size}" }
				buf.writeVarInt(value.positions.size)
				value.positions.forEach(buf::writeVec3)
			},
			{ buf ->
				val count = buf.readVarInt()
				if (count !in 0..1024) throw DecoderException("Invalid lesser sentinel count: $count")
				LesserSentinelsPayload(List(count) { buf.readVec3() })
			}
		)
	}
}

data class ShaderPayload(val shader: ResourceLocation?) : CustomPacketPayload {
	override fun type() = TYPE
	companion object {
		@JvmField val TYPE = CustomPacketPayload.Type<ShaderPayload>(HexicalMain.id("shader"))
		@JvmField val STREAM_CODEC = payloadCodec<ShaderPayload>(
			{ buf, value ->
				buf.writeBoolean(value.shader != null)
				value.shader?.let(buf::writeResourceLocation)
			},
			{ buf -> ShaderPayload(if (buf.readBoolean()) buf.readResourceLocation() else null) }
		)
	}
}
