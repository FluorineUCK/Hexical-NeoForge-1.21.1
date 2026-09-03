package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.modifiers.BouncyModifier
import miyucomics.hexical.features.mage_blocks.modifiers.LifespanModifier
import miyucomics.hexical.features.mage_blocks.modifiers.RedstoneModifier
import miyucomics.hexical.features.mage_blocks.modifiers.VolatileModifier
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.registries.RegistryBuilder

object MageBlockModifierRegistry {
	@JvmField
	val MODIFIER_REGISTRY_KEY: ResourceKey<Registry<MageBlockModifierType<*>>> =
		ResourceKey.createRegistryKey(HexicalMain.id("mage_block_modifier"))

	lateinit var MODIFIER_REGISTRY: Registry<MageBlockModifierType<*>>
		private set

	fun createRegistry(event: NewRegistryEvent) {
		MODIFIER_REGISTRY = event.create(RegistryBuilder(MODIFIER_REGISTRY_KEY).sync(true))
	}

	fun register(event: RegisterEvent) {
		if (event.registryKey != MODIFIER_REGISTRY_KEY) return
		for (type in listOf(BouncyModifier.TYPE, LifespanModifier.TYPE, RedstoneModifier.TYPE, VolatileModifier.TYPE))
			event.register(MODIFIER_REGISTRY_KEY, type.id) { type }
	}
}
