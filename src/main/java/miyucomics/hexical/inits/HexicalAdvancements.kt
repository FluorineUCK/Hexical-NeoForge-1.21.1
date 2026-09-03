package miyucomics.hexical.inits

import com.mojang.serialization.Codec
import miyucomics.hexical.HexicalMain
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.registries.RegisterEvent
import java.util.Optional

object HexicalAdvancements {
	val AR = HexicalCriterion()
	val CONJURE_CAKE = HexicalCriterion()
	val HEXXY = HexicalCriterion()
	val DIY = HexicalCriterion()
	val HALLUCINATE = HexicalCriterion()
	val EDUCATE_GENIE = HexicalCriterion()
	val RELOAD_LAMP = HexicalCriterion()

	val EVOCATION_STATISTIC: ResourceLocation = HexicalMain.id("evocation")

	fun register(event: RegisterEvent) {
		when (event.registryKey) {
			Registries.TRIGGER_TYPE -> {
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("specklike")) { AR }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("conjure_cake")) { CONJURE_CAKE }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("summon_hexxy")) { HEXXY }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("diy_conjuring")) { DIY }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("hallucinate")) { HALLUCINATE }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("educate_genie")) { EDUCATE_GENIE }
				event.register(Registries.TRIGGER_TYPE, HexicalMain.id("reload_lamp")) { RELOAD_LAMP }
			}
			Registries.CUSTOM_STAT ->
				event.register(Registries.CUSTOM_STAT, EVOCATION_STATISTIC) { EVOCATION_STATISTIC }
		}
	}
}

class HexicalCriterion : SimpleCriterionTrigger<HexicalCriterion.Condition>() {
	override fun codec(): Codec<Condition> = Condition.CODEC

	fun trigger(player: ServerPlayer) = trigger(player) { true }

	class Condition(private val playerPredicate: Optional<ContextAwarePredicate>) : SimpleInstance {
		override fun player(): Optional<ContextAwarePredicate> = playerPredicate

		companion object {
			@JvmField
			val CODEC: Codec<Condition> = EntityPredicate.ADVANCEMENT_CODEC
				.optionalFieldOf("player")
				.xmap(::Condition, Condition::player)
				.codec()
		}
	}
}
