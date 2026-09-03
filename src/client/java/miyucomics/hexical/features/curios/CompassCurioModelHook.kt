package miyucomics.hexical.features.curios

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.CompassItemPropertyFunction
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos

object CompassCurioModelHook : InitHook() {
	override fun init() {
		ItemProperties.register(
			HexicalItems.CURIO_COMPASS,
			ResourceLocation.withDefaultNamespace("angle"),
			CompassItemPropertyFunction(CompassItemPropertyFunction.CompassTarget { world: ClientLevel, stack: ItemStack, player: Entity ->
				val data = ItemStackDataCompat.customData(stack)
				if (!data.contains("needle"))
					return@CompassTarget null
				val needle = data.getIntArray("needle")
				if (needle.size < 3) return@CompassTarget null
				return@CompassTarget GlobalPos.of(player.level().dimension(), BlockPos(needle[0], needle[1], needle[2]))
			})
		)
	}
}
