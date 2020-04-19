/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.command.OreControlCommand;
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao_Old;
import de.derfrzocker.ore.control.impl.generationhandler.*;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSUtil_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSUtil_v1_13_R2;
import de.derfrzocker.ore.control.impl.v1_14_R1.NMSUtil_v1_14_R1;
import de.derfrzocker.ore.control.impl.v_15_R1.NMSUtil_v1_15_R1;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.Version;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.Supplier;

public class OreControl extends JavaPlugin implements Listener {

    // register the ConfigurationSerializable's in a static block, that we can easy use them in Test cases
    static {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);
    }

    @Getter
    @Setter
    private static OreControl instance;

    @Getter
    private ConfigValues configValues; // The Config values of this plugin
    @Getter
    private Settings settings; // The Settings of this Plugin, other than the ConfigValues, this Values should not be modified
    private NMSService nmsService = null; // The NMSService, we use this Variable, that we can easy set the variable in the onLoad method and use it in the onEnable method
    private OreControlCommand oreControlCommand; // The OreControlCommand handler
    private OreControlMessages oreControlMessages;
    private Permissions permissions;

    @Override
    public void onLoad() {
        // initial instance variable
        instance = this;

        if (Version.getCurrent() == Version.v1_13_R1)
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        else if (Version.getCurrent() == Version.v1_13_R2)
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R2(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        else if (Version.getCurrent() == Version.v1_14_R1)
            nmsService = new NMSServiceImpl(new NMSUtil_v1_14_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        else if (Version.getCurrent() == Version.v1_15_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_15_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        }
        // if no suitable version was found, throw an Exception and stop onLoad part
        if (nmsService == null)
            throw new IllegalStateException("no matching server version found, stop plugin start", new NullPointerException("overrider can't be null"));

        // register GenerationHandlers
        final GenerationHandler normalOreGenerationHandler = new NormalOreGenerationHandler(nmsService.getNMSUtil());
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
        nmsService.registerGenerationHandler(Ore.NETHER_QUARTZ, new NetherQuartzGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.INFESTED_STONE, normalOreGenerationHandler);
        nmsService.registerGenerationHandler(Ore.EMERALD, new EmeraldGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.LAPIS, new LapisGenerationHandler(nmsService.getNMSUtil()));
        nmsService.registerGenerationHandler(Ore.MAGMA, new MagmaGenerationHandler(nmsService.getNMSUtil()));

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

        final WorldOreConfigDao worldOreConfigYamlDao = new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world-ore-configs"));

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
                    protected OreSettings getNewOreSetting(@NotNull final Ore ore) {
                        return new OreSettingsYamlImpl(ore);
                    }

                    @NotNull
                    @Override
                    protected WorldOreConfig getNewWorldOreConfig(@NotNull final String name, final boolean template) {
                        return new WorldOreConfigYamlImpl(name, template);
                    }

                    @NotNull
                    @Override
                    protected BiomeOreSettings getNewBiomeOreSettings(@NotNull final Biome biome) {
                        return new BiomeOreSettingsYamlImpl(biome);
                    }
                },
                this, ServicePriority.Normal);

        // check all files, that can be have other values (not other not new one), so we can replace them
        checkFile("data/gui/biome-groups.yml");
        checkFile("data/gui/biome-gui.yml");
        checkFile("data/gui/boolean-gui.yml");
        checkFile("data/gui/config-gui.yml");
        checkFile("data/gui/language-gui.yml");
        checkFile("data/gui/ore-gui.yml");
        checkFile("data/gui/ore-settings-gui.yml");
        checkFile("data/settings.yml");
        checkFile("data/gui/settings-gui.yml");
        checkFile("data/gui/verify-gui.yml");
        checkFile("data/gui/world-config-gui.yml");
        checkFile("data/gui/world-gui.yml");

        if (Version.getCurrent() == Version.v1_14_R1) {
            checkFile("data/gui/biome-gui_v1.14.yml");
        }

        // load the Settings
        settings = new Settings(Config.getConfig(this, "data/settings.yml"));

        checkOldStorageType();

        // start the Metric of this Plugin (https://bstats.org/plugin/bukkit/Ore-Control)
        setUpMetric();

        // hook in the WorldGenerator, we try this before we register the commands and events, that if something goes wrong here
        // the player see that no command function, and looks in to the log to see if a error happen
        nmsService.replaceNMS();

        // register the command and subcommand's
        registerCommands();

        // register the Listener for the WorldLoad event
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setUpMetric() {
        // create a new Metrics
        new OreControlMetrics(this, OreControlServiceSupplier.INSTANCE) {
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

    private void registerCommands() {
        getCommand("orecontrol").setExecutor(oreControlCommand = new OreControlCommand(new OreControlValues(OreControlServiceSupplier.INSTANCE, this, configValues, oreControlMessages, permissions)));
    }

    private void checkFile(@NotNull final String name) {
        final File file = new File(getDataFolder(), name);

        if (!file.exists())
            return;

        final YamlConfiguration configuration = new Config(file);

        final YamlConfiguration configuration2 = new Config(getResource(name));

        if (configuration.getInt("version") == configuration2.getInt("version"))
            return;

        getLogger().warning("File " + name + " has an outdated / new version, replacing it!");

        if (!file.delete())
            throw new RuntimeException("can't delete file " + name + " stop plugin start!");

        saveResource(name, true);
    }

    @EventHandler //TODO maybe extra class
    public void onWorldLoad(@NotNull final WorldLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                OreControlServiceSupplier.INSTANCE.get().getWorldOreConfig(event.getWorld().getName()).ifPresent(value -> {
                    if (value.isTemplate()) {
                        value.setTemplate(false);
                        OreControlServiceSupplier.INSTANCE.get().saveWorldOreConfig(value);
                    }
                })
        );
    }

    @Deprecated
    private void checkOldStorageType() {
        final File file = new File(getDataFolder(), "data/world_ore_configs.yml");

        if (!file.exists())
            return;

        if (file.isDirectory()) {
            getLogger().info("WTF?? why??");
            return;
        }

        getLogger().info("Found old storage type, convert to new one");

        final WorldOreConfigYamlDao_Old worldConfigYamlDao = new WorldOreConfigYamlDao_Old(file);
        worldConfigYamlDao.init();

        final OreControlService service = OreControlServiceSupplier.INSTANCE.get();

        worldConfigYamlDao.getAll().forEach(service::saveWorldOreConfig);

        if (!file.delete())
            throw new RuntimeException("Can not delete File " + file);

        getLogger().info("Finish converting old storage format to new one");
    }


    private static final class OreControlServiceSupplier implements Supplier<OreControlService> {

        private static final OreControlServiceSupplier INSTANCE = new OreControlServiceSupplier();

        private OreControlService service;

        @Override
        public OreControlService get() {
            final OreControlService tempService = Bukkit.getServicesManager().load(OreControlService.class);

            if (service == null && tempService == null)
                throw new NullPointerException("The Bukkit Service has no OreControlService and no OreControlService is cached!");

            if (tempService != null && service != tempService)
                service = tempService;

            return service;
        }

    }

}
