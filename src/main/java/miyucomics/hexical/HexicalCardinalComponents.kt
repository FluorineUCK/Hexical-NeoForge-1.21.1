package miyucomics.hexical

import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.api.addldata.ItemDelegatingEntityIotaHolder
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.forge.cap.HexCapabilities
import at.petrak.hexcasting.forge.cap.adimpl.CapEntityIotaHolder
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticMediaHolder
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollEntity
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.inits.HexicalEntities
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent

/** NeoForge replacement for the Fabric Cardinal Components entrypoint. */
object HexicalCardinalComponents {
	fun register(event: RegisterCapabilitiesEvent) {
		event.registerEntity(HexCapabilities.Entity.IOTA, HexicalEntities.ANIMATED_SCROLL_ENTITY) { entity, _ ->
			CapEntityIotaHolder.Wrapper(AnimatedScrollReader(entity))
		}
		event.registerItem(
			HexCapabilities.Item.MEDIA,
			{ stack, _ -> CapStaticMediaHolder({ MediaConstants.DUST_UNIT / 10 }, ADMediaHolder.AMETHYST_DUST_PRIORITY, stack) },
			HexicalItems.HEX_GUMMY
		)
	}
}

class AnimatedScrollReader(scrollEntity: AnimatedScrollEntity) : ItemDelegatingEntityIotaHolder(
	{ scrollEntity.scroll.copy() },
	{ stack ->
		scrollEntity.scroll = stack
		val root = ItemStackDataCompat.customData(stack)
		scrollEntity.patterns = root.getList("patterns", Tag.TAG_COMPOUND.toInt()).map { it as CompoundTag }
		scrollEntity.updateRender()
	}
)
