package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexpose.iotas.getItemStack
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

object OpHasAutograph : ConstMediaAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val stack = args.getItemStack(0, argc)
		val data = ItemStackDataCompat.customData(stack)
		if (!data.contains("autographs"))
			return false.asActionResult
		val player = args.getPlayer(env.world, 1, argc)
		val list = data.getList("autographs", Tag.TAG_COMPOUND.toInt())
		return (list.count { (it as CompoundTag).getString("name") == player.scoreboardName } > 0).asActionResult
	}
}
