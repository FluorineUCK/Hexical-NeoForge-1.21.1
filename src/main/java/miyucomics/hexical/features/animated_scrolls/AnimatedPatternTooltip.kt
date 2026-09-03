package miyucomics.hexical.features.animated_scrolls

import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.world.inventory.tooltip.TooltipComponent

data class AnimatedPatternTooltip(val color: Int, val pattern: List<HexPattern>, val state: Int, val glowing: Boolean) : TooltipComponent
