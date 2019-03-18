package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.command.*;
import de.derfrzocker.ore.control.gui.InventoryClickListener;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreControlServiceImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSReplacer_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSReplacer_v1_13_R2;
import de.derfrzocker.ore.control.utils.Config;
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

public class OreControl extends JavaPlugin implements Listener {

    static {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);
    }

    @Getter
    @Setter
    @NonNull
    private static OreControl instance;

    @Getter
    private ConfigValues configValues;

    @Getter
    private Settings settings;

    private NMSReplacer nmsReplacer = null;

    private final OreControlCommand oreControlCommand = new OreControlCommand();

    @Override
    public void onLoad() {
        instance = this;

        checkFile("data/settings.yml");
        checkFile("data/biome_gui.yml");
        checkFile("data/ore_gui.yml");
        checkFile("data/ore_settings_gui.yml");
        checkFile("data/settings_gui.yml");
        checkFile("data/world_config_gui.yml");
        checkFile("data/world_gui.yml");

        final String version = getVersion();

        if (version.equalsIgnoreCase("v1_13_R1"))
            nmsReplacer = new NMSReplacer_v1_13_R1();
        else if (version.equalsIgnoreCase("v1_13_R2"))
            nmsReplacer = new NMSReplacer_v1_13_R2();

        if (nmsReplacer == null)
            throw new IllegalStateException("no matching server version found, stop plugin start", new NullPointerException("overrider can't be null"));

        configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));

        settings = new Settings(Config.getConfig(this, "data/settings.yml"));

        //noinspection ResultOfMethodCallIgnored
        OreControlMessages.getInstance();
    }

    @Override
    public void onEnable() {
        if (nmsReplacer == null)
            return;

        nmsReplacer.replaceNMS();

        setUpMetric();

        Bukkit.getServicesManager().register(OreControlService.class,
                new OreControlServiceImpl(
                        nmsReplacer,
                        new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world_ore_configs.yml"))),
                this, ServicePriority.Normal);

        registerCommands();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setUpMetric() {
       final Metrics metrics = new Metrics(this);

        metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfigValues().getLanguage().getName()));
    }

    private void registerCommands() {
        getCommand("orecontrol").setExecutor(oreControlCommand);
        oreControlCommand.registerExecutor(new SetCommand(), "set");
        oreControlCommand.registerExecutor(new ReloadCommand(), "reload");
        oreControlCommand.registerExecutor(new SetBiomeCommand(), "setbiome");
        oreControlCommand.registerExecutor(new CreateCommand(), "create");
        oreControlCommand.registerExecutor(new GuiCommand(), "");

        final HelpCommand helpCommand = new HelpCommand();
        oreControlCommand.registerExecutor(helpCommand, null);
        oreControlCommand.registerExecutor(helpCommand, "help");
    }

    private void checkFile(String name) {
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

    private String getVersion() {
        final String name = Bukkit.getServer().getClass().getPackage().getName();

        return name.substring(name.lastIndexOf('.') + 1);
    }

    @EventHandler //TODO maybe extra class
    public void onWorldLoad(WorldLoadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(OreControl.getInstance(), () ->
                getService().getWorldOreConfig(event.getWorld().getName()).ifPresent(value -> {
                    if (value.isTemplate()) {
                        value.setTemplate(false);
                        getService().saveWorldOreConfig(value);
                    }
                })
        );
    }

    public static OreControlService getService() {
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null)
            throw new IllegalStateException("The Bukkit Service have no " + OreControlService.class.getName() + " registered", new NullPointerException("service can't be null"));

        return service;
    }

}
