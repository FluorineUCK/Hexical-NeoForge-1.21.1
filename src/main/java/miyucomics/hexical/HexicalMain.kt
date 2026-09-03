package miyucomics.hexical

import at.petrak.hexcasting.common.lib.HexRegistries
import miyucomics.hexical.inits.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.event.AddReloadListenerEvent
import miyucomics.hexical.features.dyes.DyeDataHook
import miyucomics.hexical.features.prestidigitation.PrestidigitationBlockBooleans
import miyucomics.hexical.features.prestidigitation.PrestidigitationBlockTransformations
import miyucomics.hexical.network.HexicalNetworking

@Mod(HexicalMain.MOD_ID)
class HexicalMain(modBus: IEventBus) {
	init {
		modBus.addListener(::createRegistries)
		modBus.addListener(::registerContent)
		modBus.addListener(::registerCapabilities)
		modBus.addListener(HexicalNetworking::register)
		NeoForge.EVENT_BUS.addListener(::registerBrewingRecipes)
		NeoForge.EVENT_BUS.addListener(::addReloadListeners)
		HexicalHooksServer.init()
	}

	private fun createRegistries(event: NewRegistryEvent) {
		HexicalHooksServer.createRegistries(event)
	}

	private fun registerContent(event: RegisterEvent) {
		HexicalBlocks.register(event)
		HexicalEntities.register(event)
		HexicalItems.register(event)
		HexicalParticles.register(event)
		HexicalSounds.register(event)
		HexicalAdvancements.register(event)
		HexicalHooksServer.registerContent(event)

		when (event.registryKey) {
			HexRegistries.IOTA_TYPE -> HexicalIota.register(event)
			HexRegistries.ACTION -> HexicalActions.registerAll { id, entry ->
				event.register(HexRegistries.ACTION, id) { entry }
			}
		}
	}

	private fun registerCapabilities(event: RegisterCapabilitiesEvent) {
		HexicalCardinalComponents.register(event)
	}

	private fun registerBrewingRecipes(event: RegisterBrewingRecipesEvent) {
		miyucomics.hexical.features.periwinkle.WooleyedEffectRegister.registerBrewingRecipes(event)
	}

	private fun addReloadListeners(event: AddReloadListenerEvent) {
		DyeDataHook.addReloadListener(event)
		PrestidigitationBlockBooleans.addReloadListener(event)
		PrestidigitationBlockTransformations.addReloadListener(event)
	}

	companion object {
		const val MOD_ID: String = "hexical"
		@JvmField val RANDOM: RandomSource = RandomSource.create()
		@JvmStatic fun id(string: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, string)
	}
}
