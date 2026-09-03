package miyucomics.hexical.features.scarabs

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.TreeList
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel

data class ScarabFrame(val signature: String) : ContinuationFrame {
	override val type: ContinuationFrame.Type<*> = TYPE

	override fun evaluate(continuation: SpellContinuation, level: ServerLevel, harness: CastingVM): CastResult {
		return CastResult(
			NullIota(),
			continuation,
			null,
			listOf(),
			ResolvedPatternType.EVALUATED,
			HexEvalSounds.NOTHING.get(),
		)
	}

	override fun breakDownwards(stack: TreeList<Iota>) = true to stack
	override fun size() = 0

	companion object {
		val TYPE: ContinuationFrame.Type<ScarabFrame> = object : ContinuationFrame.Type<ScarabFrame> {
			override fun codec(): MapCodec<ScarabFrame> = Codec.STRING.fieldOf("signature").xmap(::ScarabFrame, ScarabFrame::signature)
			override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ScarabFrame> = StreamCodec.of(
				{ buf, frame -> buf.writeUtf(frame.signature) },
				{ buf -> ScarabFrame(buf.readUtf()) }
			)
		}
	}
}
