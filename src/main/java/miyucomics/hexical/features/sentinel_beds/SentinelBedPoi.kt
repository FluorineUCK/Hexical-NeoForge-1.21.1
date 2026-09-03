package miyucomics.hexical.features.sentinel_beds

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.neoforged.neoforge.registries.RegisterEvent

object SentinelBedPoi {
	private val SENTINEL_BED_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, HexicalMain.id("sentinel_bed"))
	private val SENTINEL_BED_POI = PoiType(HexicalBlocks.SENTINEL_BED_BLOCK.stateDefinition.possibleStates.toSet(), 0, 1)

	fun register(event: RegisterEvent) {
		if (event.registryKey == Registries.POINT_OF_INTEREST_TYPE)
			event.register(Registries.POINT_OF_INTEREST_TYPE, SENTINEL_BED_POI_KEY.location()) { SENTINEL_BED_POI }
	}

	fun isSentinelBed(world: ServerLevel, centerPos: BlockPos): Boolean =
		world.poiManager.getType(centerPos).filter { it.`is`(SENTINEL_BED_POI_KEY) }.isPresent
}
