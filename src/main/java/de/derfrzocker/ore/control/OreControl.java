package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.command.*;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreControlServiceImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSReplacer_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSReplacer_v1_13_R2;
import de.derfrzocker.ore.control.inventorygui.InventoryClickListener;
import de.derfrzocker.ore.control.utils.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class OreControl extends JavaPlugin {

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
        checkFile("data/ore_settings_gui.yml");
        checkFile("data/settings_gui.yml");
        checkFile("data/world_config_gui.yml");
        checkFile("data/world_gui.yml");

        String version = getVersion();

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

        Bukkit.getServicesManager().register(OreControlService.class,
                new OreControlServiceImpl(
                        nmsReplacer,
                        new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world_ore_configs.yml"))),
                this, ServicePriority.Normal);

        registerCommands();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);

        nmsReplacer.replaceNMS();
    }

    private void registerCommands() {
        getCommand("orecontrol").setExecutor(oreControlCommand);
        oreControlCommand.registerExecutor(new SetCommand(), "set");
        oreControlCommand.registerExecutor(new ReloadCommand(), "reload");
        oreControlCommand.registerExecutor(new SetBiomeCommand(), "setbiome");
        oreControlCommand.registerExecutor(new GuiCommand(), "");

        HelpCommand helpCommand = new HelpCommand();
        oreControlCommand.registerExecutor(helpCommand, null);
        oreControlCommand.registerExecutor(helpCommand, "help");
    }

    private void checkFile(String name) {
        File file = new File(getDataFolder(), name);

        YamlConfiguration configuration = new Config(new File(getDataFolder(), name));

        YamlConfiguration configuration2 = new Config(getResource(name));

        if (configuration.getInt("version") == configuration2.getInt("version"))
            return;

        getLogger().warning("File " + name + " has an outdated / new version, replacing it!");

        if (!file.delete())
            throw new RuntimeException("can't delete file " + name + " stop plugin start!");

        saveResource(name, true);
    }

    private String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();

        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static OreControlService getService() {
        OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null)
            throw new IllegalStateException("The Bukkit Service have no " + OreControlService.class.getName() + " registered", new NullPointerException("service can't be null"));

        return service;
    }

}
