package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
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
import java.util.function.Supplier;

public class BiomeGroupGui extends PageGui<BiomeGroupGui.BiomeGroup> {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    BiomeGroupGui(final WorldOreConfig worldOreConfig, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.serviceSupplier = serviceSupplier;
        init(BiomeGroups.getInstance().getGroups(), BiomeGroup[]::new, BiomeGui.BiomeGuiSettings.getInstance(), this::getItemStack, this::handleNormalClick);

        addItem(BiomeGui.BiomeGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGui.BiomeGuiSettings.getInstance().getBackItemStack()), event -> new WorldConfigGui(worldOreConfig, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked()));
        addItem(BiomeGui.BiomeGuiSettings.getInstance().getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGui.BiomeGuiSettings.getInstance().getBiomeItemStack()), event -> new BiomeGui(event.getWhoClicked(), worldOreConfig, serviceSupplier).openSync(event.getWhoClicked()));
    }

    private ItemStack getItemStack(final BiomeGroup biomeGroup) {
        return MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGui.BiomeGuiSettings.getInstance().getBiomeItemStack(biomeGroup.getName().toUpperCase()));
    }

    private void handleNormalClick(final BiomeGroup biomeGroup, final InventoryClickEvent event) {
        new OreGui(worldOreConfig, biomeGroup, serviceSupplier).openSync(event.getWhoClicked());
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

                stringList.forEach(biomeName -> OreControlUtil.getBiome(biomeName, false).ifPresent(biomeSet::add));

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
