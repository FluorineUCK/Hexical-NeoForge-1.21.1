package miyucomics.hexical.features.block_mimicry

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.crafting.AbstractCookingRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.core.RegistryAccess
import net.minecraft.world.item.crafting.SingleRecipeInput

class OpCook<T : AbstractCookingRecipe>(private val recipeType: RecipeType<T>, private val mishapMessage: String) : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(env.world, 0, argc)
		env.assertEntityInRange(item)

		val recipe = env.world.recipeManager
			.getRecipesFor(recipeType, SingleRecipeInput(item.item), env.world)
			.firstOrNull()
			?.value()
			?: throw MishapBadItem.of(item, mishapMessage)

		return SpellAction.Result(Spell(recipe, item), MediaConstants.DUST_UNIT * recipe.cookingTime / 200 * item.item.count, listOf())
	}

	private data class Spell(val recipe: AbstractCookingRecipe, val item: ItemEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val input = item.item.copyAndClear()
			val result = recipe.assemble(SingleRecipeInput(input), env.world.registryAccess())
			result.count *= input.count
			env.world.addFreshEntity(ItemEntity(env.world, item.x, item.y, item.z, result))
		}
	}
}
