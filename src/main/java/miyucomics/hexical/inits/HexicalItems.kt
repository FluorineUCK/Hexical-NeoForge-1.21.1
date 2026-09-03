package miyucomics.hexical.inits

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import at.petrak.hexcasting.common.items.ItemStaff
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollItem
import miyucomics.hexical.features.confection.HexburstItem
import miyucomics.hexical.features.confection.HextitoItem
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.features.grimoires.GrimoireItem
import miyucomics.hexical.features.lamps.ArchLampItem
import miyucomics.hexical.features.lamps.HandLampItem
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_log.MediaLogItem
import miyucomics.hexical.features.periwinkle.LeiItem
import miyucomics.hexical.features.scarabs.ScarabBeetleItem
import miyucomics.hexical.hexcompat.ItemStackDataCompat
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.*
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.food.FoodProperties
import net.minecraft.nbt.CompoundTag
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.level.Level
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalItems {
	private val HEXICAL_GROUP_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(Registries.CREATIVE_MODE_TAB, HexicalMain.id("general"))
	private val ITEMS = linkedMapOf<net.minecraft.resources.ResourceLocation, Item>()

	private fun <T : Item> registerItem(path: String, item: T): T {
		val id = HexicalMain.id(path)
		check(ITEMS.put(id, item) == null) { "Duplicate Hexical item id: $id" }
		return item
	}

	val SMALL_ANIMATED_SCROLL_ITEM = registerItem("animated_scroll_small", AnimatedScrollItem(1))
	val MEDIUM_ANIMATED_SCROLL_ITEM = registerItem("animated_scroll_medium", AnimatedScrollItem(2))
	val LARGE_ANIMATED_SCROLL_ITEM = registerItem("animated_scroll_large", AnimatedScrollItem(3))
	@JvmField val HAND_LAMP_ITEM = registerItem("hand_lamp", HandLampItem())
	@JvmField val ARCH_LAMP_ITEM = registerItem("arch_lamp", ArchLampItem())

	val SCARAB_BEETLE_ITEM = registerItem("scarab_beetle", ScarabBeetleItem())
	@JvmField val GRIMOIRE_ITEM = registerItem("grimoire", GrimoireItem())

	val HEX_GUMMY = registerItem("hex_gummy", Item(Properties().food(FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).alwaysEdible().fast().build())))
	val HEXBURST_ITEM = registerItem("hexburst", HexburstItem())
	val HEXTITO_ITEM = registerItem("hextito", HextitoItem())

	private val MEDIA_LOG_ITEM = registerItem("media_log", MediaLogItem())

	@JvmField
	val LEI = registerItem("lei", LeiItem)
	private val GAUNTLET_STAFF = registerItem("gauntlet_staff", ItemStaff(Properties().stacksTo(1)))
	private val LIGHTNING_ROD_STAFF = registerItem("lightning_rod_staff", ItemStaff(Properties().stacksTo(1)))

	val CURIO_NAMES: List<String> = listOf("bismuth", "clover", "compass", "conch", "cube", "flute", "handbell", "heart", "interlock", "key", "staff", "charm", "strange", "beauty", "truth", "up", "down")
	val CURIOS: List<Item> = CURIO_NAMES.map { registerItem("curio_$it", CurioItem.getCurioFromName(it)) }
	@JvmField val CURIO_COMPASS = CURIOS[CURIO_NAMES.indexOf("compass")]
	@JvmField val CURIO_FLUTE = CURIOS[CURIO_NAMES.indexOf("flute")]
	@JvmField val CURIO_HANDBELL = CURIOS[CURIO_NAMES.indexOf("handbell")]
	@JvmField val CURIO_STAFF = CURIOS[CURIO_NAMES.indexOf("staff")]

	val PLUSHIE_NAMES: List<String> = listOf("hexxy", "irissy", "pentxxy", "quadxxy", "thothy", "flexxy")
	val PLUSHIES: List<Item> = PLUSHIE_NAMES.map { registerItem("plush_$it", Item(Properties().stacksTo(1))) }
	val TCHOTCHKE_ITEM = registerItem("tchotchke", TchotchkeItem())
	@JvmStatic
	fun randomPlush() = ItemStack(PLUSHIES.random())

	val HEXICAL_GROUP: CreativeModeTab = CreativeModeTab.builder()
		.icon { ItemStack(CURIO_COMPASS) }
		.title(Component.translatable("itemGroup.hexical.general"))
		.displayItems { _, entries ->
			entries.accept(ItemStack(HAND_LAMP_ITEM).also { IXplatAbstractions.INSTANCE.findHexHolder(it)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT) })
			entries.accept(ItemStack(ARCH_LAMP_ITEM).also { IXplatAbstractions.INSTANCE.findHexHolder(it)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT) })

			entries.accept(ItemStack(SMALL_ANIMATED_SCROLL_ITEM))
			entries.accept(ItemStack(MEDIUM_ANIMATED_SCROLL_ITEM))
			entries.accept(ItemStack(LARGE_ANIMATED_SCROLL_ITEM))

			entries.accept(ItemStack(HEX_GUMMY))

			entries.accept(ItemStack(GRIMOIRE_ITEM))
			entries.accept(ItemStack(SCARAB_BEETLE_ITEM))
			entries.accept(ItemStack(LEI))

			entries.accept(ItemStack(MEDIA_LOG_ITEM))
			entries.accept(ItemStack(GAUNTLET_STAFF))
			entries.accept(ItemStack(LIGHTNING_ROD_STAFF))

			entries.accept(ItemStack(HexicalBlocks.MEDIA_JAR_ITEM).also {
				val compound = CompoundTag().also { data ->
					data.putString("id", HexicalMain.id("media_jar").toString())
					data.putLong("media", MediaJarBlock.MAX_CAPACITY)
				}
				ItemStackDataCompat.setBlockEntityData(it, compound)
			})
			entries.accept(ItemStack(HexicalBlocks.HEX_CANDLE_ITEM))
			entries.accept(ItemStack(HexicalBlocks.CASTING_CARPET_ITEM))
			entries.accept(ItemStack(HexicalBlocks.SENTINEL_BED_ITEM))
			entries.accept(ItemStack(HexicalBlocks.PERIWINKLE_FLOWER_ITEM))
			entries.accept(ItemStack(HexicalBlocks.PEDESTAL_ITEM))

			for (item in CURIOS)
				entries.accept(item)
			for (item in PLUSHIES)
				entries.accept(item)
		}
		.build()

	fun register(event: RegisterEvent) {
		when (event.registryKey) {
			Registries.ITEM -> ITEMS.forEach { (id, item) ->
				event.register(Registries.ITEM, id) { item }
			}
			Registries.CREATIVE_MODE_TAB ->
				event.register(Registries.CREATIVE_MODE_TAB, HEXICAL_GROUP_KEY.location()) { HEXICAL_GROUP }
		}
	}
}

