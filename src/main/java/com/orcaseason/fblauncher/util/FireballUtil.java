package com.orcaseason.fblauncher.util;

import com.orcaseason.fblauncher.config.Config;
import lombok.experimental.UtilityClass;
import org.bukkit.Effect;
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
        Vector dirToExplosion = location.toVector().subtract(victimLocation.toVector());
        double distanceFromExplosion = location.distance(victimLocation);

        dirToExplosion.multiply(-1);
        dirToExplosion.setY(0).normalize();

        double explosionStrength = config.getExplosionStrength();
        double explosionY = config.getExplosionY();
        double explosionDistance = config.getExplosionDistance();

        if (distanceFromExplosion > explosionDistance) {
            explosionStrength = config.getFarExplosionStrength();
            explosionY = config.getFarExplosionY();
        }

        dirToExplosion.multiply(explosionStrength);
        dirToExplosion.setY(explosionY);
        victim.setVelocity(dirToExplosion);
    }
}
