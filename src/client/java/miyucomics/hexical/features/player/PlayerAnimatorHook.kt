package miyucomics.hexical.features.player

import dev.kosmx.playerAnim.api.layered.ModifierLayer
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess
import miyucomics.hexical.features.curios.FluteCurioPlayerModel
import miyucomics.hexical.features.curios.HandbellCurioPlayerModel
import miyucomics.hexical.features.curios.curios.HandbellCurio
import miyucomics.hexical.features.dance.DanceAnimation
import miyucomics.hexical.features.evocation.EvocationAnimation
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.InteractionHand

object PlayerAnimatorHook : InitHook() {
	override fun init() {
		PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register { player, stack ->
			stack.addAnimLayer(300, DanceAnimation(player))

			stack.addAnimLayer(200, EvocationAnimation(player))

			stack.addAnimLayer(100, ModifierLayer(FluteCurioPlayerModel(player)).also {
				it.addModifierBefore(object : MirrorModifier() {
					override fun isEnabled() = (player.mainArm == HumanoidArm.LEFT) xor (player.getItemInHand(InteractionHand.OFF_HAND).`is`(
						HexicalItems.CURIO_FLUTE) && !player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_FLUTE))
				})
			})

			val handbellAnimation = ModifierLayer(HandbellCurioPlayerModel(player)).also {
				it.addModifierBefore(object : MirrorModifier() {
					override fun isEnabled() = (player.mainArm == HumanoidArm.LEFT) xor (player.getItemInHand(InteractionHand.OFF_HAND).`is`(
						HexicalItems.CURIO_HANDBELL) && !player.getItemInHand(InteractionHand.MAIN_HAND).`is`(HexicalItems.CURIO_HANDBELL))
				})
			}
			PlayerAnimationAccess.getPlayerAssociatedData(player).set(HandbellCurio.ANIMATION_ID, handbellAnimation)
			stack.addAnimLayer(100, handbellAnimation)
		}
	}
}
