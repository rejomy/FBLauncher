![FireballLauncher in Action](https://cdn.discordapp.com/attachments/1360630776059658310/1361010376635977819/fb.png?ex=67fd3330&is=67fbe1b0&hm=a3d2a4ec607c469e2a5a6c788c91e3bf66e8d14a980581570eefbfb58e5690b1&)
# FireballLauncher Plugin

Customizable Minecraft plugin that allows players to launch fireballs by right-clicking a Fireball item, featuring configurable knockback, particle effects, cooldowns, team protection, and more.

![version](https://img.shields.io/badge/version-1.0-brightgreen)

## Features

- **Fireball Launching**: Right-click with a Fireball item to launch a customizable LargeFireball.
- **Configurable Knockback**: Adjust explosion strength, Y-axis boost, and distance-based scaling for near and far explosions.
- **Cooldown System**: Prevent spamming with a customizable cooldown and message.
- **Team Protection**: Optionally prevent friendly fire between players on the same scoreboard team.
- **Particle Effects**: Customize particle effects on fireball impact with adjustable particle count.
- **GUI Configuration**: Admins can modify settings via an in-game GUI menu.
- **Command Configuration**: Change settings using commands for quick adjustments.
- **Fall Damage Control**: Option to disable fall damage caused by fireball knockback.
- **Block Protection**: Prevent specific blocks (e.g., end stone, wood) from being destroyed by explosions.
- **Air Spam Prevention**: Optionally restrict fireball use while airborne to prevent spamming.
- **Customizable Fireball Speed**: Adjust the speed of launched fireballs.
- **Particle Customization**: Supports various `org.bukkit.Effect` types for impact visuals.

## Installation
1. Download the plugin JAR file.
2. Place it in your server's `plugins` folder.
3. Restart the server to generate the default `config.yml`.
4. Configure settings via `config.yml`, commands, or the GUI menu.

## Configuration
The plugin generates a `config.yml` file on first run. You can edit it manually, use the `/fblauncher set` command, or adjust settings through the GUI with `/fblauncher menu`.

### Example `config.yml`
```yaml
prefix: "§c§lFireball §r"

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
  prevent-air-spam: false
  air-spam-limit: 1.0

team-protection-enabled: true

force-disable-entity-fall-damage: false

excluded-from-explode-blocks:
  - "ENDER_STONE"
  - "WOOD"

cooldown-seconds: 0.8
cooldown-message: "&cFireball cooldown: &e$cooldown seconds"
```

### Configuration Options
| Option | Description | Default |
|--------|-------------|---------|
| `prefix` | Prefix for plugin messages | `§c§lFireball §r` |
| `explosion.strength` | Knockback strength for close-range hits | `1.25` |
| `explosion.y` | Y-axis boost for close-range knockback | `1.1` |
| `explosion.distance` | Max distance for close-range knockback | `2.0` |
| `explosion.far-strength` | Knockback strength for far-range hits | `0.7` |
| `explosion.far-y` | Y-axis boost for far-range knockback | `0.8` |
| `fireball.speed` | Speed multiplier for launched fireballs | `1.5` |
| `fireball.particle` | Particle effect on fireball impact (e.g., `FLAME`, `SMOKE`) | `FLAME` |
| `fireball.particle-count` | Number of particles spawned on impact | `10` |
| `fireball.prevent-air-spam` | Prevent fireball use while airborne | `false` |
| `fireball.air-spam-limit` | Minimum height above ground to trigger air spam check | `1.0` |
| `team-protection-enabled` | Block friendly fire between teammates | `true` |
| `force-disable-entity-fall-damage` | Prevent fall damage from knockback | `false` |
| `excluded-from-explode-blocks` | Blocks immune to explosion damage | `["ENDER_STONE", "WOOD"]` |
| `cooldown-seconds` | Cooldown between fireball uses | `0.8` |
| `cooldown-message` | Message shown during cooldown (`$cooldown` for time) | `&cFireball cooldown: &e$cooldown seconds` |

## Commands
| Command | Description | Permission |
|---------|-------------|------------|
| `/fblauncher reload` | Reload the configuration file | `fblauncher.admin` |
| `/fblauncher set <option> <value>` | Set a configuration option (e.g., `/fblauncher set explosiondistance 3.0`) | `fblauncher.admin` |
| `/fblauncher menu` | Open the GUI configuration menu (players only) | `fblauncher.admin` |

### Supported `<option>` for `/fblauncher set`
- `explosiondistance`
- `explosiony`
- `explosionstrength`
- `farexplosiony`
- `farexplosionstrength`
- `cooldownseconds`
- `message`
- `forcedisablefalldamage`
- `fireballspeed`
- `fireballparticle`
- `fireballparticlecount`

## Usage
1. **Launching Fireballs**:
   - Equip a Fireball item.
   - Right-click to launch a LargeFireball.
   - The fireball consumes one item per use unless the stack has multiple.

2. **Cooldown**:
   - A configurable cooldown prevents spamming.
   - Players receive a message with the remaining time if they try to use it too soon.

3. **Knockback**:
   - Fireballs apply knockback based on distance from the impact.
   - Close-range hits use `explosion.strength` and `explosion.y`.
   - Far-range hits (beyond `explosion.distance`) use `explosion.far-strength` and `explosion.far-y`.

4. **Team Protection**:
   - If `team-protection-enabled` is `true`, players cannot damage teammates on the same scoreboard team.

5. **GUI Menu**:
   - Run `/fblauncher menu` to open a GUI for editing settings.
   - Click items to input new values via chat (type `cancel` to abort).

## Notes
- **Fireball Consumption**: Each use consumes one Fireball item from the stack.
- **Air Spam Prevention**: If enabled, players must be near the ground to launch fireballs.
- **Particle Effects**: Use valid `org.bukkit.Effect` names (e.g., `FLAME`, `SMOKE`). Invalid names log a warning.
- **Explosion Protection**: Blocks listed in `excluded-from-explode-blocks` are immune to fireball explosions.
- **Damage Handling**:
  - Direct fireball hits deal reduced damage after 20 ticks to balance knockback effects.
  - Fall damage can be disabled to prevent knockback-related deaths.
- **Cleanup**: Fireballs and cooldowns are removed when a player dies or quits.

## Support
For issues or inquiries, join the [Support Discord](https://discord.gg/xydjE7ym5W) and open a ticket.
