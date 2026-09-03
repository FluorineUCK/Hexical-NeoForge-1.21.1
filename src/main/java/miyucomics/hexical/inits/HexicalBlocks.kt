package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.hex_candles.HexCandleBlock
import miyucomics.hexical.features.hex_candles.HexCandleBlockEntity
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlock
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlockEntity
import miyucomics.hexical.features.mage_blocks.MageBlock
import miyucomics.hexical.features.mage_blocks.MageBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarItem
import miyucomics.hexical.features.pedestal.PedestalBlock
import miyucomics.hexical.features.pedestal.PedestalBlockEntity
import miyucomics.hexical.features.sentinel_beds.SentinelBedBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.WoolCarpetBlock
import net.minecraft.world.level.block.PinkPetalsBlock
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.item.DyeColor
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalBlocks {
	val CONJURABLE_FLOWERS: TagKey<Block> = TagKey.create(Registries.BLOCK, HexicalMain.id("conjurable_flower"))

	val HEX_CANDLE_BLOCK: HexCandleBlock = HexCandleBlock()
	val HEX_CANDLE_CAKE_BLOCK: HexCandleCakeBlock = HexCandleCakeBlock()

	@JvmField
	val MAGE_BLOCK: MageBlock = MageBlock()
	val MEDIA_JAR_BLOCK: MediaJarBlock = MediaJarBlock()

	@JvmField
	val CASTING_CARPET = WoolCarpetBlock(DyeColor.PURPLE, Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(0.1f).sound(SoundType.WOOL).ignitedByLava())
	val CASTING_CARPET_ITEM = BlockItem(CASTING_CARPET, Item.Properties())

	@JvmField
	val SENTINEL_BED_BLOCK = SentinelBedBlock()

	val PERIWINKLE_FLOWER = PinkPetalsBlock(Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY))
	val PERIWINKLE_FLOWER_ITEM = BlockItem(PERIWINKLE_FLOWER, Item.Properties())

	@JvmField
	val MEDIA_JAR_ITEM = MediaJarItem()
	@JvmField
	val MAGE_BLOCK_ITEM = BlockItem(MAGE_BLOCK, Item.Properties())
	val HEX_CANDLE_ITEM = BlockItem(HEX_CANDLE_BLOCK, Item.Properties())
	val SENTINEL_BED_ITEM = BlockItem(SENTINEL_BED_BLOCK, Item.Properties())

	val PEDESTAL_BLOCK = PedestalBlock()
	val PEDESTAL_ITEM = BlockItem(PEDESTAL_BLOCK, Item.Properties())

	@JvmField
	val MAGE_BLOCK_ENTITY: BlockEntityType<MageBlockEntity> = BlockEntityType.Builder.of(::MageBlockEntity, MAGE_BLOCK).build(null)
	val HEX_CANDLE_BLOCK_ENTITY: BlockEntityType<HexCandleBlockEntity> = BlockEntityType.Builder.of(::HexCandleBlockEntity, HEX_CANDLE_BLOCK).build(null)
	val HEX_CANDLE_CAKE_BLOCK_ENTITY: BlockEntityType<HexCandleCakeBlockEntity> = BlockEntityType.Builder.of(::HexCandleCakeBlockEntity, HEX_CANDLE_CAKE_BLOCK).build(null)
	val MEDIA_JAR_BLOCK_ENTITY: BlockEntityType<MediaJarBlockEntity> = BlockEntityType.Builder.of(::MediaJarBlockEntity, MEDIA_JAR_BLOCK).build(null)
	val PEDESTAL_BLOCK_ENTITY: BlockEntityType<PedestalBlockEntity> = BlockEntityType.Builder.of(::PedestalBlockEntity, PEDESTAL_BLOCK).build(null)

	fun register(event: RegisterEvent) {
		when (event.registryKey) {
			Registries.BLOCK -> {
				event.register(Registries.BLOCK, HexicalMain.id("hex_candle")) { HEX_CANDLE_BLOCK }
				event.register(Registries.BLOCK, HexicalMain.id("hex_candle_cake")) { HEX_CANDLE_CAKE_BLOCK }
				event.register(Registries.BLOCK, HexicalMain.id("mage_block")) { MAGE_BLOCK }
				event.register(Registries.BLOCK, HexicalMain.id("media_jar")) { MEDIA_JAR_BLOCK }
				event.register(Registries.BLOCK, HexicalMain.id("sentinel_bed")) { SENTINEL_BED_BLOCK }
				event.register(Registries.BLOCK, HexicalMain.id("periwinkle")) { PERIWINKLE_FLOWER }
				event.register(Registries.BLOCK, HexicalMain.id("casting_carpet")) { CASTING_CARPET }
				event.register(Registries.BLOCK, HexicalMain.id("pedestal")) { PEDESTAL_BLOCK }
			}
			Registries.ITEM -> {
				event.register(Registries.ITEM, HexicalMain.id("mage_block")) { MAGE_BLOCK_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("hex_candle")) { HEX_CANDLE_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("sentinel_bed")) { SENTINEL_BED_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("media_jar")) { MEDIA_JAR_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("periwinkle")) { PERIWINKLE_FLOWER_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("casting_carpet")) { CASTING_CARPET_ITEM }
				event.register(Registries.ITEM, HexicalMain.id("pedestal")) { PEDESTAL_ITEM }
			}
			Registries.BLOCK_ENTITY_TYPE -> {
				event.register(Registries.BLOCK_ENTITY_TYPE, HexicalMain.id("hex_candle")) { HEX_CANDLE_BLOCK_ENTITY }
				event.register(Registries.BLOCK_ENTITY_TYPE, HexicalMain.id("hex_candle_cake")) { HEX_CANDLE_CAKE_BLOCK_ENTITY }
				event.register(Registries.BLOCK_ENTITY_TYPE, HexicalMain.id("media_jar")) { MEDIA_JAR_BLOCK_ENTITY }
				event.register(Registries.BLOCK_ENTITY_TYPE, HexicalMain.id("mage_block")) { MAGE_BLOCK_ENTITY }
				event.register(Registries.BLOCK_ENTITY_TYPE, HexicalMain.id("pedestal")) { PEDESTAL_BLOCK_ENTITY }
			}
		}
	}
}
