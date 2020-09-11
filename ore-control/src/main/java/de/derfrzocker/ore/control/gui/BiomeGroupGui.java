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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BiomeGroupGui extends PageGui<BiomeGroupGui.BiomeGroup> {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Dimension dimension;
    @NotNull
    private final BiomeGroups biomeGroups;

    BiomeGroupGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension) {
        super(oreControlValues.getPlugin(), guiSettings.getBiomeGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = dimension;
        this.biomeGroups = new BiomeGroups(oreControlValues.getPlugin());

        final BiomeGuiSettings biomeGuiSettings = guiSettings.getBiomeGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();

        addDecorations();
        init(biomeGroups.getGroups(), BiomeGroup[]::new, this::getItemStack, this::handleNormalClick);

        addItem(biomeGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, biomeGuiSettings.getInfoItemStack(), getMessagesValues()));
        addItem(biomeGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, biomeGuiSettings.getBackItemStack()), event -> new WorldConfigGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension).openSync(event.getWhoClicked()));
        addItem(biomeGuiSettings.getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(plugin, biomeGuiSettings.getBiomeItemStack()), event -> new BiomeGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension).openSync(event.getWhoClicked()));
    }

    private ItemStack getItemStack(@NotNull final BiomeGroup biomeGroup) {
        return MessageUtil.replaceItemStack(getPlugin(), guiSettings.getBiomeGuiSettings().getBiomeItemStack(biomeGroup.getName().toUpperCase()));
    }

    private void handleNormalClick(@NotNull final BiomeGroup biomeGroup, @NotNull final InventoryClickEvent event) {
        new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup).openSync(event.getWhoClicked());
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

    private class BiomeGroups implements ReloadAble {

        private final static String FILE = "data/gui/biome-groups.yml";

        @NotNull
        private final Plugin plugin;
        @NotNull
        private YamlConfiguration yaml;

        private BiomeGroups(@NotNull final Plugin plugin) {
            Validate.notNull(plugin, "Plugin cannot be null");

            this.plugin = plugin;

            yaml = Config.getConfig(plugin, FILE);
            RELOAD_ABLES.add(this);
        }

        private BiomeGroup[] getGroups() {
            final ConfigurationSection section = yaml.getConfigurationSection("biome-groups");

            return section.getKeys(false).stream().map(string -> {
                final Set<Biome> biomeSet = new HashSet<>();

                final List<String> stringList = section.getStringList(string);

                stringList.forEach(biomeName -> OreControlUtil.getBiome(biomeName, false, oreControlValues.getVersion()).ifPresent(biomeSet::add));

                return new BiomeGroup(string, biomeSet);
            }).toArray(BiomeGroup[]::new);
        }

        @Override
        public void reload() {
            yaml = new Config(Objects.requireNonNull(plugin.getResource(FILE), "InputStream cannot be null"));
        }

    }

    static final class BiomeGroup {

        @NotNull
        private final String name;
        @NotNull
        private final Set<Biome> biomes;

        BiomeGroup(@NotNull final String name, @NotNull final Set<Biome> biomes) {
            Validate.notNull(name, "Name cannot be null");
            Validate.notNull(biomes, "Biomes cannot be null");

            this.name = name;
            this.biomes = biomes;
        }

        @NotNull
        Set<Biome> getBiomes() {
            return biomes;
        }

        @NotNull
        String getName() {
            return name;
        }

    }

}
