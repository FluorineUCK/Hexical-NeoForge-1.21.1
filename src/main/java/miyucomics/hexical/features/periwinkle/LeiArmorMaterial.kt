package miyucomics.hexical.features.periwinkle

import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient

object LeiArmorMaterial {
	/** A direct holder is sufficient because the lei is never serialized as a material value. */
	@JvmField
	val INSTANCE: Holder<ArmorMaterial> = Holder.direct(
		ArmorMaterial(
			ArmorItem.Type.entries.associateWith { 0 },
			100,
			BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_CHIME),
			{ Ingredient.EMPTY },
			// The original 1.20 material name was the unqualified "lei", so its
			// artwork intentionally lives in assets/minecraft/textures/models/armor.
			listOf(ArmorMaterial.Layer(ResourceLocation.withDefaultNamespace("lei"))),
			0f,
			0f
		)
	)
}
