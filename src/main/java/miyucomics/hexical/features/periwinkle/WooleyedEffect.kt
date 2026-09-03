package miyucomics.hexical.features.periwinkle

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.core.Holder
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.Potions
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent
import net.neoforged.neoforge.registries.RegisterEvent

object WooleyedEffectRegister {
	private lateinit var WOOLEYED_POTION: Potion
	private lateinit var LONG_WOOLEYED_POTION: Potion
	private lateinit var STRONG_WOOLEYED_POTION: Potion

	@JvmStatic
	fun effectHolder(): Holder<MobEffect> = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(WooleyedEffect)

	fun register(event: RegisterEvent) {
		when (event.registryKey) {
			Registries.MOB_EFFECT ->
				event.register(Registries.MOB_EFFECT, HexicalMain.id("wooleyed")) { WooleyedEffect }
			Registries.POTION -> {
				val effect = effectHolder()
				WOOLEYED_POTION = Potion(MobEffectInstance(effect, 12000, 0))
				LONG_WOOLEYED_POTION = Potion(MobEffectInstance(effect, 48000, 0))
				STRONG_WOOLEYED_POTION = Potion(MobEffectInstance(effect, 6000, 1))
				event.register(Registries.POTION, HexicalMain.id("wooleyed")) { WOOLEYED_POTION }
				event.register(Registries.POTION, HexicalMain.id("long_wooleyed")) { LONG_WOOLEYED_POTION }
				event.register(Registries.POTION, HexicalMain.id("strong_wooleyed")) { STRONG_WOOLEYED_POTION }
			}
		}
	}

	fun registerBrewingRecipes(event: RegisterBrewingRecipesEvent) {
		val normal = BuiltInRegistries.POTION.wrapAsHolder(WOOLEYED_POTION)
		val long = BuiltInRegistries.POTION.wrapAsHolder(LONG_WOOLEYED_POTION)
		val strong = BuiltInRegistries.POTION.wrapAsHolder(STRONG_WOOLEYED_POTION)
		event.builder.addMix(Potions.AWKWARD, HexicalBlocks.PERIWINKLE_FLOWER_ITEM, normal)
		event.builder.addMix(normal, Items.REDSTONE, long)
		event.builder.addMix(normal, Items.GLOWSTONE_DUST, strong)
	}
}

object WooleyedEffect : MobEffect(MobEffectCategory.BENEFICIAL, 0xff_a678f1.toInt())
