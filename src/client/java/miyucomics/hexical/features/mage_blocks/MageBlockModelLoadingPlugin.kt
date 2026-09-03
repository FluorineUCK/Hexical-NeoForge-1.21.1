package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent

object MageBlockModelLoadingHook : InitHook() {
	override fun init() = Unit

	fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
		event.register({ _, world, pos, tintIndex ->
			val disguise = if (world != null && pos != null) (world.getBlockEntity(pos) as? MageBlockEntity)?.disguise else null
			if (disguise == null || disguise.block === HexicalBlocks.MAGE_BLOCK) -1
			else Minecraft.getInstance().blockColors.getColor(disguise, world, pos, tintIndex)
		}, HexicalBlocks.MAGE_BLOCK)
	}

	fun modifyBakingResult(event: ModelEvent.ModifyBakingResult) {
		val blockId = ModelResourceLocation(HexicalMain.id("mage_block"), "")
		event.models[blockId]?.let { event.models[blockId] = MageBlockModel(it) }

		val itemId = ModelResourceLocation.inventory(HexicalMain.id("mage_block"))
		val shardId = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("amethyst_shard"))
		event.models[shardId]?.let { event.models[itemId] = it }
	}
}
