/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.LanguageGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class LanguageGui extends BasicGui {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;

    LanguageGui(@NotNull GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues) {
        super(oreControlValues.getPlugin(), guiSettings.getLanguageGuiSettings());

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;

        final LanguageGuiSettings languageGuiSettings = guiSettings.getLanguageGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();

        addDecorations();

        for (Language language : Language.values()) {
            addItem(languageGuiSettings.getLanguageSlot(language), MessageUtil.replaceItemStack(plugin, languageGuiSettings.getLanguageItemStack(language)), new LanguageConsumer(language));
        }

        addItem(languageGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, languageGuiSettings.getBackItemStack()), event -> new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked()));

        addItem(languageGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, languageGuiSettings.getInfoItemStack(),
                new MessageValue("amount", oreControlValues.getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("default", oreControlValues.getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ));
    }

    private final class LanguageConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Language language;

        private LanguageConsumer(@NotNull final Language language) {
            this.language = language;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            oreControlValues.getConfigValues().SET.setLanguage(language);
            new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked());
        }

    }

}
