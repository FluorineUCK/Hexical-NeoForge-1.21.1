package miyucomics.hexical.features.curios

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.neoforge.client.event.ModelEvent

object FluteCurioItemModel : InitHook() {
	@JvmField val heldFluteModel: ModelResourceLocation = ModelResourceLocation.standalone(HexicalMain.id("item/held_curio_flute"))
	@JvmField val fluteModel: ModelResourceLocation = ModelResourceLocation.inventory(HexicalMain.id("curio_flute"))

	override fun init() = Unit

	fun registerModels(event: ModelEvent.RegisterAdditional) = event.register(heldFluteModel)
}
