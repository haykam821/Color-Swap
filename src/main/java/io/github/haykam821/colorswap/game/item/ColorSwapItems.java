package io.github.haykam821.colorswap.game.item;

import java.util.function.Function;

import io.github.haykam821.colorswap.Main;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ColorSwapItems {
	public static final Item PRISM = register("prism", PrismItem::new, new Item.Settings()
		.maxCount(1)
		.rarity(Rarity.RARE)
		.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true));

	public static void initialize() {
		return;
	}

	private static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
		Identifier id = Main.identifier(path);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

		Item item = factory.apply(settings.registryKey(key));

		return Registry.register(Registries.ITEM, Main.identifier(path), item);
	}
}
