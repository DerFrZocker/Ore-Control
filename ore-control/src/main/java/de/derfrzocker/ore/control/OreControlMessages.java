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

import de.derfrzocker.spigot.utils.command.HelpConfig;
import de.derfrzocker.spigot.utils.message.MessageKey;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
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

    // Welcome message
    @NotNull
    private final MessageKey buttonOpenString;
    @NotNull
    private final MessageKey buttonCloseString;
    @NotNull
    private final MessageKey welcomeHeader;
    @NotNull
    private final MessageKey foundBug;
    @NotNull
    private final MessageKey featureRequest;
    @NotNull
    private final MessageKey support;
    @NotNull
    private final MessageKey supportMyWork;
    @NotNull
    private final MessageKey notShowAgain;
    @NotNull
    private final MessageKey notShowAgainSuccess;
    @NotNull
    private final MessageKey clickMe;
    @NotNull
    private final MessageKey rating;
    @NotNull
    private final MessageKey donation;

    public OreControlMessages(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");

        worldConfigNotFound = new MessageKey(plugin, "world-config.not-found");
        worldConfigAlreadyExists = new MessageKey(plugin, "world-config.already-exists");
        oreNotFound = new MessageKey(plugin, "ore.not-found");
        settingNotFound = new MessageKey(plugin, "setting.not-found");
        biomeNotFound = new MessageKey(plugin, "biome.not-found");
        oreNotValid = new MessageKey(plugin, "ore.not-valid");
        settingNotValid = new MessageKey(plugin, "setting.not-valid");
        numberNotValid = new MessageKey(plugin, "number.not-valid");
        numberNotSafe = new MessageKey(plugin, "number.not-safe");
        numberNotSafeWarning = new MessageKey(plugin, "number.not-safe-warning");
        guiCopySuccess = new MessageKey(plugin, "gui.copy.success");
        guiResetSuccess = new MessageKey(plugin, "gui.reset.success");
        guiAnvilTitle = new MessageKey(plugin, "gui.anvil.title");
        commandPlayerOnly = new MessageKey(plugin, "command.player-only");
        commandSetValueUsage = new MessageKey(plugin, "command.set.value.usage");
        commandSetValueDescription = new MessageKey(plugin, "command.set.value.description");
        commandSetValueNotEnoughArgs = new MessageKey(plugin, "command.set.value.not-enough-args");
        commandSetValueSuccess = new MessageKey(plugin, "command.set.value.success");
        commandSetBiomeUsage = new MessageKey(plugin, "command.set.biome.usage");
        commandSetBiomeDescription = new MessageKey(plugin, "command.set.biome.description");
        commandSetBiomeNotEnoughArgs = new MessageKey(plugin, "command.set.biome.not-enough-args");
        commandSetBiomeSuccess = new MessageKey(plugin, "command.set.biome.success");
        commandCreateUsage = new MessageKey(plugin, "command.create.usage");
        commandCreateDescription = new MessageKey(plugin, "command.create.description");
        commandCreateNotEnoughArgs = new MessageKey(plugin, "command.create.not-enough-args");
        commandCreateSuccess = new MessageKey(plugin, "command.create.success");
        commandReloadUsage = new MessageKey(plugin, "command.reload.usage");
        commandReloadDescription = new MessageKey(plugin, "command.reload.description");
        commandReloadBegin = new MessageKey(plugin, "command.reload.begin");
        commandReloadEnd = new MessageKey(plugin, "command.reload.end");
        commandHelpSeparatorFormat = new MessageKey(plugin, "command.help.separator-format");
        commandHelpHeaderFormat = new MessageKey(plugin, "command.help.header-format");
        commandHelpFooterFormat = new MessageKey(plugin, "command.help.footer-format");
        commandHelpPermissionFormat = new MessageKey(plugin, "command.help.permission-format");
        commandHelpUsageFormat = new MessageKey(plugin, "command.help.usage-format");
        commandHelpDescriptionFormat = new MessageKey(plugin, "command.help.description-format");
        commandHelpShortFormat = new MessageKey(plugin, "command.help.short-format");
        commandHelpUsage = new MessageKey(plugin, "command.help.usage");
        commandHelpDescription = new MessageKey(plugin, "command.help.description");
        buttonOpenString = new MessageKey(plugin, "welcome.button-open-string");
        buttonCloseString = new MessageKey(plugin, "welcome.button-close-string");
        welcomeHeader = new MessageKey(plugin, "welcome.header");
        foundBug = new MessageKey(plugin, "welcome.found-bug");
        featureRequest = new MessageKey(plugin, "welcome.feature-request");
        support = new MessageKey(plugin, "welcome.support");
        supportMyWork = new MessageKey(plugin, "welcome.support-my-work");
        notShowAgain = new MessageKey(plugin, "welcome.not-show-again");
        notShowAgainSuccess = new MessageKey(plugin, "welcome.not-show-again-success");
        clickMe = new MessageKey(plugin, "welcome.click-me");
        rating = new MessageKey(plugin, "welcome.rating");
        donation = new MessageKey(plugin, "welcome.donation");
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

    // Welcome message
    @NotNull
    public MessageKey getButtonCloseString() {
        return buttonCloseString;
    }

    @NotNull
    public MessageKey getButtonOpenString() {
        return buttonOpenString;
    }

    @NotNull
    public MessageKey getWelcomeHeader() {
        return welcomeHeader;
    }

    @NotNull
    public MessageKey getNotShowAgain() {
        return notShowAgain;
    }

    @NotNull
    public MessageKey getSupportMyWork() {
        return supportMyWork;
    }

    @NotNull
    public MessageKey getSupport() {
        return support;
    }

    @NotNull
    public MessageKey getFeatureRequest() {
        return featureRequest;
    }

    @NotNull
    public MessageKey getFoundBug() {
        return foundBug;
    }

    @NotNull
    public MessageKey getClickMe() {
        return clickMe;
    }

    @NotNull
    public MessageKey getRating() {
        return rating;
    }

    @NotNull
    public MessageKey getDonation() {
        return donation;
    }

    @NotNull
    public MessageKey getNotShowAgainSuccess() {
        return notShowAgainSuccess;
    }
}
