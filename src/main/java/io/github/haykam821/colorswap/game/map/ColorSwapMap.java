package io.github.haykam821.colorswap.game.map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public final class ColorSwapMap {
	private final MapTemplate template;
	private final BlockBounds platform;

	private final Vec3d center;
	private final double spawnRadius;

	public ColorSwapMap(MapTemplate template, BlockBounds platform, double spawnRadiusPadding) {
		this.template = template;
		this.platform = platform;

		this.center = this.createCenterPos(0, 0);

		BlockPos size = this.platform.size();
		int min = Math.min(size.getX(), size.getZ());

		this.spawnRadius = min > spawnRadiusPadding ? min / 2d - spawnRadiusPadding : 0;
	}

	public BlockBounds getPlatform() {
		return this.platform;
	}

	// Spawn positions
	public Vec3d getGuideTextPos() {
		return this.createCenterPos(1.2, 0);
	}

	public Vec3d getWaitingSpawnPos() {
		return this.createCenterPos(0, 4);
	}

	public Vec3d getSpawnPos(double theta) {
		double x = this.center.getX() + Math.cos(theta) * spawnRadius;
		double z = this.center.getZ() + Math.sin(theta) * spawnRadius;

		return new Vec3d(x, this.center.getY(), z);
	}

	public Vec3d getSpectatorSpawnPos() {
		return this.createCenterPos(3, 0);
	}

	// Elimination detection
	public boolean isBelowPlatform(ServerPlayerEntity player) {
		return player.getY() < this.platform.min().getY();
	}

	public boolean isAbovePlatform(ServerPlayerEntity player, boolean lenient) {
		return player.getY() > this.platform.min().getY() + (lenient ? 5 : 2.5);
	}

	public ChunkGenerator createGenerator(MinecraftServer server) {
		return new TemplateChunkGenerator(server, this.template);
	}

	private Vec3d createCenterPos(double offsetY, double offsetZ) {
		Vec3d center = this.getPlatform().centerTop();

		double maxOffsetZ = this.platform.size().getZ() / 2 - 0.5;
		double clampedOffsetZ = MathHelper.clamp(offsetZ, -maxOffsetZ, maxOffsetZ);

		return new Vec3d(center.getX(), center.getY() + offsetY, center.getZ() - clampedOffsetZ);
	}
}
