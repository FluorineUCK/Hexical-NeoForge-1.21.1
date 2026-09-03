package miyucomics.hexical.features.integrations

import dev.emi.emi.api.recipe.BasicEmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import miyucomics.hexical.HexicalMain
import net.minecraft.world.level.block.Block
import net.minecraft.tags.TagKey
import net.minecraft.resources.ResourceLocation

class DyeingEmi(input: TagKey<*>, output: Block, val dye: String, recipeId: ResourceLocation) : BasicEmiRecipe(HexicalEmi.DYEING_CATEGORY, recipeId, 134, 52) {
	init {
		this.inputs.add(EmiIngredient.of(input))
		this.outputs.add(EmiStack.of(output))
	}

	override fun addWidgets(widgets: WidgetHolder) {
		widgets.addTexture(OVERLAY, 0, 0, 134, 40, 0, 40, 134, 40, 256, 128)
		widgets.addSlot(inputs[0], 12, 12)
		widgets.addSlot(outputs[0], 42, 12)
	}

	companion object {
		private val OVERLAY: ResourceLocation = HexicalMain.id("textures/gui/dyeing_emi.png")
	}
}