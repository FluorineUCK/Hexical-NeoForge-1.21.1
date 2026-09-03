package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf
import at.petrak.hexcasting.common.lib.HexBlocks
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.mixin.CreeperAccessor
import miyucomics.hexical.mixin.DispenserBlockInvoker
import miyucomics.hexical.mixin.PufferfishAccessor
import miyucomics.hexical.mixin.SquidInvoker
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BeehiveBlockEntity
import net.minecraft.world.level.block.entity.BellBlockEntity
import net.minecraft.world.level.block.state.properties.BellAttachType
import net.minecraft.world.level.block.state.properties.ComparatorMode
import net.minecraft.world.entity.Shearable
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.animal.Panda
import net.minecraft.world.entity.animal.Pufferfish
import net.minecraft.world.entity.animal.Squid
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.core.Registry
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.BlockTags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.BlockPos
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.ItemAbilities
import net.neoforged.neoforge.common.ItemAbility
import net.neoforged.neoforge.registries.NewRegistryEvent
import net.neoforged.neoforge.registries.RegisterEvent
import net.neoforged.neoforge.registries.RegistryBuilder

object PrestidigitationHandlersHook {
	@JvmField
	val PRESTIDIGITATION_HANDLER_KEY: ResourceKey<Registry<PrestidigitationHandler>> = ResourceKey.createRegistryKey(HexicalMain.id("prestidigitation_handler"))
	lateinit var PRESTIDIGITATION_HANDLER: Registry<PrestidigitationHandler>
		private set
	private var activeRegistrar: ((ResourceLocation, PrestidigitationHandler) -> Unit)? = null

	fun createRegistry(event: NewRegistryEvent) {
		PRESTIDIGITATION_HANDLER = event.create(RegistryBuilder(PRESTIDIGITATION_HANDLER_KEY).sync(true))
	}

	fun register(event: RegisterEvent) {
		if (event.registryKey != PRESTIDIGITATION_HANDLER_KEY) return
		activeRegistrar = { id, handler -> event.register(PRESTIDIGITATION_HANDLER_KEY, id) { handler } }
		try {
			registerEntries()
		} finally {
			activeRegistrar = null
		}
	}

