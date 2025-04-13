# FireballLauncher Plugin
Customizable Minecraft plugin that allows players to launch fireballs using right-click, with configurable knockback, particle effects, cooldowns, and team protection.

![version](https://img.shields.io/badge/version-1.0-brightgreen)

## Features

- Launch fireballs by right-clicking with a Fireball item.
- Custom explosion knockback (strength, Y-axis boost, distance scaling).
- Cooldown system with customizable message.
- Team protection: prevent friendly fire between scoreboard teammates.
- Configurable fireball particles on hit.
- GUI-based settings menu for admins.
- Command support for in-game configuration changes.
- Option to disable fall damage caused by knockback.

## Configuration

When the plugin starts, it generates a default configuration file (`config.yml`). You can edit it manually or use in-game commands or GUI.

### Example Config Options:
```yaml
prefix:
  "§c§lFireball "

explosion:
  strength: 1.25
  y: 1.1
  distance: 2.0
  far-strength: 0.7
  far-y: 0.8

fireball:
  speed: 1.5
  particle: "FLAME"
  particle-count: 10

team-protection-enabled: true

# Players will not receive any damage from fall.
force-disable-entity-fall-damage: false

excluded-from-explode-blocks:
  - "ENDER_STONE"
  - "WOOD"

cooldown-seconds: 0.8
cooldown-message: "&cFireball cooldown: &e$cooldown seconds"
```

## Commands

| Command | Description |
|--------|-------------|
| `/fblauncher reload` | Reload the config file |
| `/fblauncher set <option> <value>` | Change a config option |
| `/fblauncher menu` | Open a GUI config menu (player only) |

> **Permission required:** `fblauncher.admin`

## Notes

- Fireballs are consumed on use.
- Cooldown prevents spamming.
- Friendly fire is blocked when players are in the same scoreboard team.
- Particle effects support all valid `org.bukkit.Effect` types.

## Support
For support or inquiries, open an ticket in my discord [Support Discord](https://discord.gg/xydjE7ym5W).

