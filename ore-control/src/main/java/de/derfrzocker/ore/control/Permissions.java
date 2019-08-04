package de.derfrzocker.ore.control;


import de.derfrzocker.spigot.utils.Permission;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Permissions {

    public final static Permission BASE_PERMISSION = new Permission(null, "ore.control", OreControl.getInstance(), false);


    public final static Permission RELOAD_PERMISSION = new Permission(BASE_PERMISSION, "reload", OreControl.getInstance(), true);
    public final static Permission SET_PERMISSION = new Permission(BASE_PERMISSION, "set", OreControl.getInstance(), true);
    public final static Permission SET_BIOME_PERMISSION = new Permission(SET_PERMISSION, "biome", OreControl.getInstance(), true);
    public final static Permission OPEN_GUI_PERMISSION = new Permission(BASE_PERMISSION, "gui", OreControl.getInstance(), false);

    public final static Permission TEMPLATE_PERMISSION = new Permission(BASE_PERMISSION, "template", OreControl.getInstance(), false);
    public final static Permission CREATE_TEMPLATE_PERMISSION = new Permission(TEMPLATE_PERMISSION, "create", OreControl.getInstance(), true);
    public final static Permission DELETE_TEMPLATE_PERMISSION = new Permission(TEMPLATE_PERMISSION, "delete", OreControl.getInstance(), false);

    public final static Permission VALUE_PERMISSION = new Permission(BASE_PERMISSION, "value", OreControl.getInstance(), false);
    public final static Permission RESET_VALUE_PERMISSION = new Permission(VALUE_PERMISSION, "reset", OreControl.getInstance(), false);
    public final static Permission COPY_VALUE_PERMISSION = new Permission(VALUE_PERMISSION, "copy", OreControl.getInstance(), false);

    public final static Permission EDIT_CONFIG_PERMISSION = new Permission(BASE_PERMISSION, "config.edit", OreControl.getInstance(), false);

}
