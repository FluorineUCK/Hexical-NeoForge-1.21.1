package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.HexicalMain
import net.minecraft.world.level.block.Block
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.PackType
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import java.io.InputStreamReader
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.neoforged.neoforge.event.AddReloadListenerEvent

object PrestidigitationBlockBooleans {
	private val map: MutableMap<Block, BooleanProperty> = mutableMapOf()

	fun init() {
		PrestidigitationHandlersHook.register("boolean_block", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = map.containsKey(getBlock(env, pos))
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				val property = map[state.block] ?: return
				setBlockState(env, pos, state.setValue(property, !state.getValue(property)))
			}
		})
	}

	fun addReloadListener(event: AddReloadListenerEvent) {
		event.addListener(ResourceManagerReloadListener(::reload))
	}

	private fun reload(manager: ResourceManager) {
		map.clear()
		manager.listResources("prestidigitation") { it.path.endsWith("block_booleans.json") }.keys.forEach { path ->
			manager.getResource(path).ifPresent { resource ->
				resource.open().use { stream ->
					(JsonParser.parseReader(InputStreamReader(stream, Charsets.UTF_8)) as JsonObject).entrySet().forEach {
						val id = ResourceLocation.parse(it.key)
						if (BuiltInRegistries.BLOCK.containsKey(id))
							map[BuiltInRegistries.BLOCK.get(id)] = BooleanProperty.create(it.value.asString)
					}
				}
			}
		}
	}
}
