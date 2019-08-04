package de.derfrzocker.ore.control;

import de.derfrzocker.spigot.utils.message.MessageKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OreControlMessages {

    //general command messages
    public final static MessageKey PLAYER_ONLY_COMMAND = new MessageKey(OreControl.getInstance(), "command.player_only_command");

    //reload command
    public final static MessageKey RELOAD_BEGIN = new MessageKey(OreControl.getInstance(), "command.reload.begin");
    public final static MessageKey RELOAD_END = new MessageKey(OreControl.getInstance(), "command.reload.end");

    //set command
    public final static MessageKey SET_NOT_ENOUGH_ARGS = new MessageKey(OreControl.getInstance(), "command.set.not_enough_args");
    public final static MessageKey SET_CONFIG_NOT_FOUND = new MessageKey(OreControl.getInstance(), "command.set.config_not_found");
    public final static MessageKey SET_NO_NUMBER = new MessageKey(OreControl.getInstance(), "command.set.no_number");
    public final static MessageKey SET_ORE_NOT_FOUND = new MessageKey(OreControl.getInstance(), "command.set.ore_not_found");
    public final static MessageKey SET_SUCCESS = new MessageKey(OreControl.getInstance(), "command.set.success");
    public final static MessageKey SET_SETTING_NOT_FOUND = new MessageKey(OreControl.getInstance(), "command.set.setting_not_found");
    public final static MessageKey SET_SETTING_NOT_VALID = new MessageKey(OreControl.getInstance(), "command.set.setting_not_valid");
    public final static MessageKey SET_NOT_SAFE = new MessageKey(OreControl.getInstance(), "command.set.not_safe");
    public final static MessageKey SET_NOT_SAFE_WARNING = new MessageKey(OreControl.getInstance(), "command.set.not_safe_warning");

    //setbiome command
    public final static MessageKey SET_BIOME_NOT_ENOUGH_ARGS = new MessageKey(OreControl.getInstance(), "command.set.biome.not_enough_args");
    public final static MessageKey SET_BIOME_NOT_FOUND = new MessageKey(OreControl.getInstance(), "command.set.biome.biome_not_found");
    public final static MessageKey SET_BIOME_ORE_NOT_VALID = new MessageKey(OreControl.getInstance(), "command.set.biome.ore_not_valid");

    //help command
    public final static MessageKey HELP_HEADER = new MessageKey(OreControl.getInstance(), "command.help.header");
    public final static MessageKey HELP_FOOTER = new MessageKey(OreControl.getInstance(), "command.help.footer");
    public final static MessageKey HELP_SEPARATOR = new MessageKey(OreControl.getInstance(), "command.help.separator");
    public final static MessageKey HELP_SET_COMMAND = new MessageKey(OreControl.getInstance(), "command.help.set.command");
    public final static MessageKey HELP_SET_DESCRIPTION = new MessageKey(OreControl.getInstance(), "command.help.set.description");
    public final static MessageKey HELP_SET_BIOME_COMMAND = new MessageKey(OreControl.getInstance(), "command.help.set.biome.command");
    public final static MessageKey HELP_SET_BIOME_DESCRIPTION = new MessageKey(OreControl.getInstance(), "command.help.set.biome.description");
    public final static MessageKey HELP_RELOAD_COMMAND = new MessageKey(OreControl.getInstance(), "command.help.reload.command");
    public final static MessageKey HELP_RELOAD_DESCRIPTION = new MessageKey(OreControl.getInstance(), "command.help.reload.description");
    public final static MessageKey HELP_CREATE_COMMAND = new MessageKey(OreControl.getInstance(), "command.help.create.command");
    public final static MessageKey HELP_CREATE_DESCRIPTION = new MessageKey(OreControl.getInstance(), "command.help.create.description");
    public final static MessageKey HELP_COMMAND = new MessageKey(OreControl.getInstance(), "command.help.help.command");
    public final static MessageKey HELP_DESCRIPTION = new MessageKey(OreControl.getInstance(), "command.help.help.description");

    //create command
    public final static MessageKey CREATE_NOT_ENOUGH_ARGS = new MessageKey(OreControl.getInstance(), "command.create.not_enough_args");
    public final static MessageKey CREATE_NAME_ALREADY_EXIST = new MessageKey(OreControl.getInstance(), "command.create.name_already_exist");
    public final static MessageKey CREATE_SUCCESS = new MessageKey(OreControl.getInstance(), "command.create.success");

    //anvil gui
    public final static MessageKey ANVIL_NAME_ALREADY_EXISTS = new MessageKey(OreControl.getInstance(), "gui.anvil.name_already_exist");
    public final static MessageKey ANVIL_TITLE = new MessageKey(OreControl.getInstance(), "gui.anvil.title");

    //copy / reset
    public final static MessageKey COPY_VALUE_SUCCESS = new MessageKey(OreControl.getInstance(), "gui.copy_value_success");
    public final static MessageKey RESET_VALUE_SUCCESS = new MessageKey(OreControl.getInstance(), "gui.reset_value_success");

}
