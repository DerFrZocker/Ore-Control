package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.NMSService;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.command.*;
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSUtil_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSUtil_v1_13_R2;
import de.derfrzocker.ore.control.impl.v1_14_R1.NMSUtil_v1_14_R1;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.Version;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

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
    @NonNull
    private static OreControl instance; // A static instance of this Plugin, that we can easy access it from any Location

    @Getter
    private ConfigValues configValues; // The Config values of this plugin

    @Getter
    private Settings settings; // The Settings of this Plugin, other than the ConfigValues, this Values should not be modified

    private NMSService nmsService = null; // The NMSService, we use this Variable, that we can easy set the variable in the onLoad method and use it in the onEnable method

    private final OreControlCommand oreControlCommand = new OreControlCommand(); // The OreControlCommand handler

    @Override
    public void onLoad() {
        // initial instance variable
        instance = this;

        if (Version.getCurrent() == Version.v1_13_R1)
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        else if (Version.getCurrent() == Version.v1_13_R2)
            nmsService = new NMSServiceImpl(new NMSUtil_v1_13_R2(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        else if (Version.getCurrent() == Version.v1_14_R1) {
            nmsService = new NMSServiceImpl(new NMSUtil_v1_14_R1(OreControlServiceSupplier.INSTANCE), OreControlServiceSupplier.INSTANCE);
        }
        // if no suitable version was found, throw an Exception and stop onLoad part
        if (nmsService == null)
            throw new IllegalStateException("no matching server version found, stop plugin start", new NullPointerException("overrider can't be null"));

        // load the config values of this plugin
        configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));

        // Set default language
        Language.setDefaultLanguage(() -> getConfigValues().getLanguage());
    }

    @Override
    public void onEnable() {
        // return if no suitable NMSService was found in onLoad
        if (nmsService == null)
            return;

        final WorldOreConfigYamlDao worldOreConfigYamlDao = new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world_ore_configs.yml"));

        // Register the OreControl Service, this need for checkFile and Settings, since some Files have Objects that need this Service to deserialize
        Bukkit.getServicesManager().register(OreControlService.class,
                new OreControlServiceImpl(
                        nmsService,
                        worldOreConfigYamlDao),
                this, ServicePriority.Normal);

        // we init the WorldOreConfigYamlDao later, since it need the Service, to load the WorldOreConfig's correctly
        worldOreConfigYamlDao.init();

        // check all files, that can be have other values (not other not new one), so we can replace them
        checkFile("data/biome_groups.yml");
        checkFile("data/biome_gui.yml");
        checkFile("data/boolean_gui.yml");
        checkFile("data/config_gui.yml");
        checkFile("data/language_gui.yml");
        checkFile("data/ore_gui.yml");
        checkFile("data/ore_settings_gui.yml");
        checkFile("data/settings.yml");
        checkFile("data/settings_gui.yml");
        checkFile("data/verify_gui.yml");
        checkFile("data/world_config_gui.yml");
        checkFile("data/world_gui.yml");

        if (Version.getCurrent() == Version.v1_14_R1) {
            checkFile("data/biome_gui_v1.14.yml");
        }

        // load the Settings
        settings = new Settings(Config.getConfig(this, "data/settings.yml"));

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
        final Metrics metrics = new Metrics(this);

        // add a simple Pie with the current Language that the user use
        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigValues().getLanguage().getNames()[0]));
    }

    private void registerCommands() {
        // register the command handler to Bukkit
        getCommand("orecontrol").setExecutor(oreControlCommand);

        // register all subcommand's to the command handler
        oreControlCommand.registerExecutor(new SetCommand(OreControlServiceSupplier.INSTANCE), "set");
        oreControlCommand.registerExecutor(new ReloadCommand(), "reload");
        oreControlCommand.registerExecutor(new SetBiomeCommand(OreControlServiceSupplier.INSTANCE), "setbiome");
        oreControlCommand.registerExecutor(new CreateCommand(OreControlServiceSupplier.INSTANCE), "create");
        oreControlCommand.registerExecutor(new GuiCommand(OreControlServiceSupplier.INSTANCE), "");

        final HelpCommand helpCommand = new HelpCommand();
        oreControlCommand.registerExecutor(helpCommand, null);
        oreControlCommand.registerExecutor(helpCommand, "help");
    }

    private void checkFile(final @NonNull String name) {
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
    public void onWorldLoad(final @NonNull WorldLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () ->
                OreControlServiceSupplier.INSTANCE.get().getWorldOreConfig(event.getWorld().getName()).ifPresent(value -> {
                    if (value.isTemplate()) {
                        value.setTemplate(false);
                        OreControlServiceSupplier.INSTANCE.get().saveWorldOreConfig(value);
                    }
                })
        );
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
