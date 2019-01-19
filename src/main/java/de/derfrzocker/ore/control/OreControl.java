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

    private OreControlCommand oreControlCommand = new OreControlCommand();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(WorldOreConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(LapisSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(EmeraldSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(OreSettingsYamlImpl.class);
        ConfigurationSerialization.registerClass(BiomeOreSettingsYamlImpl.class);

        configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));
        reloadAbles.add(configValues);

        {//  TODO remove in higher version
            File file = new File(getDataFolder(), "data/settings.yml");
            YamlConfiguration yaml = new Config(file);
            if (yaml.contains("defaults.gold.normal") || yaml.contains("defaults.gold.badlands"))
                file.delete();
        }

        settings = new Settings(Config.getConfig(this, "data/settings.yml"));
        OreControlMessages.getInstance().setFile(Config.getConfig(this, "messages"));

        registerCommands();

        String version = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);

        NMSReplacer overrider = null;

        if (version.equalsIgnoreCase("v1_13_R1"))
            overrider = new NMSReplacer_v1_13_R1();
        else if (version.equalsIgnoreCase("v1_13_R2"))
            overrider = new NMSReplacer_v1_13_R2();

        if (overrider == null)
            throw new IllegalStateException("no matching server version found", new NullPointerException("overrider can't be null"));

        Bukkit.getServicesManager().register(OreControlService.class,
                new OreControlServiceImpl(
                        overrider,
                        new WorldOreConfigYamlDao(new File(getDataFolder(), "data/world_ore_configs.yml"))),
                this, ServicePriority.Normal);

        overrider.replaceNMS();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

    private void registerCommands() {
        getCommand("orecontrol").setExecutor(oreControlCommand);
        oreControlCommand.registerExecuter(new SetCommand(), "set");
        oreControlCommand.registerExecuter(new ReloadCommand(), "reload");
        oreControlCommand.registerExecuter(new SetBiomeCommand(), "setbiome");
        HelpCommand helpCommand = new HelpCommand();
        oreControlCommand.registerExecuter(helpCommand, null);
        oreControlCommand.registerExecuter(helpCommand, "help");
        oreControlCommand.registerExecuter(new GuiCommand(), "");
    }

    public static OreControlService getService() {
        OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null)
            throw new IllegalStateException("The Bukkit Service have no " + OreControlService.class.getName() + " registered", new NullPointerException("service can't be null"));

        return service;
    }

}
