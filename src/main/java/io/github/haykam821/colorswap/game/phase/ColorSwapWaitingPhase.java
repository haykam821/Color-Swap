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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
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
			game.listen(GamePlayerEvents.ACCEPT, waiting::onAcceptPlayer);
			game.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
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

	private JoinAcceptorResult onAcceptPlayer(JoinAcceptor acceptor) {
		return acceptor.teleport(this.world, this.map.getWaitingSpawnPos())
			.thenRunForEach(player -> player.changeGameMode(GameMode.ADVENTURE));
	}

	public GameResult requestStart() {
		ColorSwapActivePhase.open(this.gameSpace, this.world, this.map, this.config, this.guideText);
		return GameResult.ok();
	}

	public EventResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
		// Respawn player at the start
		this.spawn(player);
		return EventResult.DENY;
	}

	private void spawn(ServerPlayerEntity player) {
		Vec3d spawnPos = map.getWaitingSpawnPos();
		ColorSwapActivePhase.spawn(this.world, spawnPos, 0, player);
	}
}
