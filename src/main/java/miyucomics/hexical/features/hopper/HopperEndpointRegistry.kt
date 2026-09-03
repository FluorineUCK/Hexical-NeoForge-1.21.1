package miyucomics.hexical.features.hopper

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.features.hopper.targets.*
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.ChestBoat
import net.minecraft.world.entity.vehicle.MinecartChest
import net.minecraft.world.entity.vehicle.MinecartHopper
import net.minecraft.world.Container
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import kotlin.math.abs

object HopperEndpointRegistry : InitHook() {
	private val resolvers = mutableListOf<HopperEndpointResolver>()

	override fun init() {
		register { iota, env, slot ->
			val caster = env.castingEntity
			if (iota is EntityIota && iota.getEntity(env.world) == caster && caster is ServerPlayer && slot == -1)
				return@register WristpocketEndpoint(caster)
			null
		}

		register { iota, env, slot ->
			val caster = env.castingEntity
			if (iota is EntityIota && iota.getEntity(env.world) == caster && caster is ServerPlayer)
				return@register getSlottedInventory(caster.inventory, slot, iota)
			null
		}

		registerInventoryEntity<ArmorStand> { ArmorStandInventory(it) }
		registerInventoryEntity<ChestBoat> { it }
		registerInventoryEntity<MinecartChest> { it }
		registerInventoryEntity<MinecartHopper> { it }
		registerEntityEndpoint<ItemEntity> { DroppedItemEndpoint(it) }
		registerEntityEndpoint<ItemFrame> { ItemFrameEndpoint(it) }

		register { iota, env, slot ->
			if (iota !is Vec3Iota)
				return@register null
			val vec = iota.vec3
			val blockPos = BlockPos.containing(vec)
			env.assertPosInRange(blockPos)
			val inventory = env.world.getBlockEntity(blockPos)
			if (inventory is WorldlyContainer) {
				if (slot != null)
					return@register SlottedInventoryEndpoint(inventory, slot, iota)
				val dx = vec.x - (blockPos.x + 0.5)
				val dy = vec.y - (blockPos.y + 0.5)
				val dz = vec.z - (blockPos.z + 0.5)
				val ax = abs(dx)
				val ay = abs(dy)
				val az = abs(dz)
				val threshold = 0.05
				val direction = when {
					ax < threshold && ay < threshold && az < threshold -> null
					ax >= ay && ax >= az -> if (dx > 0) Direction.EAST else Direction.WEST
					ay >= ax && ay >= az -> if (dy > 0) Direction.UP else Direction.DOWN
					else -> if (dz > 0) Direction.SOUTH else Direction.NORTH
				}

				return@register if (direction != null)
					SidedInventoryEndpoint(inventory, direction)
				else
					InventoryEndpoint(inventory)
			}
			null
		}

		register { iota, env, slot ->
			if (iota !is Vec3Iota)
				return@register null
			val blockPos = BlockPos.containing(iota.vec3)
			env.assertPosInRange(blockPos)
			val inventory = env.world.getBlockEntity(blockPos)
			if (inventory is Container)
				return@register getSlottedInventory(inventory, slot, iota)
			null
		}

		register { iota, env, _ ->
			if (iota is Vec3Iota) {
				env.assertVecInRange(iota.vec3)
				return@register DispenseEndpoint(iota.vec3, env.world)
			}
			null
		}

		register { iota, env, slot ->
			if (iota is NullIota && env.castingEntity is Player)
				return@register getSlottedInventory((env.castingEntity as Player).enderChestInventory, slot, iota)
			null
		}
	}

	fun register(resolver: HopperEndpointResolver) {
		resolvers += resolver
	}

	fun resolve(iota: Iota, env: CastingEnvironment, slot: Int?): HopperEndpoint? {
		return resolvers.firstNotNullOfOrNull { it.resolve(iota, env, slot) }
	}

	fun getSlottedInventory(inventory: Container, slot: Int?, iota: Iota): HopperEndpoint {
		if (slot == null)
			return InventoryEndpoint(inventory)
		return SlottedInventoryEndpoint(inventory, slot, iota)
	}

	private inline fun <reified T : Entity> registerInventoryEntity(crossinline getInventory: (T) -> Container) {
		register { iota, env, slot ->
			val entity = (iota as? EntityIota)?.getEntity(env.world) as? T ?: return@register null
			if (entity.isRemoved) return@register null
			env.assertEntityInRange(entity)
			getSlottedInventory(getInventory(entity), slot, iota)
		}
	}

	private inline fun <reified T : Entity> registerEntityEndpoint(crossinline endpoint: (T) -> HopperEndpoint) {
		register { iota, env, _ ->
			val entity = (iota as? EntityIota)?.getEntity(env.world) as? T ?: return@register null
			if (entity.isRemoved) return@register null
			env.assertEntityInRange(entity)
			endpoint(entity)
		}
	}
}
