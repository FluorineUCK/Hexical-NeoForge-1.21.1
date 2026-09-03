package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import miyucomics.hexical.hexcompat.ItemStackDataCompat

class HandLampCastEnv(caster: ServerPlayer, castingHand: InteractionHand, finale: Boolean, private val stack: ItemStack) : LampCastEnv(caster, castingHand, finale) {
	override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}

	override fun getCastingHand(): InteractionHand = this.castingHand
	override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

	fun setInternalIota(iota: Iota) {
		ItemStackDataCompat.update(stack) { it.put("storage", miyucomics.hexical.hexcompat.serializeIota(iota)) }
	}
}
