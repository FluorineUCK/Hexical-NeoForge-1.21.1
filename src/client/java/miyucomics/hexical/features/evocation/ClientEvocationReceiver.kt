package miyucomics.hexical.features.evocation

import miyucomics.hexical.network.PlayerUuidPayload
import net.minecraft.client.Minecraft

object ClientEvocationReceiver {
	fun handle(payload: PlayerUuidPayload, active: Boolean) {
		val player = Minecraft.getInstance().level?.getPlayerByUUID(payload.playerId) ?: return
		player.evocationActive = active
	}
}
