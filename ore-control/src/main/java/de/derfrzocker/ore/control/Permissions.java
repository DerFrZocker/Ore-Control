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


import de.derfrzocker.spigot.utils.Permission;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
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

    public Permissions(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin cannot be null");

        base = new Permission(null, "ore.control", plugin, false);
        reload = new Permission(base, "reload", plugin, true);
        set = new Permission(base, "set", plugin, true);
        setValue = new Permission(set, "value", plugin, true);
        setBiome = new Permission(set, "biome", plugin, true);
        gui = new Permission(base, "gui", plugin, false);
        template = new Permission(base, "template", plugin, false);
        templateCreate = new Permission(template, "create", plugin, false);
        templateDelete = new Permission(template, "delete", plugin, false);
        value = new Permission(base, "value", plugin, false);
        valueReset = new Permission(value, "reset", plugin, false);
        valueCopy = new Permission(value, "copy", plugin, false);
        configEdit = new Permission(base, "config.edit", plugin, false);
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
