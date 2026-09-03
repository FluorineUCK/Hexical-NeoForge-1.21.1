package miyucomics.hexical.features.media_log

import miyucomics.hexical.ClientStorage
import miyucomics.hexical.network.MediaLogPayload
import net.minecraft.client.Minecraft

object ClientMediaLogReceiver {
	fun handle(payload: MediaLogPayload) {
		val provider = Minecraft.getInstance().level?.registryAccess() ?: return
		ClientStorage.mediaLog = MediaLogField().also { it.fromNbt(payload.data, provider) }
	}
}
