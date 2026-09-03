package miyucomics.hexical.features.mage_blocks

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.ChunkRenderTypeSet
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty

/**
 * Dynamic wrapper for a Mage Block. NeoForge supplies block-entity state through
 * ModelData, then each render pass delegates to the disguised block's baked model.
 */
class MageBlockModel(private val fallback: BakedModel) : BakedModel {
	override fun getModelData(level: BlockAndTintGetter, pos: BlockPos, state: BlockState, modelData: ModelData): ModelData {
		val disguise = (level.getBlockEntity(pos) as? MageBlockEntity)?.disguise ?: Blocks.AMETHYST_BLOCK.defaultBlockState()
		return modelData.derive().with(DISGUISE, disguise).build()
	}

	override fun getQuads(state: BlockState?, side: Direction?, random: RandomSource): List<BakedQuad> =
		fallback.getQuads(state, side, random)

	override fun getQuads(state: BlockState?, side: Direction?, random: RandomSource, data: ModelData, renderType: RenderType?): List<BakedQuad> {
		val disguise = data.get(DISGUISE) ?: Blocks.AMETHYST_BLOCK.defaultBlockState()
		val model = targetModel(disguise)
		return model.getQuads(disguise, side, random, data, renderType)
	}

	override fun getRenderTypes(state: BlockState, random: RandomSource, data: ModelData): ChunkRenderTypeSet {
		val disguise = data.get(DISGUISE) ?: Blocks.AMETHYST_BLOCK.defaultBlockState()
		return targetModel(disguise).getRenderTypes(disguise, random, data)
	}

	private fun targetModel(disguise: BlockState): BakedModel {
		val model = Minecraft.getInstance().blockRenderer.getBlockModel(disguise)
		return if (model === this) fallback else model
	}

	override fun useAmbientOcclusion() = fallback.useAmbientOcclusion()
	override fun isGui3d() = fallback.isGui3d
	override fun usesBlockLight() = fallback.usesBlockLight()
	override fun isCustomRenderer() = false
	override fun getParticleIcon(): TextureAtlasSprite = fallback.particleIcon
	override fun getTransforms(): ItemTransforms = fallback.transforms
	override fun getOverrides(): ItemOverrides = fallback.overrides

	companion object {
		@JvmField val DISGUISE = ModelProperty<BlockState>()
	}
}
