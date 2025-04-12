package io.dogsbean.fblauncher.listener;

import io.dogsbean.fblauncher.config.Config;
import io.dogsbean.fblauncher.util.FireballUtil;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

@AllArgsConstructor
public class FireballListener implements Listener {

    private final Map<UUID, Long> fireballCooldowns = new ConcurrentHashMap<>();

    Config config;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || event.getItem().getType() != Material.FIREBALL || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        // Cooldown check.
        if (fireballCooldowns.containsKey(playerId)) {
            int cooldownMillis = (int) (config.getCooldownSeconds() * 1000);
            long lastUsed = fireballCooldowns.get(playerId);

            if (currentTime - lastUsed < cooldownMillis) {

                // Send message only if it is not empty.
                if (!config.getMessage().isEmpty()) {
                    double remainingCooldown = (cooldownMillis - (currentTime - lastUsed)) / 1000.0;
                    String formattedCooldown = String.format("%.1f", remainingCooldown);
                    player.sendMessage(config.getMessage().replace("$cooldown", formattedCooldown));
                }

                event.setCancelled(true);
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

        FireballUtil.launch(player);
        fireballCooldowns.put(playerId, currentTime);
        event.setCancelled(true);
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

            FireballUtil.explodeKnockback(event.getDamager().getLocation(), victim.getLocation(), victim, config);
            victim.damage(damage);

            event.setCancelled(true);
            victim.damage(0.0F);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            String typeName = block.getType().name().toLowerCase();
            return config.getExcludedFromExplodeBlocks().stream().anyMatch(value -> value.toLowerCase().contains(typeName));
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFallDamage(EntityDamageEvent event) {
        if (!config.isForceDisableFallDamage() || event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
    }
}
