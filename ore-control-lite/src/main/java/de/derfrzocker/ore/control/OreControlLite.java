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
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao_Old;
import de.derfrzocker.ore.control.impl.generationhandler.*;
import de.derfrzocker.ore.control.impl.v_1_16_R1.NMSUtil_v1_16_R1;
import de.derfrzocker.spigot.utils.Config;
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

public class OreControlLite extends JavaPlugin implements Listener {

    // register the ConfigurationSerializable's in a static block, that we can easy use them in Test cases
    static {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);
    }

    private NMSService nmsService = null;
    private Settings settings;

    @Override
    public void onLoad() {
        nmsService = new NMSServiceImpl(new NMSUtil_v1_16_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);

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
    }

    @Override
    public void onEnable() {
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

        checkFile("data/settings.yml");

        // load the Settings
        settings = new Settings(Config.getConfig(this, "data/settings.yml"));

        checkOldStorageType();

        // start the Metric of this Plugin (https://bstats.org/plugin/bukkit/Ore-Control)
        setUpMetric();

        // hook in the WorldGenerator, we try this before we register the commands and events, that if something goes wrong here
        // the player see that no command function, and looks in to the log to see if a error happen
        nmsService.replaceNMS();

        // register the Listener for the WorldLoad event
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setUpMetric() {
        // create a new Metrics
        new OreControlMetrics(this, OreControlServiceSupplier.INSTANCE) {
            @Override
            protected String getLanguage() {
                return "N/A";
            }

            @Override
            protected String getUseSafeMode() {
                return "N/A";
            }

            @Override
            protected String getUseTranslateTabCompilation() {
                return "N/A";
            }

            @Override
            protected String getUseVerifyCopyAction() {
                return "N/A";
            }

            @Override
            protected String getUseVerifyResetAction() {
                return "N/A";
            }
        };
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
