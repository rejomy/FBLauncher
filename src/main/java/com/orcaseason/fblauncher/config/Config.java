package com.orcaseason.fblauncher.config;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Config {

    private String prefix;

    private double explosionDistance;
    private double explosionY;
    private double explosionStrength;
    private double farExplosionY;
    private double farExplosionStrength;
    private double cooldownSeconds;
    private String message;
    private boolean forceDisableFallDamage;
    private Set<String> excludedFromExplodeBlocks;

    private double fireballSpeed;
    private String fireballParticle;
    private int fireballParticleCount;
    private boolean preventAirSpam;
    private double airSpamLimit;

    private boolean teamProtectionEnabled;

    public void load(FileConfiguration config) {
        prefix = config.getString("prefix");

        explosionDistance = config.getDouble("explosion.distance", 10.0);
        explosionY = config.getDouble("explosion.y", 0.5);
        explosionStrength = config.getDouble("explosion.strength", 2.0);
        farExplosionY = config.getDouble("explosion.far-y", 0.3);
        farExplosionStrength = config.getDouble("explosion.far-strength", 1.0);
        cooldownSeconds = config.getDouble("cooldown-seconds", 1.0);
        message = ChatColor.translateAlternateColorCodes('&', config.getString("cooldown-message", "&cPlease wait $cooldown seconds!"));
        forceDisableFallDamage = config.getBoolean("force-disable-entity-fall-damage", false);
        excludedFromExplodeBlocks = new HashSet<>(config.getStringList("excluded-from-explode-blocks"));

        fireballSpeed = config.getDouble("fireball.speed", 1.0);
        fireballParticle = config.getString("fireball.particle", "SMOKE");
        fireballParticleCount = config.getInt("fireball.particle-count", 10);
        preventAirSpam = config.getBoolean("fireball.prevent-air-spam", false);
        airSpamLimit = config.getDouble("fireball.air-spam-limit", 0.1);

        teamProtectionEnabled = config.getBoolean("team-protection-enabled", true);
    }
}