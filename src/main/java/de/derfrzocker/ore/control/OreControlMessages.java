package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.utils.MessageKey;
import de.derfrzocker.ore.control.utils.Messages;
import lombok.Getter;

public class OreControlMessages extends Messages {

    @Getter
    private static final OreControlMessages instance = new OreControlMessages();

    //general command messages
    public final static MessageKey PLAYER_ONLY_COMMAND = new MessageKey(getInstance(), "command.player_only_command");

    //reload command
    public final static MessageKey RELOAD_BEGIN = new MessageKey(getInstance(), "command.reload.begin");
    public final static MessageKey RELOAD_END = new MessageKey(getInstance(), "command.reload.end");

    //set command
    public final static MessageKey SET_NOT_ENOUGH_ARGS = new MessageKey(getInstance(), "command.set.not_enough_args");
    public final static MessageKey SET_CONFIG_NOT_FOUND = new MessageKey(getInstance(), "command.set.config_not_found");
    public final static MessageKey SET_NO_NUMBER = new MessageKey(getInstance(), "command.set.no_number");
    public final static MessageKey SET_ORE_NOT_FOUND = new MessageKey(getInstance(), "command.set.ore_not_found");
    public final static MessageKey SET_SUCCESS = new MessageKey(getInstance(), "command.set.success");
    public final static MessageKey SET_SETTING_NOT_FOUND = new MessageKey(getInstance(), "command.set.setting_not_found");
    public final static MessageKey SET_SETTING_NOT_VALID = new MessageKey(getInstance(), "command.set.setting_not_valid");
    public final static MessageKey SET_NOT_SAVE = new MessageKey(getInstance(), "command.set.not_save");
    public final static MessageKey SET_NOT_SAVE_WARNING = new MessageKey(getInstance(), "command.set.not_save_warning");

    //setbiome command
    public final static MessageKey SET_BIOME_NOT_ENOUGH_ARGS = new MessageKey(getInstance(), "command.set.biome.not_enough_args");
    public final static MessageKey SET_BIOME_NOT_FOUND = new MessageKey(getInstance(), "command.set.biome.biome_not_found");
    public final static MessageKey SET_BIOME_ORE_NOT_VALID = new MessageKey(getInstance(), "command.set.biome.ore_not_valid");

    //help command
    public final static MessageKey HELP_HEADER = new MessageKey(getInstance(), "command.help.header");
    public final static MessageKey HELP_FOOTER = new MessageKey(getInstance(), "command.help.footer");
    public final static MessageKey HELP_SEPARATOR = new MessageKey(getInstance(), "command.help.separator");
    public final static MessageKey HELP_SET_COMMAND = new MessageKey(getInstance(), "command.help.set.command");
    public final static MessageKey HELP_SET_DESCRIPTION = new MessageKey(getInstance(), "command.help.set.description");
    public final static MessageKey HELP_SET_BIOME_COMMAND = new MessageKey(getInstance(), "command.help.set.biome.command");
    public final static MessageKey HELP_SET_BIOME_DESCRIPTION = new MessageKey(getInstance(), "command.help.set.biome.description");
    public final static MessageKey HELP_RELOAD_COMMAND = new MessageKey(getInstance(), "command.help.reload.command");
    public final static MessageKey HELP_RELOAD_DESCRIPTION = new MessageKey(getInstance(), "command.help.reload.description");
    public final static MessageKey HELP_CREATE_COMMAND = new MessageKey(getInstance(), "command.help.create.command");
    public final static MessageKey HELP_CREATE_DESCRIPTION = new MessageKey(getInstance(), "command.help.create.description");
    public final static MessageKey HELP_COMMAND = new MessageKey(getInstance(), "command.help.help.command");
    public final static MessageKey HELP_DESCRIPTION = new MessageKey(getInstance(), "command.help.help.description");

    //create command
    public final static MessageKey CREATE_NOT_ENOUGH_ARGS = new MessageKey(getInstance(), "command.create.not_enough_args");
    public final static MessageKey CREATE_NAME_ALREADY_EXIST = new MessageKey(getInstance(), "command.create.name_already_exist");
    public final static MessageKey CREATE_SUCCESS = new MessageKey(getInstance(), "command.create.success");

    //anvil gui
    public final static MessageKey ANVIL_NAME_ALREADY_EXISTS = new MessageKey(getInstance(), "gui.anvil.name_already_exist");
    public final static MessageKey ANVIL_TITLE = new MessageKey(getInstance(), "gui.anvil.title");

    public OreControlMessages() {
        super(OreControl.getInstance(), () -> OreControl.getInstance().getConfigValues().getLanguage());
    }
}
