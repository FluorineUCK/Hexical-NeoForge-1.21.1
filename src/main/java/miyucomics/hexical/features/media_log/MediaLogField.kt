package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.putList
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.hexcompat.deserializePattern
import miyucomics.hexical.hexcompat.serializePattern
import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexical.network.MediaLogPayload
import miyucomics.hexpose.utils.RingBuffer
import net.minecraft.world.entity.player.Player
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import net.minecraft.core.HolderLookup
import net.neoforged.neoforge.network.PacketDistributor

class MediaLogField : PlayerField {
	var patterns: RingBuffer<HexPattern> = RingBuffer(32)
	var stack: RingBuffer<Component> = RingBuffer(8)
	var mishap: Component = Component.empty()
	var active = true

	fun saveMishap(text: Component) {
		mishap = text
	}

	fun pushPattern(pattern: HexPattern) {
		patterns.add(pattern)
	}

	fun saveStack(iotas: List<Iota>) {
		stack.clear()
		iotas.forEach { iota -> stack.add(iota.display()) }
	}

	override fun readNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		if (!compound.contains("media_log"))
			return
		fromNbt(compound.getCompound("media_log"), provider)
	}

	override fun writeNbt(compound: CompoundTag, provider: HolderLookup.Provider) {
		compound.putCompound("media_log", toNbt(provider))
	}

	fun fromNbt(log: CompoundTag, provider: HolderLookup.Provider) {
		log.getList("patterns", net.minecraft.nbt.Tag.TAG_COMPOUND.toInt()).forEach { pattern -> deserializePattern(pattern)?.let(patterns::add) }
		log.getList("stack", net.minecraft.nbt.Tag.TAG_STRING.toInt()).forEach { iota -> Component.Serializer.fromJson((iota as StringTag).getAsString(), provider)?.let { stack.add(it) } }
		this.mishap = Component.Serializer.fromJson(log.getString("mishap"), provider) ?: Component.empty()
	}

	fun toNbt(provider: HolderLookup.Provider): CompoundTag {
		return CompoundTag().also { compound ->
			compound.putList("patterns", ListTag().also { patterns.buffer().forEach { pattern -> it.add(serializePattern(pattern)) } })
			compound.putList("stack", ListTag().also { stack.buffer().forEach { iota -> it.add(StringTag.valueOf(Component.Serializer.toJson(iota, provider))) } })
			compound.putString("mishap", Component.Serializer.toJson(mishap, provider))
		}
	}

	companion object {
		@JvmStatic
		fun isEnvCompatible(env: CastingEnvironment) = env is StaffCastEnv || env is PackagedItemCastEnv
	}
}

fun Player.getMediaLog() = this.getHexicalPlayerManager().get(MediaLogField::class)
fun ServerPlayer.syncMediaLog() {
	PacketDistributor.sendToPlayer(this, MediaLogPayload(this.getMediaLog().toNbt(this.registryAccess())))
}
