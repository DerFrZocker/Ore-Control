package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.command.*;
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSReplacer_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSReplacer_v1_13_R2;
import de.derfrzocker.ore.control.inventorygui.InventoryClickListener;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.ReloadAble;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OreControl extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(LapisSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(EmeraldSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);
    }

    @Getter
    @Setter
    @NonNull
    private static OreControl instance;

    @Getter
    private List<ReloadAble> reloadAbles = new ArrayList<>();

    @Getter
    private ConfigValues configValues;

    @Getter
    private Settings settings;

    private NMSReplacer nmsReplacer;

    private OreControlCommand oreControlCommand = new OreControlCommand();

    @Override
    public void onLoad() {
        checkFile("data/settings.yml");

        String version = getVersion();

        if (version.equalsIgnoreCase("v1_13_R1"))
            nmsReplacer = new NMSReplacer_v1_13_R1();
        else if (version.equalsIgnoreCase("v1_13_R2"))
            nmsReplacer = new NMSReplacer_v1_13_R2();

        if (nmsReplacer == null)
            throw new IllegalStateException("no matching server version found, stop plugin start", new NullPointerException("overrider can't be null"));

        OreControlMessages.getInstance().setFile(Config.getConfig(this, "messages"));
        configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));
        reloadAbles.add(configValues);

        {//  TODO remove in higher version
            File file = new File(getDataFolder(), "data/settings.yml");
            YamlConfiguration yaml = new Config(file);
            if (yaml.contains("defaults.gold.normal") || yaml.contains("defaults.gold.badlands"))
                file.delete();
        }

        settings = new Settings(Config.getConfig(this, "data/settings.yml"));

        instance = this;
    }

    @Override
    public void onEnable() {
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
        oreControlCommand.registerExecuter(new SetCommand(), "set");
        oreControlCommand.registerExecuter(new ReloadCommand(), "reload");
        oreControlCommand.registerExecuter(new SetBiomeCommand(), "setbiome");
        oreControlCommand.registerExecuter(new GuiCommand(), "");

        HelpCommand helpCommand = new HelpCommand();
        oreControlCommand.registerExecuter(helpCommand, null);
        oreControlCommand.registerExecuter(helpCommand, "help");
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
