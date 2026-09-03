package miyucomics.hexical.features.confetti

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.network.ConfettiPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

object ConfettiHelper {
	fun spawn(world: ServerLevel, pos: Vec3, dir: Vec3, speed: Double) {
		val payload = ConfettiPayload(HexicalMain.RANDOM.nextLong(), pos, dir, speed)
		world.players().forEach { player -> PacketDistributor.sendToPlayer(player, payload) }
	}
}
