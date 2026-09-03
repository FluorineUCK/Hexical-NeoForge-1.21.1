package miyucomics.hexical

import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltip
import miyucomics.hexical.features.animated_scrolls.AnimatedPatternTooltipComponent
import miyucomics.hexical.features.confetti.ClientConfettiReceiver
import miyucomics.hexical.features.curios.HandbellCurioItemModel
import miyucomics.hexical.features.curios.FluteCurioItemModel
import miyucomics.hexical.features.evocation.ClientEvocationReceiver
import miyucomics.hexical.features.lesser_sentinels.ClientLesserSentinelReceiver
import miyucomics.hexical.features.media_jar.MediaJarItemRenderer
import miyucomics.hexical.features.media_jar.MediaJarShader
import miyucomics.hexical.features.media_log.ClientMediaLogReceiver
import miyucomics.hexical.features.mage_blocks.MageBlockModelLoadingHook
import miyucomics.hexical.features.shaders.ClientShaderReceiver
import miyucomics.hexical.inits.*
import miyucomics.hexical.network.*
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent
import net.neoforged.neoforge.client.event.RegisterShadersEvent
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.client.event.ClientTickEvent

/** Physical-client bootstrap. This class is never loaded on a dedicated server. */
@EventBusSubscriber(modid = HexicalMain.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object HexicalClient {
	private var initialized = false

	@JvmStatic
	@SubscribeEvent
	fun onClientSetup(event: FMLClientSetupEvent) {
		event.enqueueWork {
			if (!initialized) {
				initialized = true
				HexicalBlocksClient.clientInit()
				HexicalHooksClient.init()
				HexicalNetworking.clientHandler = ::handlePayload
				NeoForge.EVENT_BUS.addListener(::onClientTick)
			}
		}
	}

	@JvmStatic
	@SubscribeEvent
	fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) =
		HexicalEntitiesClient.registerRenderers(event)

	@JvmStatic
	@SubscribeEvent
	fun registerParticles(event: RegisterParticleProvidersEvent) =
		HexicalParticlesClient.registerProviders(event)

	@JvmStatic
	@SubscribeEvent
	fun registerShaders(event: RegisterShadersEvent) = MediaJarShader.registerShader(event)

	@JvmStatic
	@SubscribeEvent
	fun registerKeyMappings(event: RegisterKeyMappingsEvent) =
		HexicalKeybinds.register(event)

	@JvmStatic
	@SubscribeEvent
	fun registerTooltipComponents(event: RegisterClientTooltipComponentFactoriesEvent) {
		event.register(AnimatedPatternTooltip::class.java, ::AnimatedPatternTooltipComponent)
	}

	@JvmStatic
	@SubscribeEvent
	fun registerAdditionalModels(event: ModelEvent.RegisterAdditional) {
		FluteCurioItemModel.registerModels(event)
		HandbellCurioItemModel.registerModels(event)
	}

	@JvmStatic
	@SubscribeEvent
	fun modifyBakingResult(event: ModelEvent.ModifyBakingResult) =
		MageBlockModelLoadingHook.modifyBakingResult(event)

	@JvmStatic
	@SubscribeEvent
	fun registerBlockColors(event: RegisterColorHandlersEvent.Block) =
		MageBlockModelLoadingHook.registerBlockColors(event)

	@JvmStatic
	@SubscribeEvent
	fun registerClientExtensions(event: RegisterClientExtensionsEvent) {
		val renderer = MediaJarItemRenderer()
		event.registerItem(object : IClientItemExtensions {
			override fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = renderer
		}, HexicalBlocks.MEDIA_JAR_ITEM)
	}

	private fun onClientTick(event: ClientTickEvent.Post) {
		ClientStorage.ticks += 1
	}

	private fun handlePayload(payload: CustomPacketPayload) {
		when (payload) {
			is ConfettiPayload -> ClientConfettiReceiver.handle(payload)
			is PlayerUuidPayload -> when (payload.type()) {
				PlayerUuidPayload.HANDBELL_TYPE -> HandbellCurioItemModel.handle(payload)
				PlayerUuidPayload.EVOCATION_START_TYPE -> ClientEvocationReceiver.handle(payload, true)
				PlayerUuidPayload.EVOCATION_END_TYPE -> ClientEvocationReceiver.handle(payload, false)
			}
			is MediaLogPayload -> ClientMediaLogReceiver.handle(payload)
			is LesserSentinelsPayload -> ClientLesserSentinelReceiver.handle(payload)
			is ShaderPayload -> ClientShaderReceiver.handle(payload)
		}
	}
}
