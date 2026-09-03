package miyucomics.hexical.features.lesser_sentinels

import miyucomics.hexical.ClientStorage
import miyucomics.hexical.network.LesserSentinelsPayload

object ClientLesserSentinelReceiver {
	fun handle(payload: LesserSentinelsPayload) {
		ClientStorage.lesserSentinels.clear()
		ClientStorage.lesserSentinels.addAll(payload.positions)
	}
}
