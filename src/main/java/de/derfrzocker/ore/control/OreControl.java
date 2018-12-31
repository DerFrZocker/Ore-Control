package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.command.HelpCommand;
import de.derfrzocker.ore.control.command.OreGeneratorCommand;
import de.derfrzocker.ore.control.command.ReloadCommand;
import de.derfrzocker.ore.control.command.SetCommand;
import de.derfrzocker.ore.control.impl.*;
import de.derfrzocker.ore.control.impl.dao.WorldOreConfigYamlDao;
import de.derfrzocker.ore.control.impl.v1_13_R1.NMSReplacer_v1_13_R1;
import de.derfrzocker.ore.control.impl.v1_13_R2.NMSReplacer_v1_13_R2;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.ReloadAble;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OreControl extends JavaPlugin {

    @Getter
    private static OreControl instance;

    @Getter
    private List<ReloadAble> reloadAbles = new ArrayList<>();

    @Getter
    private ConfigValues configValues;

    @Getter
    private Settings settings;

    private OreGeneratorCommand oreGeneratorCommand = new OreGeneratorCommand();

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

        //  configValues = new ConfigValues(new File(getDataFolder(), "config.yml"));
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
    }

    private void registerCommands() {
        getCommand("orecontrol").setExecutor(oreGeneratorCommand);
        oreGeneratorCommand.registerExecuter(new SetCommand(), "set");
        oreGeneratorCommand.registerExecuter(new ReloadCommand(), "reload");
        HelpCommand helpCommand = new HelpCommand();
        oreGeneratorCommand.registerExecuter(helpCommand, null);
        oreGeneratorCommand.registerExecuter(helpCommand, "help");
    }

    public static OreControlService getService() {
        OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        if (service == null)
            throw new IllegalStateException("The Bukkit Service have no " + OreControlService.class.getName() + " registered", new NullPointerException("service can't be null"));

        return service;
    }

}
