<img style="text-align:center" src="img/banner.png">

> [!WARNING]
> Like some of my other projects, this repository has been migrated to Codeberg.
> Any new issues or pull requests here will be ignored, please create them over at the new repository.
> 
> New repository:
> https://codeberg.org/enjarai/do-a-barrel-roll

[<img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg">](https://discord.gg/WcYsDDQtyR)
[<img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg">](https://ko-fi.com/enjarai)

**On Fabric:**

[<img alt="fabric-api" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg">](https://modrinth.com/mod/fabric-api)
[<img alt="fabric-api" height="56" src="https://enjarai.dev/static/requires_cicada_cozy.svg">](https://modrinth.com/mod/cicada)

[![Modrinth](https://img.shields.io/modrinth/dt/do-a-barrel-roll)](https://modrinth.com/mod/do-a-barrel-roll)
[![CurseForge](https://cf.way2muchnoise.eu/full_663658_downloads.svg)](https://curseforge.com/minecraft/mc-mods/do-a-barrel-roll)

Do a Barrel Roll is a lightweight, fully clientside mod that changes 
elytra flight to be more fun and semi-realistic.
It achieves this by redesigning movement with a 
completely unlocked camera orientation in mind, 
allowing for full pitch, yaw and roll control in flight.

Some subtle and less subtle modifiers are also applied to the camera,
including but not limited to smoothing and banking.

![](img/ravine.gif)

## Controls

The default controls are as follows, but can be modified:

- Mouse x axis to roll
- Mouse y axis to pitch
- strafe keys (normally A and D) to yaw

## Configuration

The mod can be configured in-game using [ModMenu](https://modrinth.com/mod/modmenu) and [YACL](https://modrinth.com/mod/yacl).
Once you install both of these,
you can access the config screen by finding the mod in the mods list and pressing the config button.

A wide range of options are available, including custom mouse behavior, elytra activation restrictions and
changing values of modifiers like banking, sensitivity and more.

## Server-side features

Visual aspects of the mod (playermodel roll in particular) can be synced between 
clients by installing it on the server-side as well. 
Everything is still fully compatible with both vanilla clients and servers. 
All the following configurations are valid:

- Server with mod, client with mod: visuals are synced
- Server with mod, client without mod: client can join and play without issues, but can't see visuals
- Server without mod, client with mod: client can join and play without issues, but can only see their own visuals
- Server with mod, client 1 with mod, client 2 with mod, client 3 without mod: client 1 and 2 can see each other's visuals, client 3 cannot
- Server without mod, client 1 with mod, client 2 with mod, client 3 without mod: client 1 and 2 can only see their own visuals

![](img/do-a-barrel-roll.gif)

## Disclaimers

This mod does not actually modify the flight physics themselves, 
so in most cases it shouldn't trigger anticheat. 
However, doing things like loop de loops looks to the server like rapid camera movement
and may get you flagged.
**Use at your own risk.**

**Versions prior to 2.8.3 may not be suitable for use on multiplayer servers due to lax server-side restrictions**

## Credits

Based on the [Cool Elytra Roll](https://github.com/Jorbon/cool_elytra) mod by [Jorbon](https://github.com/Jorbon),
specifically it's "realistic mode".

Originally ported to Forge by [MehVahdJukaar](https://github.com/MehVahdJukaar).

Mod icon by Mizeno.

## For server admins

### Thrusting

The mod includes a "thrusting" feature that is probably considered cheating by most servers, 
as it lets users accelerate without using fireworks.

**This feature is disabled by default on servers as of 2.8.3.**

If you want to allow players to use this feature on your server you can, 
depending on your server platform, use one of the methods below.
These configurations will be respected by any clients using version 2.4.0 or later of the mod.
The pre-1.20 Forge versions do not support thrusting and other server-side featuers, 
so they will not be affected by any of these options.

#### For Fabric and Forge servers

Fabric or Forge server owners can install the mod on their server and, while in the world, use the "Server" tab on the config screen to configure it. (recommended)
- This tab is only available to level 3 server operators and players with the `do_a_barrel_roll.configure` permission.
- You can also set values manually in `config/do_a_barrel_roll-server.json`
  to configure the mod without a client.

#### For Bukkit/Spigot servers

On platforms that support Bukkit plugins, you can use the [DABRCS](https://modrinth.com/plugin/dabrcs/) plugin to configure the mod.
- While this is a third-party creation, it is officially endorsed by me.

#### For other server platforms

On servers that have no official support, you'll have to create a custom plugin or mod to send a packet to the client at login on 
the `do_a_barrel_roll:handshake` channel with the following layout:

| Type    | Value                      |
|---------|----------------------------|
| integer | `1`                        |
| string  | `{"allowThrusting": true}` |

Any client that has DABR installed will respond on the same channel with a packet like this:

| Type    | Value              |
|---------|--------------------|
| integer | [protocol version] |
| boolean | [success]          |

### Other features

The server config includes a few other features to configure server-side behaviour, including:

- `forceEnabled`: Forces the mod to be enabled for all players, regardless of their client configuration.
- `forceInstalled`: Rejects any player trying to join without having the mod installed on their client.
- `installedTimeout`: The amount of time (in ticks) to wait for a client to respond to the `do_a_barrel_roll:config_sync` packet.
  - If the client does not respond in time, and `forceInstalled` is set to `true`, they will be kicked from the server.
  - You may want to increase this value if players with bad connections are getting kicked despite having the mod installed.

These options can all be configured via the same methods as the `allowThrusting` option.

It is also possible to allow specific players to bypass configured restrictions by giving them level 2 operator status 
or the `do_a_barrel_roll.ignore_config` permission.
