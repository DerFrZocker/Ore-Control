package de.derfrzocker.ore.control;

import de.derfrzocker.spigot.utils.command.HelpConfig;
import de.derfrzocker.spigot.utils.message.MessageKey;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class OreControlMessages implements HelpConfig {

    @NotNull
    private final MessageKey worldConfigNotFound;
    @NotNull
    private final MessageKey worldConfigAlreadyExists;
    @NotNull
    private final MessageKey oreNotFound;
    @NotNull
    private final MessageKey settingNotFound;
    @NotNull
    private final MessageKey biomeNotFound;
    @NotNull
    private final MessageKey oreNotValid;
    @NotNull
    private final MessageKey settingNotValid;
    @NotNull
    private final MessageKey numberNotValid;
    @NotNull
    private final MessageKey numberNotSafe;
    @NotNull
    private final MessageKey numberNotSafeWarning;

    // gui
    @NotNull
    private final MessageKey guiCopySuccess;
    @NotNull
    private final MessageKey guiResetSuccess;
    @NotNull
    private final MessageKey guiAnvilTitle;

    //command messages
    @NotNull
    private final MessageKey commandPlayerOnly;

    // set value messages
    @NotNull
    private final MessageKey commandSetValueUsage;
    @NotNull
    private final MessageKey commandSetValueDescription;
    @NotNull
    private final MessageKey commandSetValueNotEnoughArgs;
    @NotNull
    private final MessageKey commandSetValueSuccess;

    //set biome messages
    @NotNull
    private final MessageKey commandSetBiomeUsage;
    @NotNull
    private final MessageKey commandSetBiomeDescription;
    @NotNull
    private final MessageKey commandSetBiomeNotEnoughArgs;
    @NotNull
    private final MessageKey commandSetBiomeSuccess;

    //create messages
    @NotNull
    private final MessageKey commandCreateUsage;
    @NotNull
    private final MessageKey commandCreateDescription;
    @NotNull
    private final MessageKey commandCreateNotEnoughArgs;
    @NotNull
    private final MessageKey commandCreateSuccess;

    // reload messages
    @NotNull
    private final MessageKey commandReloadUsage;
    @NotNull
    private final MessageKey commandReloadDescription;
    @NotNull
    private final MessageKey commandReloadBegin;
    @NotNull
    private final MessageKey commandReloadEnd;

    // help format
    @NotNull
    private final MessageKey commandHelpSeparatorFormat;
    @NotNull
    private final MessageKey commandHelpHeaderFormat;
    @NotNull
    private final MessageKey commandHelpFooterFormat;
    @NotNull
    private final MessageKey commandHelpPermissionFormat;
    @NotNull
    private final MessageKey commandHelpUsageFormat;
    @NotNull
    private final MessageKey commandHelpDescriptionFormat;
    @NotNull
    private final MessageKey commandHelpShortFormat;

    // help messages
    @NotNull
    private final MessageKey commandHelpUsage;
    @NotNull
    private final MessageKey commandHelpDescription;

    public OreControlMessages(@NotNull final JavaPlugin javaPlugin) {
        Validate.notNull(javaPlugin, "JavaPlugin can not be null");

        worldConfigNotFound = new MessageKey(javaPlugin, "world-config.not-found");
        worldConfigAlreadyExists = new MessageKey(javaPlugin, "world-config.already-exists");
        oreNotFound = new MessageKey(javaPlugin, "ore.not-found");
        settingNotFound = new MessageKey(javaPlugin, "setting.not-found");
        biomeNotFound = new MessageKey(javaPlugin, "biome.not-found");
        oreNotValid = new MessageKey(javaPlugin, "ore.not-valid");
        settingNotValid = new MessageKey(javaPlugin, "setting.not-valid");
        numberNotValid = new MessageKey(javaPlugin, "number.not-valid");
        numberNotSafe = new MessageKey(javaPlugin, "number.not-safe");
        numberNotSafeWarning = new MessageKey(javaPlugin, "number.not-safe-warning");
        guiCopySuccess = new MessageKey(javaPlugin, "gui.copy.success");
        guiResetSuccess = new MessageKey(javaPlugin, "gui.reset.success");
        guiAnvilTitle = new MessageKey(javaPlugin, "gui.anvil.title");
        commandPlayerOnly = new MessageKey(javaPlugin, "command.player-only");
        commandSetValueUsage = new MessageKey(javaPlugin, "command.set.value.usage");
        commandSetValueDescription = new MessageKey(javaPlugin, "command.set.value.description");
        commandSetValueNotEnoughArgs = new MessageKey(javaPlugin, "command.set.value.not-enough-args");
        commandSetValueSuccess = new MessageKey(javaPlugin, "command.set.value.success");
        commandSetBiomeUsage = new MessageKey(javaPlugin, "command.set.biome.usage");
        commandSetBiomeDescription = new MessageKey(javaPlugin, "command.set.biome.description");
        commandSetBiomeNotEnoughArgs = new MessageKey(javaPlugin, "command.set.biome.not-enough-args");
        commandSetBiomeSuccess = new MessageKey(javaPlugin, "command.set.biome.success");
        commandCreateUsage = new MessageKey(javaPlugin, "command.create.usage");
        commandCreateDescription = new MessageKey(javaPlugin, "command.create.description");
        commandCreateNotEnoughArgs = new MessageKey(javaPlugin, "command.create.not-enough-args");
        commandCreateSuccess = new MessageKey(javaPlugin, "command.create.success");
        commandReloadUsage = new MessageKey(javaPlugin, "command.reload.usage");
        commandReloadDescription = new MessageKey(javaPlugin, "command.reload.description");
        commandReloadBegin = new MessageKey(javaPlugin, "command.reload.begin");
        commandReloadEnd = new MessageKey(javaPlugin, "command.reload.end");
        commandHelpSeparatorFormat = new MessageKey(javaPlugin, "command.help.separator-format");
        commandHelpHeaderFormat = new MessageKey(javaPlugin, "command.help.header-format");
        commandHelpFooterFormat = new MessageKey(javaPlugin, "command.help.footer-format");
        commandHelpPermissionFormat = new MessageKey(javaPlugin, "command.help.permission-format");
        commandHelpUsageFormat = new MessageKey(javaPlugin, "command.help.usage-format");
        commandHelpDescriptionFormat = new MessageKey(javaPlugin, "command.help.description-format");
        commandHelpShortFormat = new MessageKey(javaPlugin, "command.help.short-format");
        commandHelpUsage = new MessageKey(javaPlugin, "command.help.usage");
        commandHelpDescription = new MessageKey(javaPlugin, "command.help.description");
    }

    @NotNull
    public MessageKey getWorldConfigNotFoundMessage() {
        return worldConfigNotFound;
    }

    @NotNull
    public MessageKey getWorldConfigAlreadyExistsMessage() {
        return worldConfigAlreadyExists;
    }

    @NotNull
    public MessageKey getOreNotFoundMessage() {
        return oreNotFound;
    }

    @NotNull
    public MessageKey getSettingNotFoundMessage() {
        return settingNotFound;
    }

    @NotNull
    public MessageKey getBiomeNotFoundMessage() {
        return biomeNotFound;
    }

    @NotNull
    public MessageKey getOreNotValidMessage() {
        return oreNotValid;
    }

    @NotNull
    public MessageKey getSettingNotValidMessage() {
        return settingNotValid;
    }

    @NotNull
    public MessageKey getNumberNotValidMessage() {
        return numberNotValid;
    }

    @NotNull
    public MessageKey getNumberNotSafeMessage() {
        return numberNotSafe;
    }

    @NotNull
    public MessageKey getNumberNotSafeWarningMessage() {
        return numberNotSafeWarning;
    }


    //gui
    @NotNull
    public MessageKey getGuiCopySuccessMessage() {
        return guiCopySuccess;
    }

    @NotNull
    public MessageKey getGuiResetSuccessMessage() {
        return guiResetSuccess;
    }

    @NotNull
    public MessageKey getGuiAnvilTitleMessage() {
        return guiAnvilTitle;
    }


    // command messages
    @NotNull
    public MessageKey getCommandPlayerOnlyMessage() {
        return commandPlayerOnly;
    }


    // set value messages
    @NotNull
    public MessageKey getCommandSetValueUsageMessage() {
        return commandSetValueUsage;
    }

    @NotNull
    public MessageKey getCommandSetValueDescriptionMessage() {
        return commandSetValueDescription;
    }

    @NotNull
    public MessageKey getCommandSetValueNotEnoughArgsMessage() {
        return commandSetValueNotEnoughArgs;
    }

    @NotNull
    public MessageKey getCommandSetValueSuccessMessage() {
        return commandSetValueSuccess;
    }


    // set biome messages
    @NotNull
    public MessageKey getCommandSetBiomeUsageMessage() {
        return commandSetBiomeUsage;
    }

    @NotNull
    public MessageKey getCommandSetBiomeDescriptionMessage() {
        return commandSetBiomeDescription;
    }

    @NotNull
    public MessageKey getCommandSetBiomeNotEnoughArgsMessage() {
        return commandSetBiomeNotEnoughArgs;
    }

    @NotNull
    public MessageKey getCommandSetBiomeSuccessMessage() {
        return commandSetBiomeSuccess;
    }

    // create messages
    @NotNull
    public MessageKey getCommandCreateUsageMessage() {
        return commandCreateUsage;
    }

    @NotNull
    public MessageKey getCommandCreateDescriptionMessage() {
        return commandCreateDescription;
    }

    @NotNull
    public MessageKey getCommandCreateNotEnoughArgsMessage() {
        return commandCreateNotEnoughArgs;
    }

    @NotNull
    public MessageKey getCommandCreateSuccessMessage() {
        return commandCreateSuccess;
    }

    // reload messages
    @NotNull
    public MessageKey getCommandReloadUsageMessage() {
        return commandReloadUsage;
    }

    @NotNull
    public MessageKey getCommandReloadDescriptionMessage() {
        return commandReloadDescription;
    }

    @NotNull
    public MessageKey getCommandReloadBeginMessage() {
        return commandReloadBegin;
    }

    @NotNull
    public MessageKey getCommandReloadEndMessage() {
        return commandReloadEnd;
    }

    // help format
    @NotNull
    @Override
    public MessageKey getSeparatorMessageFormat() {
        return commandHelpSeparatorFormat;
    }

    @NotNull
    @Override
    public MessageKey getHeaderMessageFormat() {
        return commandHelpHeaderFormat;
    }

    @NotNull
    @Override
    public MessageKey getFooterMessageFormat() {
        return commandHelpFooterFormat;
    }

    @NotNull
    @Override
    public MessageKey getPermissionMessageFormat() {
        return commandHelpPermissionFormat;
    }

    @NotNull
    @Override
    public MessageKey getUsageMessageFormat() {
        return commandHelpUsageFormat;
    }

    @NotNull
    @Override
    public MessageKey getDescriptionMessageFormat() {
        return commandHelpDescriptionFormat;
    }

    @NotNull
    @Override
    public MessageKey getShortHelpMessageFormat() {
        return commandHelpShortFormat;
    }

    // help messages
    @NotNull
    public MessageKey getCommandHelpUsageMessage() {
        return commandHelpUsage;
    }

    @NotNull
    public MessageKey getCommandHelpDescriptionMessage() {
        return commandHelpDescription;
    }

}
