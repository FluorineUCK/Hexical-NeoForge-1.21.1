package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import net.minecraft.world.damagesource.DamageType
import net.minecraft.resources.ResourceKey
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries

object HexicalDamageTypes {
	val MAGIC_MISSILE: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, HexicalMain.id("magic_missile"))
	val SPIKE: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, HexicalMain.id("spike"))
}
