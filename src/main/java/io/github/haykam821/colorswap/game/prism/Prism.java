package io.github.haykam821.colorswap.game.prism;

import io.github.haykam821.colorswap.game.phase.ColorSwapActivePhase;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class Prism {
	private Text name;

	public abstract boolean activate(ColorSwapActivePhase phase, ServerPlayerEntity player);

	public abstract Item getDisplayItem();

	public Text getName() {
		if (this.name == null) {
			Identifier id = Prisms.REGISTRY.getIdentifier(this);
			return Text.translatable(Util.createTranslationKey("prism", id));
		}

		return this.name;
	}
}
