package miyucomics.hexical.features.curios

import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import miyucomics.hexical.features.curios.curios.HandbellCurio
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.network.PlayerUuidPayload
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.neoforge.client.event.ModelEvent

object HandbellCurioItemModel : InitHook() {
	@JvmField val heldHandbellModel: ModelResourceLocation = ModelResourceLocation.standalone(HexicalMain.id("item/held_curio_handbell"))
	@JvmField val handbellModel: ModelResourceLocation = ModelResourceLocation.inventory(HexicalMain.id("curio_handbell"))

	override fun init() = Unit

	fun registerModels(event: ModelEvent.RegisterAdditional) = event.register(heldHandbellModel)

	fun handle(payload: PlayerUuidPayload) {
		val player = Minecraft.getInstance().level?.getPlayerByUUID(payload.playerId) as? AbstractClientPlayer ?: return
		val layer = PlayerAnimationAccess.getPlayerAssociatedData(player)[HandbellCurio.ANIMATION_ID]
		val handbellAnimation = (layer as? ModifierLayer<HandbellCurioPlayerModel>)?.animation ?: return
		handbellAnimation.shakingBellTimer = 10
	}
}
