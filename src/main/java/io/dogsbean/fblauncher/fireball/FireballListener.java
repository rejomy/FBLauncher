package io.dogsbean.fblauncher.fireball;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FireballListener implements Listener {
    private final List<Material> allowBreak = Arrays.asList(Material.WOOD, Material.WOOL);
    private final Map<UUID, Long> fireballCooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME_MS = 500;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.FIREBALL || !event.getAction().name().contains("RIGHT_")) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        long currentTime = System.currentTimeMillis();
        if (fireballCooldowns.containsKey(playerId)) {
            long lastUsed = fireballCooldowns.get(playerId);
            if (currentTime - lastUsed < COOLDOWN_TIME_MS) {
                double remainingCooldown = (COOLDOWN_TIME_MS - (currentTime - lastUsed)) / 1000.0;
                String formattedCooldown = String.format("%.1f", remainingCooldown);
                player.sendMessage(ChatColor.RED + "Fireball cooldown: " + ChatColor.YELLOW + formattedCooldown + " seconds.");
                return;
            }
        }

        ItemStack item = event.getItem();
        if (item.getAmount() <= 1) {
            player.getInventory().setItemInHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
        }
        player.updateInventory();

        Vector direction = player.getLocation().getDirection();
        Fireball fireball = (Fireball) player.launchProjectile(Fireball.class, direction);
        fireball.setShooter(player);

        fireballCooldowns.put(playerId, currentTime);
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Material> allowedBlocks = Arrays.asList(Material.ENDER_STONE, Material.WOOD);
        event.blockList().removeIf(block -> !allowedBlocks.contains(block.getType()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        for (Fireball fireball : player.getWorld().getEntitiesByClass(Fireball.class)) {
            if (fireball.getShooter() == player) {
                fireball.remove();
            }
        }

        fireballCooldowns.remove(playerId);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        fireballCooldowns.remove(playerId);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        Player victim = (Player) event.getEntity();

        if (event.getDamager() instanceof Fireball) {
            double damage = 0;

            if (event.getDamager().getTicksLived() > 20) {
                damage = event.getFinalDamage() / 4;
            }

            explodeKnockback(event.getDamager().getLocation(), victim.getLocation(), victim);
            victim.damage(damage);

            event.setCancelled(true);
            victim.damage(0.0F);
        }
    }

    private void explodeKnockback(Location tntLocaton, Location victimLocation, Player victim) {
        Vector dirToExplosion = tntLocaton.toVector().subtract(victimLocation.toVector());
        double distanceFromExplosion = tntLocaton.distance(victimLocation);

        dirToExplosion.multiply(-1);
        dirToExplosion.setY(0).normalize();

        double explosionStrength = 1.25;
        double explosionY = 1.1;
        double explosionDistance = 2.0;

        if(distanceFromExplosion > explosionDistance) {
            explosionStrength = 0.7;
            explosionY = 0.8;
        }

        dirToExplosion.multiply(explosionStrength);
        dirToExplosion.setY(explosionY);
        victim.setVelocity(dirToExplosion);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (allowBreak.stream().noneMatch(it -> event.getBlock().getType().equals(it))) {
            event.setCancelled(true);
        }
    }
}
