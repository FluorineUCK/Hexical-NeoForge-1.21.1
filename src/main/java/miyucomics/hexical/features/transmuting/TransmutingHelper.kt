package miyucomics.hexical.features.transmuting

import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarItem
import miyucomics.hexical.features.transmuting.TransmutingRecipe.Type
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.RegisterEvent
import kotlin.math.min

object TransmutingHelper {
	@JvmField
	val TRANSMUTING_RECIPE: RecipeType<TransmutingRecipe> = Type.INSTANCE

	fun register(event: RegisterEvent) {
		when (event.registryKey) {
			Registries.RECIPE_SERIALIZER ->
				event.register(Registries.RECIPE_SERIALIZER, HexicalMain.id("transmuting")) { TransmutingSerializer.INSTANCE }
			Registries.RECIPE_TYPE ->
				event.register(Registries.RECIPE_TYPE, HexicalMain.id("transmuting")) { TRANSMUTING_RECIPE }
		}
	}

	fun transmuteItem(world: Level, stack: ItemStack, media: Long, insertMedia: (Long) -> Long, withdrawMedia: (Long) -> Boolean): TransmutationResult {
		if (stack.`is`(HexItems.BATTERY.get())) {
			val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
			val given = min(mediaHolder.maxMedia - mediaHolder.media, media)
			mediaHolder.insertMedia(given, false)
			withdrawMedia(given)
			return TransmutationResult.RefilledHolder
		}

		if (stack.`is`(HexicalBlocks.MEDIA_JAR_ITEM)) {
			val jarData = ItemStackDataCompat.blockEntityData(stack) ?: return TransmutationResult.Pass
			val given = min(MediaJarBlock.MAX_CAPACITY - MediaJarItem.getMedia(jarData), media)
			MediaJarItem.insertMedia(jarData, given)
			ItemStackDataCompat.setBlockEntityData(stack, jarData)
			withdrawMedia(given)
			return TransmutationResult.RefilledHolder
		}

		if (isMediaItem(stack) && media < MediaJarBlock.MAX_CAPACITY) {
			val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
			val consumed = insertMedia(mediaHolder.media)
			mediaHolder.withdrawMedia(consumed, false)
			return TransmutationResult.AbsorbedMedia
		}

		val recipe = getRecipe(stack, world)
		if (recipe != null && media >= recipe.cost) {
			stack.shrink(1)
			withdrawMedia(recipe.cost)
			return TransmutationResult.TransmutedItems(recipe.output.map { it.copy() })
		}

		return TransmutationResult.Pass
	}

	private fun getRecipe(input: ItemStack, world: Level): TransmutingRecipe? {
		val recipeInput = SingleRecipeInput(input)
		return world.recipeManager
			.getAllRecipesFor(TRANSMUTING_RECIPE)
			.firstOrNull { it.value().matches(recipeInput, world) }
			?.value()
	}
}

sealed class TransmutationResult {
	object AbsorbedMedia : TransmutationResult()
	object Pass : TransmutationResult()
	object RefilledHolder : TransmutationResult()
	data class TransmutedItems(val output: List<ItemStack>) : TransmutationResult()
}
