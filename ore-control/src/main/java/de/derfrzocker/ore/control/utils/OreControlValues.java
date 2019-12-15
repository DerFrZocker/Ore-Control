package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.ConfigValues;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class OreControlValues {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;
    @NotNull
    private final JavaPlugin javaPlugin;
    @NotNull
    private final ConfigValues configValues;
    @NotNull
    private final OreControlMessages oreControlMessages;
    @NotNull
    private final Permissions permissions;

    public OreControlValues(@NotNull final Supplier<OreControlService> serviceSupplier, @NotNull final JavaPlugin javaPlugin, @NotNull final ConfigValues configValues, @NotNull final OreControlMessages oreControlMessages, @NotNull final Permissions permissions) {
        Validate.notNull(serviceSupplier, "Service supplier can not be null");
        Validate.notNull(javaPlugin, "JavaPlugin can not be null");
        Validate.notNull(configValues, "ConfigValues can not be null");
        Validate.notNull(oreControlMessages, "OreControlMessages can not be null");
        Validate.notNull(permissions, "Permissions can not be null");

        this.serviceSupplier = serviceSupplier;
        this.javaPlugin = javaPlugin;
        this.configValues = configValues;
        this.oreControlMessages = oreControlMessages;
        this.permissions = permissions;
    }

    @NotNull
    public Supplier<OreControlService> getServiceSupplier() {
        return serviceSupplier;
    }

    @NotNull
    public OreControlService getService() {
        return getServiceSupplier().get();
    }

    @NotNull
    public JavaPlugin getJavaPlugin() {
        return javaPlugin;
    }

    @NotNull
    public ConfigValues getConfigValues() {
        return configValues;
    }

    @NotNull
    public OreControlMessages getOreControlMessages() {
        return oreControlMessages;
    }

    @NotNull
    public Permissions getPermissions() {
        return permissions;
    }

}
