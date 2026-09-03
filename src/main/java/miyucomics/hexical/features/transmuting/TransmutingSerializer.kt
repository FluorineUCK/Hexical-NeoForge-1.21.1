package miyucomics.hexical.features.transmuting

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer

class TransmutingSerializer : RecipeSerializer<TransmutingRecipe> {
	override fun codec(): MapCodec<TransmutingRecipe> = CODEC
	override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, TransmutingRecipe> = STREAM_CODEC

	companion object {
		private const val MAX_OUTPUTS = 64
		@JvmField val INSTANCE: TransmutingSerializer = TransmutingSerializer()

		private val OUTPUT_CODEC: Codec<List<ItemStack>> = Codec.either(ItemStack.CODEC, ItemStack.CODEC.listOf()).xmap(
			{ either -> either.map({ listOf(it) }, { it }) },
			{ outputs -> if (outputs.size == 1) Either.left(outputs[0]) else Either.right(outputs) }
		)

		@JvmField
		val CODEC: MapCodec<TransmutingRecipe> = RecordCodecBuilder.mapCodec { instance ->
			instance.group(
				Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(TransmutingRecipe::input),
				Codec.LONG.fieldOf("cost").forGetter(TransmutingRecipe::cost),
				OUTPUT_CODEC.fieldOf("output").forGetter(TransmutingRecipe::output)
			).apply(instance, ::TransmutingRecipe)
		}

		@JvmField
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, TransmutingRecipe> = StreamCodec.of(
			{ buf, recipe ->
				require(recipe.output.size in 1..MAX_OUTPUTS) { "A transmuting recipe must contain 1..$MAX_OUTPUTS outputs" }
				Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input)
				buf.writeLong(recipe.cost)
				buf.writeVarInt(recipe.output.size)
				recipe.output.forEach { ItemStack.STREAM_CODEC.encode(buf, it) }
			},
			{ buf ->
				val input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf)
				val cost = buf.readLong()
				val size = buf.readVarInt()
				require(size in 1..MAX_OUTPUTS) { "Invalid transmuting output count: $size" }
				TransmutingRecipe(input, cost, List(size) { ItemStack.STREAM_CODEC.decode(buf) })
			}
		)
	}
}
