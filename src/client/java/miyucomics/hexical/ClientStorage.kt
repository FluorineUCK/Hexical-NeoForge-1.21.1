package miyucomics.hexical

import miyucomics.hexical.features.media_log.MediaLogField
import net.minecraft.world.phys.Vec3

object ClientStorage {
	@JvmField
	var ticks: Int = 0
	var lesserSentinels: MutableList<Vec3> = mutableListOf()
	var mediaLog: MediaLogField = MediaLogField()
}