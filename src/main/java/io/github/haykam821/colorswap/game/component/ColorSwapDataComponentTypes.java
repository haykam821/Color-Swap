package io.github.haykam821.colorswap.game.component;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.haykam821.colorswap.Main;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ColorSwapDataComponentTypes {
	private static final Identifier PRISM_ID = Main.identifier("prism");

	public static final ComponentType<PrismComponent> PRISM = ComponentType.<PrismComponent>builder()
		.codec(PrismComponent.CODEC)
		.cache()
		.build();

	public static void register() {
		Registry.register(Registries.DATA_COMPONENT_TYPE, PRISM_ID, PRISM);
		PolymerComponent.registerDataComponent(PRISM);
	}
}
