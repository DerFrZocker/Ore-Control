package de.derfrzocker.ore.control;


import de.derfrzocker.spigot.utils.Permission;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class Permissions {

    @NotNull
    private final Permission base;
    @NotNull
    private final Permission reload;
    @NotNull
    private final Permission set;
    @NotNull
    private final Permission setValue;
    @NotNull
    private final Permission setBiome;
    @NotNull
    private final Permission gui;
    @NotNull
    private final Permission template;
    @NotNull
    private final Permission templateCreate;
    @NotNull
    private final Permission templateDelete;
    @NotNull
    private final Permission value;
    @NotNull
    private final Permission valueReset;
    @NotNull
    private final Permission valueCopy;
    @NotNull
    private final Permission configEdit;

    public Permissions(@NotNull final JavaPlugin javaPlugin) {
        Validate.notNull(javaPlugin, "JavaPlugin can not be null");

        base = new Permission(null, "ore.control", javaPlugin, false);
        reload = new Permission(base, "reload", javaPlugin, true);
        set = new Permission(base, "set", javaPlugin, true);
        setValue = new Permission(set, "value", javaPlugin, true);
        setBiome = new Permission(set, "biome", javaPlugin, true);
        gui = new Permission(base, "gui", javaPlugin, false);
        template = new Permission(base, "template", javaPlugin, false);
        templateCreate = new Permission(template, "create", javaPlugin, false);
        templateDelete = new Permission(template, "delete", javaPlugin, false);
        value = new Permission(base, "value", javaPlugin, false);
        valueReset = new Permission(value, "reset", javaPlugin, false);
        valueCopy = new Permission(value, "copy", javaPlugin, false);
        configEdit = new Permission(base, "config.edit", javaPlugin, false);
    }

    @NotNull
    public Permission getBasePermission() {
        return base;
    }

    @NotNull
    public Permission getReloadPermission() {
        return reload;
    }

    @NotNull
    public Permission getSetPermission() {
        return set;
    }

    @NotNull
    public Permission getSetValuePermission() {
        return setValue;
    }

    @NotNull
    public Permission getSetBiomePermission() {
        return setBiome;
    }

    @NotNull
    public Permission getGuiPermission() {
        return gui;
    }

    @NotNull
    public Permission getTemplatePermission() {
        return template;
    }

    @NotNull
    public Permission getTemplateCreatePermission() {
        return templateCreate;
    }

    @NotNull
    public Permission getTemplateDeletePermission() {
        return templateDelete;
    }

    @NotNull
    public Permission getValuePermission() {
        return value;
    }

    @NotNull
    public Permission getValueResetPermission() {
        return valueReset;
    }

    @NotNull
    public Permission getValueCopyPermission() {
        return valueCopy;
    }

    @NotNull
    public Permission getConfigEditPermission() {
        return configEdit;
    }

}
