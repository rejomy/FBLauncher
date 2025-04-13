package com.orcaseason.fblauncher.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

@Getter
public class Config {

    double explosionDistance;
    double explosionY;
    double explosionStrength;
    double farExplosionY;
    double farExplosionStrength;
    double cooldownSeconds;
    String message;
    boolean forceDisableFallDamage;
    List<String> excludedFromExplodeBlocks;

    double fireballSpeed;
    String fireballParticle;
    int fireballParticleCount;

    public void load(FileConfiguration config) {
        farExplosionStrength = config.getDouble("explosion.far-strength");
        farExplosionY = config.getDouble("explosion.far-y");
        explosionDistance = config.getDouble("explosion.distance");
        explosionY = config.getDouble("explosion.y");
        explosionStrength = config.getDouble("explosion.strength");
        cooldownSeconds = config.getDouble("cooldown-seconds");
        message = ChatColor.translateAlternateColorCodes('&', config.getString("cooldown-message"));
        forceDisableFallDamage = config.getBoolean("force-disable-entity-fall-damage");
        excludedFromExplodeBlocks = config.getStringList("excluded-from-explode-blocks");

        fireballSpeed = config.getDouble("fireball.speed");
        fireballParticle = config.getString("fireball.particle");
        fireballParticleCount = config.getInt("fireball.particle-count");
    }
}
