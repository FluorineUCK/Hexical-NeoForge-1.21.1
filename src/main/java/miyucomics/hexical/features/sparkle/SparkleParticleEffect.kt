package miyucomics.hexical.features.sparkle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import miyucomics.hexical.inits.HexicalParticles
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import org.joml.Vector3f

class SparkleParticleEffect(val color: Vector3f, val lifespan: Int) : ParticleOptions {
	override fun getType() = HexicalParticles.SPARKLE_PARTICLE

	companion object {
		@JvmField
		val CODEC: MapCodec<SparkleParticleEffect> = RecordCodecBuilder.mapCodec { instance ->
			instance.group(
				ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(SparkleParticleEffect::color),
				Codec.INT.fieldOf("lifespan").forGetter(SparkleParticleEffect::lifespan)
			).apply(instance, ::SparkleParticleEffect)
		}

		@JvmField
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SparkleParticleEffect> = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F,
			SparkleParticleEffect::color,
			ByteBufCodecs.VAR_INT,
			SparkleParticleEffect::lifespan,
			::SparkleParticleEffect
		)
	}
}
