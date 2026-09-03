package miyucomics.hexical.features.integrations

import at.petrak.hexcasting.api.utils.putCompound
import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiStack
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.nbt.CompoundTag

@EmiEntrypoint
class HexicalEmi : EmiPlugin {
	override fun register(registry: EmiRegistry) {
		registry.addCategory(DYEING_CATEGORY)
		registry.addCategory(TRANSMUTING_CATEGORY)

		registry.addWorkstation(TRANSMUTING_CATEGORY, TRANSMUTING_ICON)

		for (holder in registry.recipeManager.getAllRecipesFor(TransmutingHelper.TRANSMUTING_RECIPE))
			registry.addRecipe(TransmutingEmi(holder.id(), holder.value()))
	}

	companion object {
		val DYEING_CATEGORY = EmiRecipeCategory(HexicalMain.id("dyeing"), EmiStack.of(ItemStack(Items.RED_DYE)), EmiTexture(HexicalMain.id("textures/gui/dyeing_simplified.png"), 0, 0, 16, 16, 16, 16, 16, 16))

		val TRANSMUTING_ICON: EmiStack = EmiStack.of(ItemStack(HexicalBlocks.MEDIA_JAR_ITEM).also {
			val compound = CompoundTag()
			compound.putLong("media", MediaJarBlock.MAX_CAPACITY)
			ItemStackDataCompat.setBlockEntityData(it, compound)
		})
		val TRANSMUTING_CATEGORY = EmiRecipeCategory(HexicalMain.id("transmuting"), TRANSMUTING_ICON, EmiTexture(HexicalMain.id("textures/gui/transmuting_simplified.png"), 0, 0, 16, 16, 16, 16, 16, 16))
	}
}
