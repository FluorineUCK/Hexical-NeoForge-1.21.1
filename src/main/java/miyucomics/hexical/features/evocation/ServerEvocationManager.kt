package miyucomics.hexical.features.evocation

import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.network.PlayerUuidPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.PacketDistributor

object ServerEvocationManager {
	const val EVOKE_DURATION: Int = 20

	fun startEvocation(player: ServerPlayer, server: MinecraftServer) {
		if (!CastingUtils.isEnlightened(player))
			return
		player.evocationActive = true
		player.evocationDuration = EVOKE_DURATION
		player.level().playSound(null, player.x, player.y, player.z, HexicalSounds.EVOKING_MURMUR, SoundSource.PLAYERS, 1f, 1f)
		val payload = PlayerUuidPayload(player.uuid, PlayerUuidPayload.EVOCATION_START_TYPE)
		for (receiver in server.playerList.players)
			PacketDistributor.sendToPlayer(receiver, payload)
	}

	fun endEvocation(player: ServerPlayer, server: MinecraftServer) {
		if (!CastingUtils.isEnlightened(player))
			return
		player.evocationActive = false
		val payload = PlayerUuidPayload(player.uuid, PlayerUuidPayload.EVOCATION_END_TYPE)
		for (receiver in server.playerList.players)
			PacketDistributor.sendToPlayer(receiver, payload)
	}
}
