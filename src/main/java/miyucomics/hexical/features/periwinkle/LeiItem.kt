package miyucomics.hexical.features.periwinkle

import at.petrak.hexcasting.common.lib.HexAttributes
import miyucomics.hexical.HexicalMain
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionHand

object LeiItem : ArmorItem(LeiArmorMaterial.INSTANCE, Type.HELMET, Properties()) {
	private val bakedAttributes: ItemAttributeModifiers = ItemAttributeModifiers.builder()
		.add(
			HexAttributes.GRID_ZOOM,
			AttributeModifier(HexicalMain.id("lei_grid_zoom"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
			EquipmentSlotGroup.HEAD
		)
		.add(
			HexAttributes.SCRY_SIGHT,
			AttributeModifier(HexicalMain.id("lei_scry_sight"), 1.0, AttributeModifier.Operation.ADD_VALUE),
			EquipmentSlotGroup.HEAD
		)
		.build()

	override fun interactLivingEntity(stack: ItemStack, player: Player, friend: LivingEntity, hand: InteractionHand): InteractionResult {
		if (friend is Player && friend.getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
			friend.setItemSlot(EquipmentSlot.HEAD, stack.copy())
			stack.shrink(1)
			return InteractionResult.SUCCESS
		}
		return InteractionResult.PASS
	}

	override fun getDefaultAttributeModifiers(): ItemAttributeModifiers = bakedAttributes
}
