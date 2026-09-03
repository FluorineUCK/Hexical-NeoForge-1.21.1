package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.HexicalMain
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.PackType
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import java.io.InputStreamReader
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.neoforged.neoforge.event.AddReloadListenerEvent

object PrestidigitationBlockTransformations {
	private val map: MutableMap<Block, BlockState> = mutableMapOf()

	fun init() {
		PrestidigitationHandlersHook.register("transform_block", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = map.containsKey(getBlock(env, pos))
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				setBlockState(env, pos, map[getBlock(env, pos)]!!)
			}
		})
	}

	fun addReloadListener(event: AddReloadListenerEvent) {
		event.addListener(ResourceManagerReloadListener(::reload))
	}

	private fun reload(manager: ResourceManager) {
		map.clear()
		manager.listResources("prestidigitation") { it.path.endsWith("block_transformations.json") }.keys.forEach { path ->
			manager.getResource(path).ifPresent { resource ->
				resource.open().use { stream ->
					(JsonParser.parseReader(InputStreamReader(stream, Charsets.UTF_8)) as JsonObject).entrySet().forEach {
						val from = ResourceLocation.parse(it.key)
						val to = ResourceLocation.parse(it.value.asString)
						if (BuiltInRegistries.BLOCK.containsKey(from) && BuiltInRegistries.BLOCK.containsKey(to))
							map[BuiltInRegistries.BLOCK.get(from)] = BuiltInRegistries.BLOCK.get(to).defaultBlockState()
					}
				}
			}
		}
	}
}
