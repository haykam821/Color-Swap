package io.github.haykam821.colorswap.game.map;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import io.github.haykam821.colorswap.game.RainbowTextColors;
import net.minecraft.entity.decoration.DisplayEntity.BillboardMode;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

public final class ColorSwapGuideText {
	private static final Text TITLE = Text.translatable("gameType.colorswap.color_swap").formatted(Formatting.BOLD);

	private static final Text STAND_ON_MATCHING_BLOCKS_LINE = Text.translatable("text.colorswap.guide.stand_on_matching_blocks");
	private static final Text KNOCK_OTHERS_OFF_LINE = Text.translatable("text.colorswap.guide.knock_others_off");
	private static final Text GRAB_PRISMS_LINE = Text.translatable("text.colorswap.guide.grab_prisms");
	private static final Text LAST_PLAYER_STANDING_LINE = Text.translatable("text.colorswap.guide.last_player_standing");

	private ColorSwapGuideText() {
		return;
	}

	public static ElementHolder createElementHolder(Random random, boolean knockback, boolean prisms) {
		TextDisplayElement element = new TextDisplayElement(createText(random, knockback, prisms));

		element.setBillboardMode(BillboardMode.CENTER);
		element.setLineWidth(350);
		element.setInvisible(true);

		ElementHolder holder = new ElementHolder();
		holder.addElement(element);

		return holder;
	}

	private static Text createText(Random random, boolean knockback, boolean prisms) {
		MutableText text = Text.empty()
			.append(TITLE)
			.append(ScreenTexts.LINE_BREAK)
			.append(STAND_ON_MATCHING_BLOCKS_LINE);

		if (knockback) {
			text
				.append(ScreenTexts.LINE_BREAK)
				.append(KNOCK_OTHERS_OFF_LINE);
		}

		if (prisms) {
			text
				.append(ScreenTexts.LINE_BREAK)
				.append(GRAB_PRISMS_LINE);
		}

		Style style = RainbowTextColors.getRandomStyle(random);

		return text
			.append(ScreenTexts.LINE_BREAK)
			.append(LAST_PLAYER_STANDING_LINE)
			.setStyle(style);
	}
}
