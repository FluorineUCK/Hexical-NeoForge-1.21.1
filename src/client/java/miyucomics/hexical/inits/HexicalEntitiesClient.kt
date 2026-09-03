package miyucomics.hexical.inits

import miyucomics.hexical.features.animated_scrolls.AnimatedScrollRenderer
import miyucomics.hexical.features.magic_missile.MagicMissileRenderer
import miyucomics.hexical.features.specklikes.MeshRenderer
import miyucomics.hexical.features.specklikes.SpeckRenderer
import miyucomics.hexical.features.spike.SpikeRenderer
import miyucomics.hexical.inits.HexicalEntities.ANIMATED_SCROLL_ENTITY
import miyucomics.hexical.inits.HexicalEntities.MAGIC_MISSILE_ENTITY
import miyucomics.hexical.inits.HexicalEntities.MESH_ENTITY
import miyucomics.hexical.inits.HexicalEntities.SPECK_ENTITY
import miyucomics.hexical.inits.HexicalEntities.SPIKE_ENTITY
import miyucomics.hexical.features.media_jar.MediaJarBlockEntityRenderer
import miyucomics.hexical.features.pedestal.PedestalBlockEntityRenderer
import net.neoforged.neoforge.client.event.EntityRenderersEvent

object HexicalEntitiesClient {
	fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
		event.registerEntityRenderer(ANIMATED_SCROLL_ENTITY, ::AnimatedScrollRenderer)
		event.registerEntityRenderer(MAGIC_MISSILE_ENTITY, ::MagicMissileRenderer)
		event.registerEntityRenderer(SPIKE_ENTITY, ::SpikeRenderer)
		event.registerEntityRenderer(SPECK_ENTITY, ::SpeckRenderer)
		event.registerEntityRenderer(MESH_ENTITY, ::MeshRenderer)
		event.registerBlockEntityRenderer(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY) { MediaJarBlockEntityRenderer() }
		event.registerBlockEntityRenderer(HexicalBlocks.PEDESTAL_BLOCK_ENTITY, ::PedestalBlockEntityRenderer)
	}
}
