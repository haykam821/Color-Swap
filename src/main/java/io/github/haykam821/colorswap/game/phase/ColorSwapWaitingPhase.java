package io.github.haykam821.colorswap.game.phase;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import io.github.haykam821.colorswap.game.ColorSwapConfig;
import io.github.haykam821.colorswap.game.map.ColorSwapGuideText;
import io.github.haykam821.colorswap.game.map.ColorSwapMap;
import io.github.haykam821.colorswap.game.map.ColorSwapMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class ColorSwapWaitingPhase {
	private final GameSpace gameSpace;
	private final ServerWorld world;
	private final ColorSwapMap map;
	private final ColorSwapConfig config;

	private HolderAttachment guideText;

	public ColorSwapWaitingPhase(GameSpace gameSpace, ServerWorld world, ColorSwapMap map, ColorSwapConfig config) {
		this.gameSpace = gameSpace;
		this.world = world;
		this.map = map;
		this.config = config;
	}

	public static GameOpenProcedure open(GameOpenContext<ColorSwapConfig> context) {
		ColorSwapConfig config = context.game().config();
		ColorSwapMapBuilder mapBuilder = new ColorSwapMapBuilder(config);

		ColorSwapMap map = mapBuilder.create(Random.createLocal());
		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.createGenerator(context.server()));

		return context.openWithWorld(worldConfig, (game, world) -> {
			ColorSwapWaitingPhase waiting = new ColorSwapWaitingPhase(game.getGameSpace(), world, map, config);

			GameWaitingLobby.addTo(game, config.getPlayerConfig());
			ColorSwapActivePhase.setRules(game);
			game.deny(GameRuleType.PVP);

			// Listeners
			game.listen(GameActivityEvents.ENABLE, waiting::enable);
			game.listen(GameActivityEvents.TICK, waiting::tick);
			game.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
			game.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
			game.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
		});
	}

	private void enable() {
		// Spawn guide text
		Vec3d guideTextPos = this.map.getGuideTextPos();

		if (guideTextPos != null) {
			Random random = this.world.getRandom();

			boolean knockback = this.config.getNoKnockbackRounds() >= 0;
			boolean prisms = this.config.getPrismConfig().isPresent();

			ElementHolder holder = ColorSwapGuideText.createElementHolder(random, knockback, prisms);
			this.guideText = ChunkAttachment.of(holder, world, guideTextPos);
		}
	}

	private void tick() {
		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (this.map.isBelowPlatform(player)) {
				this.spawn(player);
			}
		}
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.world, this.map.getWaitingSpawnPos())
			.and(() -> offer.player().changeGameMode(GameMode.ADVENTURE));
	}

	public GameResult requestStart() {
		ColorSwapActivePhase.open(this.gameSpace, this.world, this.map, this.config, this.guideText);
		return GameResult.ok();
	}

	public ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		// Respawn player at the start
		this.spawn(player);
		return ActionResult.FAIL;
	}

	private void spawn(ServerPlayerEntity player) {
		Vec3d spawnPos = map.getWaitingSpawnPos();
		ColorSwapActivePhase.spawn(this.world, spawnPos, 0, player);
	}
}
