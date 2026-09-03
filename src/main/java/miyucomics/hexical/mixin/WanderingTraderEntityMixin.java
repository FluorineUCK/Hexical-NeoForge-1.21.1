package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {
	public WanderingTraderEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
		super(entityType, world);
	}

	@SuppressWarnings("DataFlowIssue")
	@Inject(method = "updateTrades", at = @At("RETURN"))
	public void addNewTrades(CallbackInfo info) {
		MerchantOffers tradeOfferList = getOffers();
		if (tradeOfferList == null)
			return;
		if (random.nextFloat() < 0.4) {
			ItemStack trade = new ItemStack(HexicalItems.HAND_LAMP_ITEM);
			IXplatAbstractions.INSTANCE.findHexHolder(trade).writeHex(new ArrayList<>(), null, MediaConstants.DUST_UNIT * 320);
			IXplatAbstractions.INSTANCE.findMediaHolder(trade).withdrawMedia((int) (HexicalMain.RANDOM.nextFloat() * 160f) * MediaConstants.DUST_UNIT, false);
			tradeOfferList.add(new MerchantOffer(new ItemCost(Items.EMERALD, 32), trade, 1, 1, 1));
		}
		if (random.nextFloat() < 0.5) {
			ItemStack trade = HexicalItems.randomPlush();
			tradeOfferList.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), trade, 1, 1, 1));
		}
	}
}
