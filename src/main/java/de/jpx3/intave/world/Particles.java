package de.jpx3.intave.world;

import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;
import org.bukkit.Particle;

public class Particles {
	public static void spawnVillagerHappyParticleAt(User user, Position position) {
		user.player().getWorld().spawnParticle(
			(Particle) villagerHappyParticle(),
			position.toLocation(user.player().getWorld()), 1
		);
	}

	private static Object villagerHappyParticleCache;

	private static Object villagerHappyParticle() {
		if (villagerHappyParticleCache == null) {
			try {
				try {
					villagerHappyParticleCache = Particle.VILLAGER_HAPPY;
				} catch (NoSuchFieldError e) {
					villagerHappyParticleCache = Particle.class.getField("HAPPY_VILLAGER").get(null);
				}
			} catch (IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
		return villagerHappyParticleCache;
	}
}
