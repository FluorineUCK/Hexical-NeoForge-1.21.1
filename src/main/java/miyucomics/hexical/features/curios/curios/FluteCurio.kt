package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.msgs.MsgBeepS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.features.curios.CurioItem
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

object FluteCurio : CurioItem() {
	override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.pass(user.getItemInHand(hand))
		playSound(world, user, hand, floor((Mth.clamp(user.xRot, -40f, 40f) / -80f + 0.5f) * 25f).toInt())
		return InteractionResultHolder.pass(user.getItemInHand(hand))
	}

	override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
		val note = stack.lastOrNull()
		if (note !is DoubleIota || note.double < 0 || note.double > 24 || abs(note.double - note.double.roundToInt()) > DoubleIota.TOLERANCE)
			return
		playSound(world, user, hand, note.double.toInt())
	}

	private fun playSound(world: Level, user: Player, hand: InteractionHand, note: Int) {
		val upPitch = (-user.xRot + 90) * (Math.PI.toFloat() / 180)
		val yaw = -user.yHeadRot * (Math.PI.toFloat() / 180)
		val j = Mth.cos(upPitch).toDouble()
		val upAxis = Vec3(Mth.sin(yaw).toDouble() * j, Mth.sin(upPitch).toDouble(), Mth.cos(yaw).toDouble() * j)

		val position = user.eyePosition.add(user.lookAngle.cross(upAxis).scale(if (hand == InteractionHand.MAIN_HAND) 0.6 else -0.6))
		IXplatAbstractions.INSTANCE.sendPacketNear(position, 128.0, world as ServerLevel, MsgBeepS2C(position, note, NoteBlockInstrument.FLUTE))
		world.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, position)
	}
}
