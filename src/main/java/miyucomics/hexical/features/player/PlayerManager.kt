package miyucomics.hexical.features.player

import miyucomics.hexical.features.evocation.EvocationField
import miyucomics.hexical.features.evocation.EvocationTicker
import miyucomics.hexical.features.lamps.ArchLampField
import miyucomics.hexical.features.lesser_sentinels.LesserSentinelField
import miyucomics.hexical.features.media_log.MediaLogField
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexical.features.player.types.PlayerTicker
import miyucomics.hexical.features.telepathy.KeybindField
import miyucomics.hexical.features.telepathy.KeybindTicker
import miyucomics.hexical.features.wristpocket.WristpocketField
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.HolderLookup
import kotlin.reflect.KClass

class PlayerManager {
	@Suppress("UNCHECKED_CAST")
	fun <T : PlayerField> get(clazz: KClass<T>): T { return fields[clazz] as? T ?: error("No field registered for $clazz") }
	private val fields = mutableMapOf<KClass<out PlayerField>, PlayerField>()
	private fun registerField(field: PlayerField) { fields[field::class] = field }

	private val tickers = mutableListOf<PlayerTicker>()
	private fun registerTicker(ticker: PlayerTicker) { tickers.add(ticker) }

	init {
		registerField(ArchLampField())
		registerField(EvocationField())
		registerField(KeybindField())
		registerField(MediaLogField())
		registerField(LesserSentinelField())
		registerField(WristpocketField())
		registerTicker(EvocationTicker())
		registerTicker(KeybindTicker())
	}

	fun tick(player: Player) {
		for (ticker in tickers)
			ticker.tick(player)
	}

	fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		for (field in fields.values)
			field.readNbt(compound, provider)
	}

	fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		for (field in fields.values)
			field.writeNbt(compound, provider)
	}

	fun handleRespawn(new: Player, old: Player) {
		for (field in fields)
			field.value.handleRespawn(new, old)
	}
}

fun Player.getHexicalPlayerManager() = (this as PlayerEntityMinterface).getPlayerManager()
