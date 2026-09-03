package miyucomics.hexical.features.dyes

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.Item
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.PackType
import net.minecraft.resources.ResourceLocation
import java.io.InputStream
import java.io.InputStreamReader
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.neoforged.neoforge.event.AddReloadListenerEvent

object DyeDataHook : InitHook() {
	override fun init() {
		// Registered through NeoForge's AddReloadListenerEvent.
	}

	fun addReloadListener(event: AddReloadListenerEvent) {
		event.addListener(ResourceManagerReloadListener(::reload))
	}

	private fun reload(manager: ResourceManager) {
		blockGroups.clear()
		itemGroups.clear()
		blockDyeLookup.clear()
		itemDyeLookup.clear()
		manager.listResources("dyes") { path -> path.path.endsWith(".json") }.keys.forEach { id ->
			manager.getResource(id).ifPresent { resource -> resource.open().use(::loadData) }
		}
	}

	fun isDyeable(block: Block): Boolean = blockDyeLookup.containsKey(block)
	fun isDyeable(item: Item): Boolean = itemDyeLookup.containsKey(item)
	fun getDye(block: Block): DyeOption? = blockDyeLookup[block]
	fun getDye(item: Item): DyeOption? = itemDyeLookup[item]

	fun getNewBlock(block: Block, dye: DyeOption): BlockState? {
		blockGroups.forEach { (_, group) ->
			if (group.containsValue(block) && group.containsKey(dye))
				return group[dye]!!.defaultBlockState()
		}
		return null
	}

	fun getNewItem(item: Item, dye: DyeOption): Item? {
		itemGroups.forEach { (_, group) ->
			if (group.containsValue(item) && group.containsKey(dye))
				return group[dye]!!
		}
		return null
	}

	private val blockGroups: HashMap<String, HashMap<DyeOption, Block>> = HashMap()
	private val itemGroups: HashMap<String, HashMap<DyeOption, Item>> = HashMap()
	private val blockDyeLookup = HashMap<Block, DyeOption>()
	private val itemDyeLookup = HashMap<Item, DyeOption>()

	private fun loadData(stream: InputStream) {
		val json = JsonParser.parseReader(InputStreamReader(stream, "UTF-8")) as JsonObject

		val blocks = json.getAsJsonObject("blocks")
		blocks.keySet().forEach { groupName ->
			val pattern = blocks.getAsJsonPrimitive(groupName).asString
			val group = HashMap<DyeOption, Block>()
			DyeOption.values().forEach { dye -> resolvePattern(BuiltInRegistries.BLOCK, pattern, dye)?.let {
				group[dye] = it
				blockDyeLookup[it] = dye
			} }
			if (group.isNotEmpty())
				blockGroups[groupName] = group
		}

		val items = json.getAsJsonObject("items")
		items.keySet().forEach { groupName ->
			val pattern = items.getAsJsonPrimitive(groupName).asString
			val group = HashMap<DyeOption, Item>()
			DyeOption.values().forEach { dye -> resolvePattern(BuiltInRegistries.ITEM, pattern, dye)?.let {
				group[dye] = it
				itemDyeLookup[it] = dye
			} }
			if (group.isNotEmpty())
				itemGroups[groupName] = group
		}
	}

	private fun <T : Any> resolvePattern(registry: Registry<T>, pattern: String, dye: DyeOption) = listOfNotNull(
		registry.get(ResourceKey.create<T>(registry.key(), ResourceLocation.parse(pattern.replace("{color}", dye.replacement)))),
		registry.get(ResourceKey.create<T>(registry.key(), ResourceLocation.parse(pattern.replace("{color}", dye.replacement + "_"))))
	).firstOrNull()
}