	private fun registerEntries() {
		register("toggle_comparator", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(Blocks.COMPARATOR)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				setBlockState(env, pos, state.setValue(BlockStateProperties.MODE_COMPARATOR, when (state.getValue(BlockStateProperties.MODE_COMPARATOR)) {
					ComparatorMode.COMPARE -> ComparatorMode.SUBTRACT
					ComparatorMode.SUBTRACT -> ComparatorMode.COMPARE
					else -> ComparatorMode.COMPARE
				}))
			}
		})

		register("carve_pumpkin", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(Blocks.PUMPKIN)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				setBlockState(env, pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HORIZONTAL_FACING.possibleValues.random()))
			}
		})

		register("axeing", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = toolModifiedState(env, pos, ItemStack(Items.IRON_AXE), ItemAbilities.AXE_STRIP, true) != null
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				toolModifiedState(env, pos, ItemStack(Items.IRON_AXE), ItemAbilities.AXE_STRIP, false)?.let { setBlockState(env, pos, it) }
			}
		})

		register("pathing", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = toolModifiedState(env, pos, ItemStack(Items.IRON_SHOVEL), ItemAbilities.SHOVEL_FLATTEN, true) != null
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				toolModifiedState(env, pos, ItemStack(Items.IRON_SHOVEL), ItemAbilities.SHOVEL_FLATTEN, false)?.let { setBlockState(env, pos, it) }
			}
		})

		register("press_buttons", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(BlockTags.BUTTONS)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				(state.block as ButtonBlock).press(state, env.world, pos, env.castingEntity as? Player)
			}
		})

		register("extinguish_fires", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(BlockTags.FIRE)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				env.world.removeBlock(pos, false)
			}
		})

		register("create_soul_fire", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(BlockTags.SOUL_FIRE_BASE_BLOCKS) && getBlockState(env, pos.above()).isAir
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				setBlockState(env, pos.above(), Blocks.SOUL_FIRE.defaultBlockState())
			}
		})

		register("pressure_pressure_plates", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(BlockTags.PRESSURE_PLATES)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				setBlockState(env, pos, state.setValue(BlockStateProperties.POWERED, !state.getValue(BlockStateProperties.POWERED)))
			}
		})

		register("drain_cauldrons", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(BlockTags.CAULDRONS)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				setBlockState(env, pos, Blocks.CAULDRON.defaultBlockState())
			}
		})

		register("light_candle", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
				val state = getBlockState(env, pos)
				return state.`is`(BlockTags.CANDLES) || state.`is`(BlockTags.CANDLE_CAKES) || state.`is`(BlockTags.CAMPFIRES)
			}

			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				setBlockState(env, pos, state.setValue(BlockStateProperties.LIT, !state.getValue(BlockStateProperties.LIT)))
			}
		})

		register("open_doors", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
				val state = getBlockState(env, pos)
				return state.`is`(BlockTags.DOORS) || state.`is`(BlockTags.TRAPDOORS) || state.`is`(BlockTags.FENCE_GATES)
			}

			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				setBlockState(env, pos, state.setValue(BlockStateProperties.OPEN, !state.getValue(BlockStateProperties.OPEN)))
			}
		})

		register("steal_honey", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
				val state = getBlockState(env, pos)
				return state.`is`(BlockTags.BEEHIVES) && state.getValue(BeehiveBlock.HONEY_LEVEL) == 5
			}

			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				env.world.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1f, 1f)
				BeehiveBlock.dropHoneycomb(env.world, pos)
				(state.block as BeehiveBlock).releaseBeesAndResetHoneyLevel(env.world, state, pos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED)
				env.world.gameEvent(null, GameEvent.SHEAR, pos)
			}
		})

		register("play_note", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(Blocks.NOTE_BLOCK)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				env.world.blockEvent(pos, Blocks.NOTE_BLOCK, 0, 0)
				env.world.gameEvent(null, GameEvent.NOTE_BLOCK_PLAY, pos)
			}
		})

		register("ring_bell", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(Blocks.BELL)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				val facing = state.getValue(BellBlock.FACING)
				val ringDirection = when (state.getValue(BellBlock.ATTACHMENT)) {
					BellAttachType.SINGLE_WALL -> facing.getClockWise()
					BellAttachType.DOUBLE_WALL -> facing.getClockWise()
					else -> facing
				}
				(env.world.getBlockEntity(pos) as BellBlockEntity).onHit(ringDirection)
				env.world.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0f, 1.0f)
				env.world.gameEvent(env.castingEntity, GameEvent.BLOCK_CHANGE, pos)
			}
		})

		register("dispense", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlock(env, pos) is DispenserBlock
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val state = getBlockState(env, pos)
				(state.block as DispenserBlockInvoker).invokeDispenseFrom(env.world as ServerLevel, state, pos)
			}
		})

		register("prime_tnt", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = getBlockState(env, pos).`is`(Blocks.TNT)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				TntBlock.explode(env.world, pos)
				env.world.removeBlock(pos, false)
			}
		})

		register("learn_akashic", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = env.castingEntity is ServerPlayer && getBlockState(env, pos).`is`(HexBlocks.AKASHIC_BOOKSHELF.get())
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				val caster = env.castingEntity as ServerPlayer
				val iota = (env.world.getBlockEntity(pos) as BlockEntityAkashicBookshelf).iota ?: return
				CastingUtils.giveIota(caster, iota)
			}
		})

		register("trigger_impetus", object : PrestidigitationHandlerBlock() {
			override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) = env.castingEntity is ServerPlayer && getBlockState(env, pos).`is`(HexTags.Blocks.IMPETI)
			override fun affect(env: CastingEnvironment, pos: BlockPos) {
				(env.world.getBlockEntity(pos) as BlockEntityAbstractImpetus).startExecution(env.castingEntity as ServerPlayer)
			}
		})

		register("arm_stands", object : PrestidigitationHandlerEntity<ArmorStand>(ArmorStand::class.java) {
			override fun affect(env: CastingEnvironment, entity: ArmorStand) {
				entity.setShowArms(!entity.isShowArms())
				entity.playSound(SoundEvents.ARMOR_STAND_PLACE, 1f, 1f)
			}
		})

		register("disarm_tnt", object : PrestidigitationHandlerEntity<PrimedTnt>(PrimedTnt::class.java) {
			override fun affect(env: CastingEnvironment, entity: PrimedTnt) {
				if (entity.level().getBlockState(entity.blockPosition()).canBeReplaced()) {
					entity.level().setBlockAndUpdate(entity.blockPosition(), Blocks.TNT.defaultBlockState())
					entity.level().updateNeighborsAt(entity.blockPosition(), Blocks.TNT)
				}
				entity.discard()
			}
		})

		register("shear", object : PrestidigitationHandlerEntity<Shearable>(Shearable::class.java) {
			override fun affect(env: CastingEnvironment, entity: Shearable) {
				entity.shear(SoundSource.MASTER)
			}
		})

		register("milk_squids", object : PrestidigitationHandlerEntity<Squid>(Squid::class.java) {
			override fun affect(env: CastingEnvironment, entity: Squid) {
				(entity as SquidInvoker).invokeSpawnInk()
			}
		})

		register("pandas_sneeze", object : PrestidigitationHandlerEntity<Panda>(Panda::class.java) {
			override fun affect(env: CastingEnvironment, entity: Panda) {
				entity.sneeze(true)
			}
		})

		register("detonate_creepers", object : PrestidigitationHandlerEntity<Creeper>(Creeper::class.java) {
			override fun affect(env: CastingEnvironment, entity: Creeper) {
				if (entity.isIgnited) entity.entityData.set(CreeperAccessor.hexicalGetIgnitedAccessor(), false)
				else entity.ignite()
			}
		})

		register("puff_pufferfish", object : PrestidigitationHandlerEntity<Pufferfish>(Pufferfish::class.java) {
			override fun affect(env: CastingEnvironment, entity: Pufferfish) {
				if (entity.puffState != 2) {
					entity.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1f, 1f)
					(entity as PufferfishAccessor).hexicalSetInflateCounter(0)
					(entity as PufferfishAccessor).hexicalSetDeflateTimer(0)
					entity.puffState = 2
				}
			}
		})

		PrestidigitationBlockBooleans.init()
		PrestidigitationBlockTransformations.init()
	}

	private fun toolModifiedState(
		env: CastingEnvironment,
		pos: BlockPos,
		stack: ItemStack,
		ability: ItemAbility,
		simulate: Boolean
	): net.minecraft.world.level.block.state.BlockState? {
		val hit = BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false)
		val context = object : UseOnContext(env.world, env.castingEntity as? Player, InteractionHand.MAIN_HAND, stack, hit) {}
		return env.world.getBlockState(pos).getToolModifiedState(context, ability, simulate)
	}

	fun register(name: String, handler: PrestidigitationHandler) {
		checkNotNull(activeRegistrar) { "Prestidigitation handlers must register during RegisterEvent" }(HexicalMain.id(name), handler)
	}
}
