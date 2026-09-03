package miyucomics.hexical.features.mage_blocks

import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation

abstract class MageBlockModifierType<T : MageBlockModifier> {
	abstract val argc: Int
	abstract val id: ResourceLocation
	abstract fun construct(args: List<Iota>): T
	abstract fun deserialize(element: Tag): T
}