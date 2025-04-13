package com.orcaseason.fblauncher.command;

import com.orcaseason.fblauncher.Main;
import com.orcaseason.fblauncher.config.Config;
import com.orcaseason.fblauncher.gui.FireballMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FireballCommand implements CommandExecutor {

    private final Config config;
    private final Main plugin;

    public FireballCommand(Config config, Main plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("fblauncher.admin")) {
            sender.sendMessage(config.getPrefix() + ChatColor.RED + "You don't have permission!");
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                config.load(plugin.getConfig());
                plugin.saveConfig();
                sender.sendMessage(config.getPrefix() + ChatColor.GREEN + "Configuration reloaded!");
                return true;

            case "set":
                if (args.length < 3) {
                    sender.sendMessage(config.getPrefix() + ChatColor.RED + "Usage: /fblauncher set <option> <value>");
                    return true;
                }
                return handleSetCommand(sender, args[1], args[2]);

            case "menu":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(config.getPrefix() + ChatColor.RED + "This command is for players only!");
                    return true;
                }
                new FireballMenu((Player) sender, config, plugin).open();
                return true;

            default:
                sendHelpMessage(sender);
                return true;
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "/fblauncher reload - Reload config");
        sender.sendMessage(ChatColor.YELLOW + "/fblauncher set <option> <value> - Set a config option");
        sender.sendMessage(ChatColor.YELLOW + "/fblauncher menu - Open config menu");
    }

    private boolean handleSetCommand(CommandSender sender, String option, String value) {
        FileConfiguration configFile = plugin.getConfig();
        try {
            switch (option.toLowerCase()) {
                case "explosiondistance":
                    double explosionDistance = Double.parseDouble(value);
                    if (explosionDistance <= 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be positive!");
                    configFile.set("explosion.distance", explosionDistance);
                    break;

                case "explosiony":
                    configFile.set("explosion.y", Double.parseDouble(value));
                    break;

                case "explosionstrength":
                    double explosionStrength = Double.parseDouble(value);
                    if (explosionStrength <= 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be positive!");
                    configFile.set("explosion.strength", explosionStrength);
                    break;

                case "farexplosiony":
                    configFile.set("explosion.far-y", Double.parseDouble(value));
                    break;

                case "farexplosionstrength":
                    double farExplosionStrength = Double.parseDouble(value);
                    if (farExplosionStrength <= 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be positive!");
                    configFile.set("explosion.far-strength", farExplosionStrength);
                    break;

                case "cooldownseconds":
                    double cooldownSeconds = Double.parseDouble(value);
                    if (cooldownSeconds < 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be non-negative!");
                    configFile.set("cooldown-seconds", cooldownSeconds);
                    break;

                case "message":
                    configFile.set("cooldown-message", value);
                    break;

                case "forcedisablefalldamage":
                    configFile.set("force-disable-entity-fall-damage", Boolean.parseBoolean(value));
                    break;

                case "fireballspeed":
                    double fireballSpeed = Double.parseDouble(value);
                    if (fireballSpeed <= 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be positive!");
                    configFile.set("fireball.speed", fireballSpeed);
                    break;

                case "fireballparticle":
                    configFile.set("fireball.particle", value.toUpperCase());
                    break;

                case "fireballparticlecount":
                    int particleCount = Integer.parseInt(value);
                    if (particleCount < 0) throw new IllegalArgumentException(config.getPrefix() + "Value must be non-negative!");
                    configFile.set("fireball.particle-count", particleCount);
                    break;

                default:
                    sender.sendMessage(config.getPrefix() + ChatColor.RED + "Unknown option: " + option);
                    return true;
            }
            plugin.saveConfig();
            config.load(configFile);
            sender.sendMessage(config.getPrefix() + ChatColor.GREEN + "Set " + option + " to " + value);
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage(config.getPrefix() + ChatColor.RED + "Invalid number format!");
            return true;
        } catch (IllegalArgumentException e) {
            sender.sendMessage(config.getPrefix() + ChatColor.RED + e.getMessage());
            return true;
        }
    }
}