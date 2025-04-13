package com.orcaseason.fblauncher.util;

import com.orcaseason.fblauncher.config.Config;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@UtilityClass
public class FireballUtil {

    public void launch(Player player, Config config) {
        Vector direction = player.getLocation().getDirection().multiply(config.getFireballSpeed());
        Fireball fireball = player.launchProjectile(LargeFireball.class, direction);
        fireball.setShooter(player);
    }

    public void explodeKnockback(Location location, Location victimLocation, Player victim, Config config) {
        Vector dirToExplosion = victimLocation.toVector().subtract(location.toVector()).normalize();
        double distanceFromExplosion = location.distance(victimLocation);

        double explosionStrength = distanceFromExplosion > config.getExplosionDistance()
                ? config.getFarExplosionStrength()
                : config.getExplosionStrength();
        double explosionY = distanceFromExplosion > config.getExplosionDistance()
                ? config.getFarExplosionY()
                : config.getExplosionY();

        dirToExplosion.multiply(explosionStrength).setY(explosionY);
        victim.setVelocity(dirToExplosion);
    }
}