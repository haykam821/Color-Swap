package io.github.haykam821.colorswap.game.prism;

import java.util.Optional;

import io.github.haykam821.colorswap.game.phase.ColorSwapActivePhase;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class LeapPrism extends Prism {
	private static final RegistryEntry<SoundEvent> INTENTIONALLY_EMPTY = RegistryEntry.of(SoundEvents.INTENTIONALLY_EMPTY);

	private static final double LEAP_MULTIPLIER = 1.2;

	private static final double LEAP_MIN_Y = 0.15;
	private static final double STEALTHY_LEAP_MIN_Y = 0;

	@Override
	public boolean activate(ColorSwapActivePhase phase, ServerPlayerEntity player) {
		Vec3d velocity = LeapPrism.getLeapVelocity(player);
		Packet<?> packet = new ExplosionS2CPacket(Vec3d.ZERO, Optional.of(velocity), ParticleTypes.EXPLOSION, INTENTIONALLY_EMPTY);

		player.networkHandler.sendPacket(packet);
		phase.getWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.PLAYERS, 0.3f, 1.1f);

		return true;
	}

	@Override
	public Item getDisplayItem() {
		return Items.FEATHER;
	}

	public static Vec3d getLeapVelocity(ServerPlayerEntity player) {
		Vec3d facing = Vec3d
			.fromPolar(player.getPitch(), player.getYaw())
			.multiply(LEAP_MULTIPLIER);

		double y = Math.max(LeapPrism.getLeapMinY(player), facing.getY());
		return new Vec3d(facing.getX(), y, facing.getZ());
	}

	private static double getLeapMinY(ServerPlayerEntity player) {
		if (player.isSneaking()) {
			return STEALTHY_LEAP_MIN_Y;
		} else {
			return LEAP_MIN_Y;
		}
	}
}
