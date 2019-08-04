package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.InventoryUtil;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;


public class LanguageGui extends BasicGui {

    LanguageGui() {
        super(OreControl.getInstance(), LanguageGuiSettings.getInstance());
        final Language[] languages = Language.values();

        for (int i = 0; i < languages.length; i++)
            addItem(InventoryUtil.calculateSlot(i, LanguageGuiSettings.getInstance().getLanguageGap()), MessageUtil.replaceItemStack(OreControl.getInstance(), LanguageGuiSettings.getInstance().getLanguageItemStack(languages[i])), new LanguageConsumer(languages[i]));

        addItem(LanguageGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), LanguageGuiSettings.getInstance().getInfoItemStack(),
                new MessageValue("amount", OreControl.getInstance().getConfigValues().getLanguage().getNames()[0]),
                new MessageValue("value", OreControl.getInstance().getConfigValues().DEFAULT.defaultLanguage().getNames()[0])
        ));
    }

    private static final class LanguageGuiSettings extends BasicSettings {

        private static LanguageGuiSettings instance = null;

        private static LanguageGuiSettings getInstance() {
            if (instance == null)
                instance = new LanguageGuiSettings();

            return instance;
        }

        private LanguageGuiSettings() {
            super(OreControl.getInstance(), "data/language_gui.yml");
        }

        private ItemStack getLanguageItemStack(final Language language) {
            return getYaml().getItemStack("language." + language);
        }

        private int getLanguageGap() {
            return getYaml().getInt("inventory.language_gap");
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }

    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class LanguageConsumer implements Consumer<InventoryClickEvent> {

        private final Language language;

        @Override
        public void accept(final InventoryClickEvent event) {
            OreControl.getInstance().getConfigValues().SET.setLanguage(language);
            new ConfigGui().openSync(event.getWhoClicked());
        }
    }

}