class TchotchkeItem : ItemPackagedHex(Properties().stacksTo(1)) {
	override fun canDrawMediaFromInventory(stack: ItemStack) = false
	override fun isBarVisible(stack: ItemStack) = false
	override fun canRecharge(stack: ItemStack) = false
	override fun breakAfterDepletion() = true
	override fun cooldown() = 0

	override fun use(world: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (world.isClientSide)
			return InteractionResultHolder.success(player.getItemInHand(usedHand))
		val stack = player.getItemInHand(usedHand)
		if (hasHex(stack) && getMedia(stack) > 0) {
			val charmed = ItemStack(Items.STICK)
			ItemStackDataCompat.update(charmed) { root ->
				root.putCompound("charmed", CompoundTag().also {
					it.putLong("media", getMedia(stack))
					it.putLong("max_media", getMaxMedia(stack))
					it.putList("hex", HexSerialization.serializeHex(getHex(stack, world as ServerLevel)!!))
					it.putIntArray("normal_inputs", listOf(0, 1))
					it.putIntArray("sneak_inputs", listOf(0, 1))
				})
			}
			player.setItemInHand(usedHand, charmed)
		}
		return InteractionResultHolder.success(player.getItemInHand(usedHand))
	}

	override fun appendHoverText(stack: ItemStack, context: TooltipContext, lines: MutableList<Component>, advanced: TooltipFlag) {
		lines.add(Component.literal("Right-click this item to get a charmed stick.").withStyle(ChatFormatting.RED))
	}
}
