package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.animated_scrolls.AnimatedScrollEntity
import miyucomics.hexical.features.magic_missile.MagicMissileEntity
import miyucomics.hexical.features.specklikes.mesh.MeshEntity
import miyucomics.hexical.features.specklikes.speck.SpeckEntity
import miyucomics.hexical.features.spike.SpikeEntity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.RegisterEvent

object HexicalEntities {
	val ANIMATED_SCROLL_ENTITY: EntityType<AnimatedScrollEntity> = EntityType.Builder.of(::AnimatedScrollEntity, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(10).updateInterval(1).build(HexicalMain.MOD_ID + ":animated_scroll")
	val MAGIC_MISSILE_ENTITY: EntityType<MagicMissileEntity> = EntityType.Builder.of(::MagicMissileEntity, MobCategory.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build(HexicalMain.MOD_ID + ":magic_missile")
	val SPIKE_ENTITY: EntityType<SpikeEntity> = EntityType.Builder.of(::SpikeEntity, MobCategory.MISC).sized(1f, 1f).eyeHeight(0.5f).clientTrackingRange(10).updateInterval(1).build(HexicalMain.MOD_ID + ":spike")
	val SPECK_ENTITY: EntityType<SpeckEntity> = EntityType.Builder.of(::SpeckEntity, MobCategory.MISC).sized(0.5f, 0.5f).eyeHeight(0.25f).clientTrackingRange(32).updateInterval(1).build(HexicalMain.MOD_ID + ":speck")
	val MESH_ENTITY: EntityType<MeshEntity> = EntityType.Builder.of(::MeshEntity, MobCategory.MISC).sized(0.5f, 0.5f).eyeHeight(0.25f).clientTrackingRange(32).updateInterval(1).build(HexicalMain.MOD_ID + ":mesh")

	fun register(event: RegisterEvent) {
		if (event.registryKey != Registries.ENTITY_TYPE) return
		event.register(Registries.ENTITY_TYPE, HexicalMain.id("animated_scroll")) { ANIMATED_SCROLL_ENTITY }
		event.register(Registries.ENTITY_TYPE, HexicalMain.id("magic_missile")) { MAGIC_MISSILE_ENTITY }
		event.register(Registries.ENTITY_TYPE, HexicalMain.id("spike")) { SPIKE_ENTITY }
		event.register(Registries.ENTITY_TYPE, HexicalMain.id("speck")) { SPECK_ENTITY }
		event.register(Registries.ENTITY_TYPE, HexicalMain.id("mesh")) { MESH_ENTITY }
	}
}
