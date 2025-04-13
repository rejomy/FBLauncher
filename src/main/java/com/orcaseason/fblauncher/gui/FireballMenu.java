package com.orcaseason.fblauncher.gui;

import com.orcaseason.fblauncher.Main;
import com.orcaseason.fblauncher.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireballMenu implements Listener {

    private final Player player;
    private final Config config;
    private final Main plugin;
    private final Inventory inventory;
    private final Map<UUID, String> pendingInput = new HashMap<>();

    public FireballMenu(Player player, Config config, Main plugin) {
        this.player = player;
        this.config = config;
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Fireball Launcher Config");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initializeItems();
    }

    private void initializeItems() {
        inventory.setItem(9, createItem("Explosion Distance", config.getExplosionDistance()));
        inventory.setItem(10, createItem("Explosion Y", config.getExplosionY()));
        inventory.setItem(11, createItem("Explosion Strength", config.getExplosionStrength()));
        inventory.setItem(12, createItem("Far Explosion Y", config.getFarExplosionY()));
        inventory.setItem(13, createItem("Far Explosion Strength", config.getFarExplosionStrength()));
        inventory.setItem(14, createItem("Cooldown Seconds", config.getCooldownSeconds()));
        inventory.setItem(15, createItem("Cooldown Message", config.getMessage()));
        inventory.setItem(16, createItem("Disable Fall Damage", config.isForceDisableFallDamage()));
        inventory.setItem(17, createItem("Fireball Speed", config.getFireballSpeed()));
    }

    private ItemStack createItem(String name, Object value) {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + name);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Value: " + value, ChatColor.GREEN + "Click to edit"));
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        initializeItems();
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory) || !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        event.setCancelled(true);

        int slot = event.getSlot();
        String option = getOptionFromSlot(slot);
        if (option == null) return;

        player.closeInventory();
        player.sendMessage(ChatColor.YELLOW + "Enter new value for " + option + " (or 'cancel' to abort):");
        pendingInput.put(player.getUniqueId(), option);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        UUID uuid = sender.getUniqueId();
        if (!pendingInput.containsKey(uuid)) return;

        String option = pendingInput.remove(uuid);
        String value = event.getMessage().trim();
        event.setCancelled(true);

        if (value.equalsIgnoreCase("cancel")) {
            sender.sendMessage(ChatColor.RED + "Cancelled!");
            open();
            return;
        }

        try {
            FileConfiguration configFile = plugin.getConfig();
            switch (option.toLowerCase()) {
                case "explosion distance":
                    double explosionDistance = Double.parseDouble(value);
                    if (explosionDistance <= 0) throw new IllegalArgumentException("Must be positive!");
                    configFile.set("explosion.distance", explosionDistance);
                    break;
                case "explosion y":
                    configFile.set("explosion.y", Double.parseDouble(value));
                    break;
                case "explosion strength":
                    double explosionStrength = Double.parseDouble(value);
                    if (explosionStrength <= 0) throw new IllegalArgumentException("Must be positive!");
                    configFile.set("explosion.strength", explosionStrength);
                    break;
                case "far explosion y":
                    configFile.set("explosion.far-y", Double.parseDouble(value));
                    break;
                case "far explosion strength":
                    double farExplosionStrength = Double.parseDouble(value);
                    if (farExplosionStrength <= 0) throw new IllegalArgumentException("Must be positive!");
                    configFile.set("explosion.far-strength", farExplosionStrength);
                    break;
                case "cooldown seconds":
                    double cooldownSeconds = Double.parseDouble(value);
                    if (cooldownSeconds < 0) throw new IllegalArgumentException("Must be non-negative!");
                    configFile.set("cooldown-seconds", cooldownSeconds);
                    break;
                case "cooldown message":
                    configFile.set("cooldown-message", value);
                    break;
                case "disable fall damage":
                    configFile.set("force-disable-entity-fall-damage", Boolean.parseBoolean(value));
                    break;
                case "fireball speed":
                    double fireballSpeed = Double.parseDouble(value);
                    if (fireballSpeed <= 0) throw new IllegalArgumentException("Must be positive!");
                    configFile.set("fireball.speed", fireballSpeed);
                    break;
            }
            plugin.saveConfig();
            config.load(configFile);
            sender.sendMessage(ChatColor.GREEN + "Set " + option + " to " + value);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number format!");
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        Bukkit.getScheduler().runTask(plugin, this::open);
    }

    private String getOptionFromSlot(int slot) {
        switch (slot) {
            case 9: return "Explosion Distance";
            case 10: return "Explosion Y";
            case 11: return "Explosion Strength";
            case 12: return "Far Explosion Y";
            case 13: return "Far Explosion Strength";
            case 14: return "Cooldown Seconds";
            case 15: return "Cooldown Message";
            case 16: return "Disable Fall Damage";
            case 17: return "Fireball Speed";
            default: return null;
        }
    }
}