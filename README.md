# MiningHelmet
A Paper plugin adding a custom Mining Helmet with permanent Haste II and Night Vision
# MiningHelmet

A custom Paper plugin that adds the **Mining Helmet** — a diamond helmet with permanent Haste II and Night Vision, on top of top-tier armor protection.

**Author:** Minecraft User: oSmexy | GitHub: [@versaceapples](https://github.com/versaceapples)

> **Usage:** This plugin is free to download and run on your server. The source code is provided for transparency only — copying, modifying, or redistributing it without permission is not allowed.

## Features

- **Permanent Haste II** and **Permanent Night Vision** while worn — no potion upkeep, no expiring effects
- Fully enchanted: Protection IV, Fire Protection IV, Blast Protection IV, Mending
- Two randomly-assigned name colors (yellow / red) for a bit of variety
- A distinct equip sound so putting it on feels like a moment
- Cannot be upgraded to netherite at a smithing table — stays exactly what it is
- Console audit logging on every give/get for accountability
- Works on both Java and Bedrock (via Geyser/Floodgate)

## Commands

| Command | Description | Permission |
|---|---|---|
| `/mininghelmet` | Shows help/info | — |
| `/mininghelmet get` | Gives yourself 1 Mining Helmet | `mininghelmet.get` |
| `/mininghelmet give <player>` | Gives another online player 1 Mining Helmet | `mininghelmet.give` |

## Permissions

Every node defaults to `op`, so **the plugin works immediately with zero configuration** — no permissions plugin required.

If you'd like to grant access to specific ranks (e.g. a "VIP" or "Staff" group) without opping those players, you can override these nodes in [LuckPerms](https://luckperms.net/) or any other permissions plugin:

| Node | Description | Default |
|---|---|---|
| `mininghelmet.get` | Allows getting a Mining Helmet for yourself | `op` |
| `mininghelmet.give` | Allows giving a Mining Helmet to other players | `op` |
| `mininghelmet.*` | Wildcard covering both nodes above | `op` |

**Example (LuckPerms):**
```
/lp group vip permission set mininghelmet.get true
```
This grants the `get` command to everyone in the `vip` group, while `give` stays OP-only unless separately granted.

## Requirements

- [Paper](https://papermc.io/) 1.21+ (built/tested against Paper 26.2)
- Java 21

## Building

```bash
git clone https://github.com/<your-username>/MiningHelmet.git
cd MiningHelmet
mvn package
```

The compiled jar will be at `target/MiningHelmet.jar`. Drop it into your server's `plugins/` folder and restart. 

You can also view Releases to download the latest version:

> **Note:** Update the `paper-api` version in `pom.xml` to match your actual Paper build before compiling.

## Installation

1. Download or build `MiningHelmet.jar`
2. Place it in your server's `plugins/` directory
3. Restart the server
4. Works immediately for OP'd accounts — optionally configure LuckPerms as described above for non-OP ranks
