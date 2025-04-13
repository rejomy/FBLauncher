package com.orcaseason.fblauncher.listener;

import com.orcaseason.fblauncher.config.Config;
import com.orcaseason.fblauncher.util.FireballUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class FireballListener implements Listener {

    private final Map<UUID, Long> fireballCooldowns = new ConcurrentHashMap<>();
    private final Config config;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.FIREBALL ||
                (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        int cooldownMillis = (int) (config.getCooldownSeconds() * 1000);
        if (fireballCooldowns.getOrDefault(playerId, 0L) + cooldownMillis > currentTime) {
            if (!config.getMessage().isEmpty()) {
                double remainingCooldown = (cooldownMillis - (currentTime - fireballCooldowns.get(playerId))) / 1000.0;
                player.sendMessage(config.getMessage().replace("$cooldown", String.format("%.1f", remainingCooldown)));
            }
            event.setCancelled(true);
            return;
        }

        if (item.getAmount() == 1) {
            player.getInventory().setItemInHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }

        FireballUtil.launch(player, config);
        fireballCooldowns.put(playerId, currentTime);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        cleanupPlayer(event.getEntity());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cleanupPlayer(event.getPlayer());
    }

    private void cleanupPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        player.getWorld().getEntitiesByClass(Fireball.class).stream()
                .filter(fireball -> player.equals(fireball.getShooter()))
                .forEach(Entity::remove);
        fireballCooldowns.remove(playerId);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Fireball)) {
            return;
        }

        ProjectileSource source = ((Fireball) event.getDamager()).getShooter();
        if (!(source instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player shooter = (Player) source;

        if (config.isTeamProtectionEnabled() &&
                victim.getScoreboard().getEntryTeam(victim.getName()) != null &&
                victim.getScoreboard().getEntryTeam(victim.getName())
                        .equals(shooter.getScoreboard().getEntryTeam(shooter.getName()))) {
            shooter.sendMessage(config.getPrefix() + ChatColor.RED + "You cannot attack your teammates!");
            event.setCancelled(true);
            return;
        }

        double damage = event.getDamager().getTicksLived() > 20 ? event.getFinalDamage() / 4 : 0;
        FireballUtil.explodeKnockback(event.getDamager().getLocation(), victim.getLocation(), victim, config);
        victim.damage(damage);
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block ->
                config.getExcludedFromExplodeBlocks().stream()
                        .anyMatch(value -> block.getType().name().toLowerCase().contains(value.toLowerCase()))
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (config.isForceDisableFallDamage() &&
                event.getEntityType() == EntityType.PLAYER &&
                event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onFireballHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.FIREBALL) return;

        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player)) return;

        Player shooter = (Player) projectile.getShooter();
        Location loc = projectile.getLocation();

        String particleName = config.getFireballParticle();
        int particleCount = config.getFireballParticleCount();

        if (particleName == null || particleName.isEmpty() || particleName.equalsIgnoreCase("NONE")) {
            return;
        }

        try {
            Effect effect = Effect.valueOf(particleName.toUpperCase());
            for (Player nearby : loc.getWorld().getPlayers()) {
                if (nearby.getLocation().distanceSquared(loc) < 32 * 32) {
                    for (int i = 0; i < particleCount; i++) {
                        nearby.getWorld().playEffect(loc, effect, 0);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            shooter.getServer().getLogger().warning("Invalid particle name: " + particleName);
        }
    }
}