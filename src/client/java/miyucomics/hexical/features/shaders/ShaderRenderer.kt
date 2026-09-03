package miyucomics.hexical.features.shaders

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.PostChain
import net.minecraft.resources.ResourceLocation
import java.io.IOException

object ShaderRenderer {
    private var activeShader: PostChain? = null
    private var lastShader: PostChain? = null
    private var lastWidth = 0
    private var lastHeight = 0

    @JvmStatic
    fun render(deltaTick: Float) {
        if (activeShader == null)
            return

        if (lastShader != activeShader) {
            lastShader = activeShader
            lastWidth = 0
            lastHeight = 0
        }

        updateEffectSize(activeShader!!)
        activeShader!!.process(deltaTick)
        Minecraft.getInstance().mainRenderTarget.bindWrite(false)
    }

    fun setEffect(location: ResourceLocation?) {
        if (location == null) {
            activeShader = null
            return
        }
        try {
            val client = Minecraft.getInstance()
            activeShader = PostChain(client.textureManager, client.resourceManager, client.mainRenderTarget, location)
        } catch (_: IOException) {}
    }

    private fun updateEffectSize(effect: PostChain) {
        val client = Minecraft.getInstance()
        val width = client.window.screenWidth
        val height = client.window.screenHeight
        if (width != lastWidth || height != lastHeight) {
            lastWidth = width
            lastHeight = height
            effect.resize(width, height)
        }
    }
}