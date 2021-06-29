/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.command.NoCommandsAvailableCommand;
import de.derfrzocker.ore.control.command.OreControlCommand;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao_Old;
import de.derfrzocker.ore.control.impl.generationhandler.*;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSUtil_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSUtil_v1_13_R2;
import de.derfrzocker.ore.control.impl.v1_14_R1.NMSUtil_v1_14_R1;
import de.derfrzocker.ore.control.impl.v1_15_R1.NMSUtil_v1_15_R1;
import de.derfrzocker.ore.control.impl.v1_16_R1.NMSUtil_v1_16_R1;
import de.derfrzocker.ore.control.impl.v1_16_R2.NMSUtil_v1_16_R2;
import de.derfrzocker.ore.control.impl.v1_16_R3.NMSUtil_v1_16_R3;
import de.derfrzocker.ore.control.impl.v1_17_R1.NMSUtil_v1_17_R1;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class OreControl extends JavaPlugin implements Listener {

    private static OreControl instance;

    // register the ConfigurationSerializable's in a static block, that we can easy use them in Test cases
    static {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);
    }

    private ConfigValues configValues; // The Config values of this plugin
    private Settings settings; // The Settings of this Plugin, other than the ConfigValues, this Values should not be modified
    private NMSServiceImpl nmsService = null; // The NMSService, we use this Variable, that we can easy set the variable in the onLoad method and use it in the onEnable method
    private OreControlCommand oreControlCommand; // The OreControlCommand handler
    private OreControlMessages oreControlMessages;
    private Permissions permissions;
    private OreControlServiceSupplier oreControlServiceSupplier;

    public static OreControl getInstance() {
        return instance;
    }

    public static void setInstance(final OreControl instance) {
        OreControl.instance = instance;
    }

    public ConfigValues getConfigValues() {
        return this.configValues;
    }

    @Override
    public void onLoad() {
        // initial instance variable
        instance = this;
        this.oreControlServiceSupplier = new OreControlServiceSupplier(this);

        Version version = Version.getServerVersion(getServer());

        if (version == Version.v1_13_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R1(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_13_R2) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R2(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_14_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_14_R1(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_15_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_15_R1(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_16_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_16_R1(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_16_R2) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_16_R2(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_16_R3) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_16_R3(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        } else if (version == Version.v1_17_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_17_R1(this.oreControlServiceSupplier), this.oreControlServiceSupplier);
        }

        // if no suitable version was found, log and return
        if (nmsService == null) {
            getLogger().warning("The Server version which you are running is unsupported, you are running version '" + version + "'");
            getLogger().warning("The plugin supports following version " + combineVersions(Version.v1_13_R1, Version.v1_13_R2, Version.v1_14_R1, Version.v1_15_R1, Version.v1_16_R1, Version.v1_16_R2, Version.v1_16_R3, Version.v1_17_R1));
            getLogger().warning("(Spigot / Paper version 1.13.1 - 1.17), if you are running such a Minecraft version, than your bukkit implementation is unsupported, in this case please contact the developer, so he can resolve this Issue");

            if (version == Version.UNKNOWN) {
                getLogger().warning("The Version '" + version + "' can indicate, that you are using a newer Minecraft version than currently supported.");
                getLogger().warning("In this case please update to the newest version of this plugin. If this is the newest Version, than please be patient. It can take some weeks until the plugin is updated");
            }

            return;
        }

        // register GenerationHandlers
        final GenerationHandler normalOreGenerationHandler = new NormalOreGenerationHandler(nmsService.getNMSUtil());
        final GenerationHandler netherNormalOreGenerationHandler = new NetherNormalOreGenerationHandler(nmsService.getNMSUtil());
        nmsService.registerGenerationHandler(Ore.DIAMOND, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.COAL, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.GOLD, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.GOLD_BADLANDS, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.IRON, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.REDSTONE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.DIRT, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.GRAVEL, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.GRANITE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.DIORITE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.ANDESITE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.NETHER_QUARTZ, netherNormalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.INFESTED_STONE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.EMERALD, new EmeraldGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.LAPIS, new LapisGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.MAGMA, new MagmaGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.NETHER_GOLD, netherNormalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.ANCIENT_DEBRIS, netherNormalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.ANCIENT_DEBRIS_2, new NetherAncientDebrisGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.COPPER, new LapisGenerationHandler(nmsService.getNMSUtil()));

        // load the config values of this plugin
        configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));

        // Set default language
        Language.setDefaultLanguage(() -> getConfigValues().getLanguage());
    }

    @Override
    public void onEnable() {
        // return if no suitable NMSService was found in onLoad
        if (nmsService == null) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        oreControlMessages = new OreControlMessages(this);
        permissions = new Permissions(this);
        Version version = Version.getServerVersion(getServer());
        oreControlServiceSupplier.registerEvents();

        final WorldOreConfigYamlDao worldOreConfigYamlDao = new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world-ore-configs"));

        // Register the OreControl Service, this need for checkFile and Settings, since some Files have Objects that need this Service to deserialize
        Bukkit.getServicesManager().register(OreControlService.class,
                new OreControlServiceImpl(
                        nmsService,
                        worldOreConfigYamlDao) {
                    @NotNull
                    @Override
                    protected OreSettings getDefaultOreSetting(@NotNull final Ore ore) {
                        return settings.getDefaultSettings(ore);
                    }

                    @NotNull
                    @Override
                    protected OreSettings getDefaultOreSetting(@NotNull Biome biome, @NotNull Ore ore) {
                        return settings.getDefaultSettings(biome, ore);
                    }

                    @NotNull
                    @Override
                    protected OreSettings getNewOreSetting(@NotNull final Ore ore) {
                        return new OreSettingsYamlImpl(ore);
                    }

                    @NotNull
                    @Override
                    protected WorldOreConfig getNewWorldOreConfig(@NotNull final String name, final boolean template) {
                        return new WorldOreConfigYamlImpl(name, template ? ConfigType.TEMPLATE : ConfigType.UNKNOWN);
                    }

                    @NotNull
                    @Override
                    protected BiomeOreSettings getNewBiomeOreSettings(@NotNull final Biome biome) {
                        return new BiomeOreSettingsYamlImpl(biome);
                    }
                },
                this, ServicePriority.Normal);

        checkFile("data/settings.yml");

        // load the Settings
        settings = new Settings(() -> Config.getConfig(this, "data/settings.yml"), version, getLogger());

        checkOldStorageType();
        checkLanguage();
        worldOreConfigYamlDao.reload();

        // start the Metric of this Plugin (https://bstats.org/plugin/bukkit/Ore-Control)
        setUpMetric();

        // hook in the WorldGenerator, we try this before we register the commands and events, that if something goes wrong here
        // the player see that no command function, and looks in to the log to see if a error happen
        nmsService.replaceNMS();

        // register the Listener for the WorldLoad event
        Bukkit.getPluginManager().registerEvents(this, this);

        // checking if the server jar contains the bungee chat api
        // for example normal CraftBukkit does not contains it
        // if it does not contains it, print a message that no commands are available
        // but the plugin should run anyway
        try {
            Class.forName("net.md_5.bungee.api.ChatColor");
        } catch (final ClassNotFoundException e) {
            getLogger().warning("It seems your are running a server jar, which does not contains the package 'net.md_5.bungee.api'");
            getLogger().warning("This plugin requires this package for the commands and gui");
            getLogger().warning("If no other error appears, than the plugin should work anyway (beside commands and gui)");
            getLogger().warning("If you want to use the commands and gui, please use a server jar which contains the package 'net.md_5.bungee.api', such as Spigot (not CraftBukkit)");
            getLogger().warning("After you have set the values, you can use the other server jar again");

            getCommand("orecontrol").setExecutor(new NoCommandsAvailableCommand());

            return;
        }

        if (configValues.showWelcomeMessage()) {
            final WelcomeMessage welcomeMessage = new WelcomeMessage(this, oreControlMessages);
            final PlayerJoinListener playerJoinListener = new PlayerJoinListener(permissions.getBasePermission(), welcomeMessage);

            registerCommands(playerJoinListener, welcomeMessage);
            getServer().getPluginManager().registerEvents(playerJoinListener, this);
        } else {
            registerCommands(null, null);
        }

    }

    private void checkLanguage() {
        for (Language language : Language.values()) {
            File file = new File(getDataFolder(), language.getFileLocation());
            if (!file.exists()) {
                continue;
            }

            Config diskConfig = new Config(file);
            Config jarConfig = new Config(getResource(language.getFileLocation()));
            int diskVersion = diskConfig.getInt("version", 0);
            int jarVersion = jarConfig.getInt("version", 0);

            if (diskVersion != jarVersion) {
                getLogger().warning("The language file " + language.getFileLocation() + " has an outdated / new version, replacing it!");

                File newLocation = new File(getDataFolder(), language.getFileLocation() + "_old_" + diskVersion);

                int i = 1;
                while (newLocation.exists()) {
                    newLocation = new File(getDataFolder(), language.getFileLocation() + "_old_" + diskVersion + "_" + i);
                    i++;
                }

                try {
                    newLocation.createNewFile();
                    Files.move(file.toPath(), newLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Error while creating and moving file", e);
                }

                getLogger().warning("You find the old version under: " + newLocation.toPath());
            }

        }
    }

    private void setUpMetric() {
        // create a new Metrics
        new OreControlMetrics(this, this.oreControlServiceSupplier) {
            @Override
            protected String getLanguage() {
                return configValues.getLanguage().getNames()[0];
            }

            @Override
            protected String getUseSafeMode() {
                return String.valueOf(configValues.isSafeMode());
            }

            @Override
            protected String getUseTranslateTabCompilation() {
                return String.valueOf(configValues.isTranslateTabCompilation());
            }

            @Override
            protected String getUseVerifyCopyAction() {
                return String.valueOf(configValues.verifyCopyAction());
            }

            @Override
            protected String getUseVerifyResetAction() {
                return String.valueOf(configValues.verifyResetAction());
            }
        };
    }

    private void registerCommands(@Nullable final PlayerJoinListener playerJoinListener, @Nullable final WelcomeMessage welcomeMessage) {
        getCommand("orecontrol").setExecutor(oreControlCommand = new OreControlCommand(
                new OreControlValues(this.oreControlServiceSupplier, this, configValues, oreControlMessages, permissions, Version.getServerVersion(getServer())),
                new GuiSettings(this, "data/gui", Version.getServerVersion(getServer()))
                , playerJoinListener, welcomeMessage));
    }

    private void checkFile(@NotNull final String name) {
        final File file = new File(getDataFolder(), name);

        if (!file.exists()) {
            return;
        }

        final YamlConfiguration configuration = new Config(file);

        final YamlConfiguration configuration2 = new Config(getResource(name));

        if (configuration.getInt("version") == configuration2.getInt("version")) {
            return;
        }

        getLogger().warning("File " + name + " has an outdated / new version, replacing it!");

        if (!file.delete()) {
            throw new RuntimeException("can't delete file " + name + " stop plugin start!");
        }

        saveResource(name, true);
    }

    @EventHandler //TODO maybe extra class
    public void onWorldLoad(@NotNull final WorldLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                this.oreControlServiceSupplier.get().getWorldOreConfig(event.getWorld().getName()).ifPresent(value -> {
                    if (value.getConfigType() == ConfigType.TEMPLATE) {
                        value.setConfigType(getConfigType(event.getWorld()));
                        this.oreControlServiceSupplier.get().saveWorldOreConfig(value);
                    }
                    if (value.getConfigType() == ConfigType.GLOBAL) {
                        WorldOreConfig worldOreConfig = value.clone(value.getName());
                        worldOreConfig.setConfigType(getConfigType(event.getWorld()));
                        this.oreControlServiceSupplier.get().saveWorldOreConfig(worldOreConfig);
                    }
                })
        );
    }

    private ConfigType getConfigType(World world) {
        Dimension dimension = this.oreControlServiceSupplier.get().getNMSService().getNMSUtil().getDimension(world);

        switch (dimension) {
            case OVERWORLD:
                return ConfigType.OVERWORLD;
            case NETHER:
                return ConfigType.NETHER;
            case THE_END:
            case CUSTOM:
                return ConfigType.UNKNOWN;
        }

        return ConfigType.UNKNOWN;
    }

    @Deprecated
    private void checkOldStorageType() {
        final File file = new File(getDataFolder(), "data/world_ore_configs.yml");

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            getLogger().info("WTF?? why??");
            return;
        }

        getLogger().info("Found old storage type, convert to new one");

        final WorldOreConfigYamlDao_Old worldConfigYamlDao = new WorldOreConfigYamlDao_Old(file);
        worldConfigYamlDao.init();

        final OreControlService service = this.oreControlServiceSupplier.get();

        worldConfigYamlDao.getAll().forEach(service::saveWorldOreConfig);

        if (!file.delete()) {
            throw new RuntimeException("cannot delete File " + file);
        }

        getLogger().info("Finish converting old storage format to new one");
    }

    private String combineVersions(Version... versions) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;

        for (Version version : versions) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(" ");
            }

            stringBuilder.append("'");
            stringBuilder.append(version);
            stringBuilder.append("'");
        }

        return stringBuilder.toString();
    }

}
