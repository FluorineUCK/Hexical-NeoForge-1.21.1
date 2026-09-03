package miyucomics.hexical.features.block_mimicry

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.mixin.DispenserBlockInvoker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.core.dispenser.DispenseItemBehavior
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.DispenserBlockEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ArrowItem
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

object OpDispense : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val item = args.getItemEntity(env.world, 0, argc)
		env.assertEntityInRange(item)
		val stack = item.item
		val behavior = (Blocks.DISPENSER as DispenserBlockInvoker).invokeGetDispenseMethod(env.world, stack)
		if (behavior == DispenseItemBehavior.NOOP || stack.isEmpty)
			return SpellAction.Result(Noop(item), 0, listOf())

		var cost = MediaConstants.DUST_UNIT / 2
		if (stack.item is ArrowItem)
			cost = MediaConstants.CRYSTAL_UNIT

		val position = args.getBlockPos(1, argc)
		env.assertPosInRange(position)

		val dirRaw = args.getBlockPos(2, argc)
		val direction = Direction.fromDelta(dirRaw.x, dirRaw.y, dirRaw.z) ?: throw MishapInvalidIota.of(args[2], 0, "axis_vector")

		return SpellAction.Result(Spell(item, behavior, position, direction), cost, listOf())
	}

	private data class Noop(val item: ItemEntity) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
	}

	private data class Spell(val item: ItemEntity, val behavior: DispenseItemBehavior, val position: BlockPos, val direction: Direction) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val stack = item.item
			val fakeDispenser: BlockState = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, direction)
			val blockEntity = DispenserBlockEntity(position, fakeDispenser)

			val blockPointer = BlockSource(env.world, blockEntity.blockPos, fakeDispenser, blockEntity)

			item.item = behavior.dispense(blockPointer, stack)

			val spawnPos = position.getCenter()
			for (i in 0 until blockEntity.getContainerSize()) {
				val leftoverStack = blockEntity.getItem(i)
				if (leftoverStack.isEmpty)
					continue
				env.world.addFreshEntity(ItemEntity(env.world, spawnPos.x, spawnPos.y, spawnPos.z, leftoverStack))
			}
		}
	}
}
