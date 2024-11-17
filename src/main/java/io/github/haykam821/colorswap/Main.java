package io.github.haykam821.colorswap;

import io.github.haykam821.colorswap.game.ColorSwapConfig;
import io.github.haykam821.colorswap.game.component.ColorSwapDataComponentTypes;
import io.github.haykam821.colorswap.game.item.ColorSwapItems;
import io.github.haykam821.colorswap.game.phase.ColorSwapWaitingPhase;
import io.github.haykam821.colorswap.game.prism.Prisms;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.GameType;

public class Main implements ModInitializer {
	private static final String MOD_ID = "colorswap";

	private static final Identifier COLOR_SWAP_ID = Main.identifier("color_swap");
	public static final GameType<ColorSwapConfig> COLOR_SWAP_TYPE = GameType.register(COLOR_SWAP_ID, ColorSwapConfig.CODEC, ColorSwapWaitingPhase::open);

	private static final Identifier PLATFORM_BLOCKS_ID = Main.identifier("platform_blocks");
	public static final TagKey<Block> PLATFORM_BLOCKS = TagKey.of(RegistryKeys.BLOCK, PLATFORM_BLOCKS_ID);

	@Override
	public void onInitialize() {
		Prisms.register();

		ColorSwapDataComponentTypes.register();
		ColorSwapItems.initialize();
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
