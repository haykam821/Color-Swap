package io.github.haykam821.colorswap.game;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public final class RainbowTextColors {
	private static final List<Style> STYLES = createStyles(new int[] {
		Formatting.RED.getColorValue(),
		Formatting.GOLD.getColorValue(),
		Formatting.YELLOW.getColorValue(),
		Formatting.GREEN.getColorValue(),
		Formatting.BLUE.getColorValue(),
		Formatting.LIGHT_PURPLE.getColorValue(),
	});

	private RainbowTextColors() {
		return;
	}

	public static Style getInitialStyle() {
		return STYLES.get(0);
	}

	public static Style getRandomStyle(Random random) {
		return Util.getRandom(STYLES, random);
	}

	public static Style getNextStyle(Style style) {
		return STYLES.get((STYLES.indexOf(style) + 1) % STYLES.size());
	}

	private static List<Style> createStyles(int[] colors) {
		ImmutableList.Builder<Style> styles = ImmutableList.builderWithExpectedSize(colors.length);

		for (int color : colors) {
			styles.add(Style.EMPTY.withColor(color));
		}

		return styles.build();
	}
}
