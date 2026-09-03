package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.hexcompat.serializePigment
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

object OpAutograph : SpellAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (env.castingEntity !is Player)
			throw MishapBadCaster()
		if (env !is StaffCastEnv)
			throw NeedsStaffMishap()
		val stack = env.getHeldItemToOperateOn { true }
		if (stack == null)
			throw MishapBadOffhandItem.of(null, "anything")
		return SpellAction.Result(Spell(stack.stack), 0, listOf())
	}

	private data class Spell(val stack: ItemStack) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val caster = env.castingEntity as ServerPlayer
			ItemStackDataCompat.update(stack) { root ->
				val list = root.getList("autographs", net.minecraft.nbt.Tag.TAG_COMPOUND.toInt())
				list.removeIf { compound -> (compound as CompoundTag).getString("name") == caster.scoreboardName }
				val compound = CompoundTag()
				compound.putString("name", caster.scoreboardName)
				compound.put("pigment", serializePigment(IXplatAbstractions.INSTANCE.getPigment(caster)))
				list.add(0, compound)
				root.put("autographs", list)
			}
		}
	}
}
