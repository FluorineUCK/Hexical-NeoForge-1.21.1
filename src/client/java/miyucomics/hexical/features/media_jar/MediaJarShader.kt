package miyucomics.hexical.features.media_jar

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.ShaderInstance
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.event.RegisterShadersEvent

object MediaJarShader : InitHook() {
	lateinit var mediaJarRenderLayer: RenderType
	val PERLIN_NOISE: ResourceLocation = HexicalMain.id("textures/misc/perlin.png")

	override fun init() {
		// Registered from the physical-client mod event bus.
	}

	fun registerShader(event: RegisterShadersEvent) {
		event.registerShader(ShaderInstance(event.resourceProvider, HexicalMain.id("media_jar"), DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL)) { shader ->
				mediaJarRenderLayer = RenderType.create(
					"media_jar_shader",
					DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
					VertexFormat.Mode.QUADS,
					512,
					RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.ShaderStateShard { shader })
						.setTextureState(RenderStateShard.TextureStateShard(PERLIN_NOISE, false, false))
						.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
						.setCullState(RenderStateShard.CULL)
						.setLightmapState(RenderStateShard.NO_LIGHTMAP)
						.setOverlayState(RenderStateShard.NO_OVERLAY)
						.createCompositeState(true)
				)
		}
	}
}
