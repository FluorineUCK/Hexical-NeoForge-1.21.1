package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.lib.HexSounds
import miyucomics.hexical.features.curios.CurioItem
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand

class BaseCurio : CurioItem() {
	override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
		user.swing(hand)
		world.playSound(null, user.x, user.y, user.z, HexSounds.CAST_HERMES.value(), SoundSource.MASTER, 0.25f, 1f)
	}
}