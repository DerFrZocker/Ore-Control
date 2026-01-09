# Ore-Control

Ore-Control is a powerful Spigot plugin that allows you to manipulate the default Minecraft ore generation in your world.

**Note:** The plugin only affects newly generated chunks. Existing worlds or chunks are not affected and will need to be regenerated.

**Note:** If you are updating from version **v2021.06.29** or earlier, or from the pre-release versions **v2022.01.31** or **v2021.12.31**, you will need to reconfigure the plugin.

With Ore-Control, you can customize the following aspects of ore generation:

* The number of ores to generate in a chunk.
* The size of individual ore veins.
* The height at which ores should generate.
* The ability to stop the generation of specific ores.
* The ability to stop the generation of large [ore veins](https://minecraft.fandom.com/wiki/Ore_vein).
* Experimental feature: Change which blocks should be generated and which ones should be replaced.
* Experimental feature: Generation based on mathematical equations. For example, making ores more common the further you are from the center. (This feature is only available through file system configuration.)
* Compatibility with custom biomes and features from other datapacks, such as [terralith](https://www.planetminecraft.com/data-pack/terralith-overworld-evolved-100-biomes-caves-and-more/).

You have multiple options for applying these settings. You can configure them on a global scale to affect every world, on a global per biome scale to affect a specific biome in all worlds, or on a per-world scale to affect only one world. Additionally, you can choose to affect only one specific biome in a certain world.

Configuration can be done in-game using an inventory GUI (`/ore-control`), or via the file system (recommended for advanced users or for features not available in the inventory GUI).

When using the inventory GUI, your changes will be applied immediately to newly generated chunks without the need to restart the server.

Links
-----

* [Spigot plugin page](https://www.spigotmc.org/resources/63621/) (English)
* [Minecraft-server.eu plugin page](https://minecraft-server.eu/forum/resources/17/) (German)
* [Dev Builds](https://jenkins.derfrzocker.de/job/minecraft/job/ore-control/job/ore-control-dev/)
* [Wiki](https://github.com/DerFrZocker/Ore-Control/wiki)
* [bStats](https://bstats.org/plugin/bukkit/Ore-Control)
* [Support](https://github.com/DerFrZocker/Ore-Control/discussions)
* [Donate](https://github.com/sponsors/DerFrZocker)