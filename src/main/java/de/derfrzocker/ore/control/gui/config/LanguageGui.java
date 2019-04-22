package de.derfrzocker.ore.control.gui.config;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.gui.BasicGui;
import de.derfrzocker.ore.control.gui.BasicSettings;
import de.derfrzocker.ore.control.gui.utils.InventoryUtil;
import de.derfrzocker.ore.control.utils.Language;
import de.derfrzocker.ore.control.utils.MessageUtil;
import de.derfrzocker.ore.control.utils.MessageValue;
import de.derfrzocker.ore.control.utils.Messages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;


public class LanguageGui extends BasicGui {

    LanguageGui() {
        super(LanguageGuiSettings.getInstance());
        final Language[] languages = Language.values();

        for (int i = 0; i < languages.length; i++)
            addItem(InventoryUtil.calculateSlot(i, LanguageGuiSettings.getInstance().getLanguageGap()), MessageUtil.replaceItemStack(LanguageGuiSettings.getInstance().getLanguageItemStack(languages[i])), new LanguageConsumer(languages[i]));

        addItem(LanguageGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(LanguageGuiSettings.getInstance().getInfoItemStack(),
                new MessageValue("amount", OreControl.getInstance().getConfigValues().getLanguage().getName()),
                new MessageValue("value", OreControl.getInstance().getConfigValues().DEFAULT.defaultLanguage().getName())
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
            Messages.getInstance().reload();
            openSync(event.getWhoClicked(), new ConfigGui().getInventory());
        }
    }

}
