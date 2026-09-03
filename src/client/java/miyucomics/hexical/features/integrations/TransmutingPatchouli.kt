package miyucomics.hexical.features.integrations

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import vazkii.patchouli.api.IComponentProcessor
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.IVariableProvider

@Suppress("unused")
class TransmutingPatchouli : IComponentProcessor {
	lateinit var recipe: TransmutingRecipe

	override fun setup(world: Level, vars: IVariableProvider) {
		val id = ResourceLocation.parse(vars.get("recipe", world.registryAccess()).asString())
		val recman = Minecraft.getInstance().level!!.recipeManager
		val transmutingRecipes = recman.getAllRecipesFor(TransmutingRecipe.Type.INSTANCE)
		for (holder in transmutingRecipes) {
			if (holder.id() == id) {
				this.recipe = holder.value()
				break
			}
		}
	}

	override fun process(world: Level, key: String): IVariable {
		if (key.length > 6 && key.take(6) == "output") {
			val index = Integer.parseInt(key.substring(6))
			if (index < recipe.output.size)
				return IVariable.from(recipe.output[index], world.registryAccess())
			return IVariable.from(ItemStack.EMPTY, world.registryAccess())
		}

		return when (key) {
			"input" -> IVariable.from(recipe.input, world.registryAccess())
			"cost" -> IVariable.from(costText(recipe.cost).setStyle(Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)), world.registryAccess())
			else -> IVariable.empty()
		}
	}
}

fun costText(media: Long): MutableComponent {
	val loss = media.toFloat() / MediaConstants.DUST_UNIT
	if (loss > 0f)
		return Component.translatable("hexical.recipe.transmute.media_cost", loss)
	if (loss < 0f)
		return Component.translatable("hexical.recipe.transmute.media_yield", -loss)
	return Component.translatable("hexical.recipe.transmute.media_free")
}
