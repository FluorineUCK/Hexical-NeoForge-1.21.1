package miyucomics.hexical.features.transmuting

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level

data class TransmutingRecipe(val input: Ingredient, val cost: Long, val output: List<ItemStack>) : Recipe<SingleRecipeInput> {
	override fun getType() = Type.INSTANCE
	override fun canCraftInDimensions(width: Int, height: Int) = false
	override fun getSerializer() = TransmutingSerializer.INSTANCE
	override fun getResultItem(provider: HolderLookup.Provider): ItemStack = output.firstOrNull()?.copy() ?: ItemStack.EMPTY
	override fun matches(recipeInput: SingleRecipeInput, world: Level) = input.test(recipeInput.item())
	override fun assemble(recipeInput: SingleRecipeInput, provider: HolderLookup.Provider): ItemStack = ItemStack.EMPTY
	override fun isSpecial() = true

	class Type : RecipeType<TransmutingRecipe> {
		override fun toString() = "hexical:transmuting"

		companion object {
			val INSTANCE = Type()
		}
	}
}
