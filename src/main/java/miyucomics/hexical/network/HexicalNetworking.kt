package miyucomics.hexical.network

import miyucomics.hexical.features.charms.ServerCharmedUseReceiver
import miyucomics.hexical.features.telepathy.ServerPeripheralReceiver
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext

object HexicalNetworking {
	/** Installed by the physical-client entrypoint; remains null on dedicated servers. */
	@Volatile var clientHandler: ((CustomPacketPayload) -> Unit)? = null

	fun register(event: RegisterPayloadHandlersEvent) {
		val registrar = event.registrar("1")
		registrar.playToServer(CharmedItemUsePayload.TYPE, CharmedItemUsePayload.STREAM_CODEC, ::handleCharmedItem)
		registrar.playToServer(KeyStatePayload.PRESSED_TYPE, KeyStatePayload.PRESSED_STREAM_CODEC, ::handleKeyState)
		registrar.playToServer(KeyStatePayload.RELEASED_TYPE, KeyStatePayload.RELEASED_STREAM_CODEC, ::handleKeyState)
		registrar.playToServer(ScrollPayload.TYPE, ScrollPayload.STREAM_CODEC, ::handleScroll)

		registrar.playToClient(ConfettiPayload.TYPE, ConfettiPayload.STREAM_CODEC, ::handleClient)
		registrar.playToClient(PlayerUuidPayload.HANDBELL_TYPE, PlayerUuidPayload.HANDBELL_STREAM_CODEC, ::handleClient)
		registrar.playToClient(PlayerUuidPayload.EVOCATION_START_TYPE, PlayerUuidPayload.EVOCATION_START_STREAM_CODEC, ::handleClient)
		registrar.playToClient(PlayerUuidPayload.EVOCATION_END_TYPE, PlayerUuidPayload.EVOCATION_END_STREAM_CODEC, ::handleClient)
		registrar.playToClient(MediaLogPayload.TYPE, MediaLogPayload.STREAM_CODEC, ::handleClient)
		registrar.playToClient(LesserSentinelsPayload.TYPE, LesserSentinelsPayload.STREAM_CODEC, ::handleClient)
		registrar.playToClient(ShaderPayload.TYPE, ShaderPayload.STREAM_CODEC, ::handleClient)
	}

	private fun handleCharmedItem(payload: CharmedItemUsePayload, context: IPayloadContext) {
		(context.player() as? ServerPlayer)?.let { player ->
			context.enqueueWork { ServerCharmedUseReceiver.handle(player, payload) }
		}
	}

	private fun handleKeyState(payload: KeyStatePayload, context: IPayloadContext) {
		(context.player() as? ServerPlayer)?.let { player ->
			context.enqueueWork { ServerPeripheralReceiver.handleKeyState(player, payload) }
		}
	}

	private fun handleScroll(payload: ScrollPayload, context: IPayloadContext) {
		(context.player() as? ServerPlayer)?.let { player ->
			context.enqueueWork { ServerPeripheralReceiver.handleScroll(player, payload.delta) }
		}
	}

	private fun <T : CustomPacketPayload> handleClient(payload: T, context: IPayloadContext) {
		context.enqueueWork { clientHandler?.invoke(payload) }
	}
}
