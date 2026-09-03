package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.client.render.*
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.ClientStorage.ticks
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.util.FastColor
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec2
import kotlin.math.max
import kotlin.math.min
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderGuiEvent
import net.neoforged.neoforge.common.NeoForge

object MediaLogRenderer : InitHook() {
	const val FADE_IN_DURATION: Int = 40
	var fadingInLog = false
	var fadingInLogStart = 0
	var fadingInLogTweener = 0

	override fun init() {
		NeoForge.EVENT_BUS.addListener(::onClientTick)
		NeoForge.EVENT_BUS.addListener(::onRenderGui)
	}

	private fun onClientTick(event: ClientTickEvent.Post) {
		fadingInLogTweener = if (fadingInLog) min(ticks - fadingInLogStart, FADE_IN_DURATION) else max(fadingInLogTweener - 5, 0)
	}

	private fun onRenderGui(event: RenderGuiEvent.Post) {
		if (fadingInLogTweener == 0) return
		val context = event.guiGraphics
		val tickDelta = event.partialTick.getGameTimeDeltaPartialTick(true)
		val progress = (fadingInLogTweener + tickDelta) / FADE_IN_DURATION.toFloat()

			val backgroundColor = FastColor.ARGB32.color((progress * 100).toInt(), 0, 0, 0)
			context.fillGradient(0, 0, context.guiWidth(), context.guiHeight(), backgroundColor, backgroundColor)

			context.pose().pushPose()
			context.pose().translate(context.guiWidth() / 2f, context.guiHeight() / 2f, 0f)

			for (phase in phases) {
				val localProgress = (progress - phase.start) / phase.duration
				if (localProgress > 0f)
					phase.render(context, Mth.clamp(localProgress, 0f, 1f))
			}

			context.pose().popPose()
	}

	fun drawMishapText(context: GuiGraphics, alpha: Float) {
		val mishapText = ClientStorage.mediaLog.mishap
		context.drawCenteredString(Minecraft.getInstance().font, mishapText, 0, -context.guiHeight() / 2 + 10, FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255))
	}

	fun drawMediaLogPattern(matrices: PoseStack, index: Int, alpha: Float) {
		matrices.pushPose()
		val x = index % 8
		val y = (index / 8)
		matrices.translate(x * 50f, y * 50f, 0f)

		if (ClientStorage.mediaLog.patterns.buffer().size > index) {
			matrices.translate(-12.5f, -12.5f, 0f)
			matrices.scale(25f, 25f, 25f)
			val color = FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255)
			val patternlike = HexPatternLike.of(ClientStorage.mediaLog.patterns.buffer()[index])
			val patternSettings = WorldlyPatternRenderHelpers.READABLE_SCROLL_SETTINGS
			val staticPoints = HexPatternPoints.getStaticPoints(patternlike, patternSettings, 0.0)
			val nonzappyLines = patternlike.nonZappyPoints
			val zappyPattern = makeZappy(nonzappyLines, findDupIndices(nonzappyLines), patternSettings.hops, patternSettings.variance, patternSettings.speed, patternSettings.flowIrregular, patternSettings.readabilityOffset, patternSettings.lastSegmentProp, 0.0)
			drawLineSeq(matrices.last().pose(), staticPoints.scaleVecs(zappyPattern), 0.05f, color, color, VCDrawHelper.getHelper(null, matrices, 0.001f))
		} else {
			drawSpot(matrices.last().pose(), Vec2.ZERO, 0.2f, 1f, 1f, 1f, alpha)
		}

		matrices.popPose()
	}

	fun drawStackItem(context: GuiGraphics, index: Int, alpha: Float) {
		if (index >= ClientStorage.mediaLog.stack.buffer().size || alpha == 0f)
			return
		context.pose().pushPose()
		val iotas = ClientStorage.mediaLog.stack.buffer()
		context.drawCenteredString(Minecraft.getInstance().font, iotas[index], 17, 16 * (4 - index), FastColor.ARGB32.color((alpha * 255).toInt(), 255, 255, 255))
		context.pose().popPose()
	}

	private val phases = listOf(
		Phase(0.0f, 0.2f) { ctx, t ->
			drawMishapText(ctx, t)
		},
		Phase(0.2f, 0.5f) { ctx, t ->
			val progress = Mth.clamp(t, 0f, 1f)
			val visible = (progress * 32).toInt()
			val alpha = (progress * 32) % 1
			for (i in 0 until visible)
				drawMediaLogPattern(ctx.pose(), i, 1f)
			if (visible < 31)
				drawMediaLogPattern(ctx.pose(), visible, alpha)
		},
		Phase(0.7f, 0.3f) { ctx, t ->
			val progress = Mth.clamp(t, 0f, 1f)
			val visible = (progress * 8).toInt()
			val alpha = (progress * 8) % 1
			for (i in 0 until visible)
				drawStackItem(ctx, i, 1f)
			if (visible < 7)
				drawStackItem(ctx, visible, alpha)
		}
	)

	private data class Phase(
		val start: Float,
		val duration: Float,
		val render: (GuiGraphics, Float) -> Unit
	)
}

