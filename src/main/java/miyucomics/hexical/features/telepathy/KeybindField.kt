package miyucomics.hexical.features.telepathy

import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.world.entity.player.Player

class KeybindField : PlayerField {
	var active: HashMap<String, Boolean> = HashMap()
	var duration: HashMap<String, Int> = HashMap()
	var scroll: Int = 0
}

fun Player.serverKeybindActive() = this.getHexicalPlayerManager().get(KeybindField::class).active
fun Player.serverKeybindDuration() = this.getHexicalPlayerManager().get(KeybindField::class).duration
var Player.serverScroll: Int
	get() = this.getHexicalPlayerManager().get(KeybindField::class).scroll
	set(scroll) { this.getHexicalPlayerManager().get(KeybindField::class).scroll = scroll }