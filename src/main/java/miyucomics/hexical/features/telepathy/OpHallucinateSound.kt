package miyucomics.hexical.features.telepathy

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.inits.HexicalAdvancements
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.core.Holder
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvent

class OpHallucinateSound(private val sound: Holder<SoundEvent>) : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val caster = env.castingEntity
		if (caster !is ServerPlayer)
			return emptyList()
		caster.connection.send(ClientboundSoundPacket(sound, SoundSource.MASTER, caster.x, caster.y, caster.z, 1f, 1f, 0))
		HexicalAdvancements.HALLUCINATE.trigger(caster)
		return emptyList()
	}
}