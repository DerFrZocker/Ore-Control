package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.gui.PageGui;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeGroupGui extends PageGui<BiomeGroupGui.BiomeGroup> {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    BiomeGroupGui(final WorldOreConfig worldOreConfig) {
        this.worldOreConfig = worldOreConfig;
        init(BiomeGroups.getInstance().getGroups(), BiomeGroup[]::new, BiomeGui.BiomeGuiSettings.getInstance(), this::getItemStack, this::handleNormalClick);

        addItem(BiomeGui.BiomeGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(BiomeGui.BiomeGuiSettings.getInstance().getBackItemStack()), event -> openSync(event.getWhoClicked(), new WorldConfigGui(worldOreConfig, event.getWhoClicked()).getInventory()));
        addItem(BiomeGui.BiomeGuiSettings.getInstance().getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(BiomeGui.BiomeGuiSettings.getInstance().getBiomeItemStack()), event -> openSync(event.getWhoClicked(), new BiomeGroupGui(worldOreConfig).getInventory()));
    }

    private ItemStack getItemStack(final BiomeGroup biomeGroup) {
        return MessageUtil.replaceItemStack(BiomeGui.BiomeGuiSettings.getInstance().getBiomeItemStack(biomeGroup.getName().toUpperCase()));
    }

    private void handleNormalClick(final BiomeGroup biomeGroup, final InventoryClickEvent event) {
        openSync(event.getWhoClicked(), new OreGui(worldOreConfig, biomeGroup).getInventory());
    }

    private static class BiomeGroups implements ReloadAble {

        private final static String FILE = "data/biome_groups.yml";

        private static BiomeGroups instance;

        private static BiomeGroups getInstance() {
            if (instance != null)
                return instance;

            instance = new BiomeGroups();

            return instance;
        }

        private YamlConfiguration yaml;

        private BiomeGroups() {
            yaml = Config.getConfig(OreControl.getInstance(), FILE);
            RELOAD_ABLES.add(this);
        }

        private BiomeGroup[] getGroups() {
            final ConfigurationSection section = yaml.getConfigurationSection("biome_groups");

            return section.getKeys(false).stream().map(string -> {
                final Set<Biome> biomeSet = new HashSet<>();

                final List<String> stringList = section.getStringList(string);

                stringList.forEach(biome -> biomeSet.add(OreControlUtil.getBiome(biome, false).orElseThrow(() -> new IllegalArgumentException("String: " + biome + " is not a biome!"))));

                return new BiomeGroup(string, biomeSet);
            }).toArray(BiomeGroup[]::new);
        }

        @Override
        public void reload() {
            yaml = Config.getConfig(OreControl.getInstance(), FILE);
        }
    }

    @Getter(value = AccessLevel.PACKAGE)
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static final class BiomeGroup {

        private final String name;

        private final Set<Biome> biomes;
    }

}
