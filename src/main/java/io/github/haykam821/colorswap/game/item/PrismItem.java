package io.github.haykam821.colorswap.game.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.haykam821.colorswap.game.component.PrismComponent;
import io.github.haykam821.colorswap.game.prism.Prism;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public class PrismItem extends Item implements PolymerItem {
	public PrismItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public Item getPolymerItem(ItemStack stack, PacketContext context) {
		Prism prism = PrismComponent.get(stack);
		return prism == null ? Items.NETHER_STAR : prism.getDisplayItem();
	}

	@Override
	public Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
		return null;
	}
}
