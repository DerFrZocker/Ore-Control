package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyBiomesAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Version;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.gui.PageSettings;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class BiomeGui extends PageGui<Biome> {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    private final CopyAction copyAction;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    BiomeGui(final Permissible permissible, final WorldOreConfig worldOreConfig, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.copyAction = null;
        this.serviceSupplier = serviceSupplier;

        final Set<Biome> biomes = new LinkedHashSet<>();

        for (Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            biomes.add(biome);
        }

        init(biomes.toArray(new Biome[0]), Biome[]::new, BiomeGuiSettings.getInstance(), this::getItemStack, this::handleNormalClick);

        addItem(BiomeGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getInfoItemStack(), getMessagesValues()));
        addItem(BiomeGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getBackItemStack()), event -> new WorldConfigGui(worldOreConfig, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked()));
        addItem(BiomeGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getBackItemStack()), event -> new WorldConfigGui(worldOreConfig, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked()));
        addItem(BiomeGuiSettings.getInstance().getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getBiomeGroupItemStack()), event -> new BiomeGroupGui(worldOreConfig, serviceSupplier).openSync(event.getWhoClicked()));

        if (Permissions.RESET_VALUE_PERMISSION.hasPermission(permissible))
            addItem(BiomeGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUE_PERMISSION.hasPermission(permissible))
            addItem(BiomeGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getCopyValueItemStack()), event -> new WorldGui(new CopyBiomesAction(worldOreConfig, Biome.values(), serviceSupplier), serviceSupplier).openSync(event.getWhoClicked()));
    }

    public BiomeGui(final WorldOreConfig worldOreConfig, final @NonNull CopyAction copyAction, final Supplier<OreControlService> serviceSupplier) {
        super(OreControl.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.copyAction = copyAction;
        this.serviceSupplier = serviceSupplier;

        final Set<Biome> biomes = new LinkedHashSet<>();

        for (Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            if (copyAction.shouldSet(biome))
                biomes.add(biome);
        }
        init(biomes.toArray(new Biome[0]), Biome[]::new, BiomeGuiSettings.getInstance(), this::getItemStack, this::handleCopyAction);

        addItem(BiomeGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getInfoItemStack(), getMessagesValues()));
    }

    private ItemStack getItemStack(final Biome biome) {
        return MessageUtil.replaceItemStack(OreControl.getInstance(), BiomeGuiSettings.getInstance().getBiomeItemStack(biome.toString()));
    }

    private void handleNormalClick(final Biome biome, final InventoryClickEvent event) {
        new OreGui(worldOreConfig, biome, event.getWhoClicked(), serviceSupplier).openSync(event.getWhoClicked());
    }

    private void handleCopyAction(final Biome biome, final InventoryClickEvent event) {
        copyAction.setBiomeTarget(biome);
        copyAction.next(event.getWhoClicked(), this);
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            new VerifyGui(OreControl.getInstance(), clickEvent -> {
                for (Biome biome : Biome.values())
                    OreControlUtil.reset(this.worldOreConfig, biome);

                serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }

        for (Biome biome : Biome.values())
            OreControlUtil.reset(this.worldOreConfig, biome);

        serviceSupplier.get().saveWorldOreConfig(worldOreConfig);
        OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

    static final class BiomeGuiSettings extends PageSettings {
        private static BiomeGuiSettings instance = null;

        protected static BiomeGuiSettings getInstance() {
            if (instance == null)
                instance = new BiomeGuiSettings();

            return instance;
        }

        private BiomeGuiSettings() {
            super(OreControl.getInstance(), "data/biome_gui.yml");
            if (Version.getCurrent() == Version.v1_14_R1)
                getYaml().setDefaults(Config.getConfig(OreControl.getInstance(), "data/biome_gui_v1.14.yml"));

        }

        @Override
        public void reload() {
            super.reload();
            if (Version.getCurrent() == Version.v1_14_R1)
                getYaml().setDefaults(Config.getConfig(OreControl.getInstance(), "data/biome_gui_v1.14.yml"));
        }

        ItemStack getBiomeItemStack(final String biome) {
            return getYaml().getItemStack("biomes." + biome).clone();
        }

        int getBiomeGroupSwitchSlot() {
            return getYaml().getInt("biome_group.slot");
        }

        ItemStack getBiomeGroupItemStack() {
            return getYaml().getItemStack("biome_group.group.item_stack").clone();
        }

        ItemStack getBiomeItemStack() {
            return getYaml().getItemStack("biome_group.biome.item_stack").clone();
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }

        ItemStack getBackItemStack() {
            return getYaml().getItemStack("back.item_stack").clone();
        }

        int getBackSlot() {
            return getYaml().getInt("back.slot");
        }

        private int getResetValueSlot() {
            return getYaml().getInt("value.reset.slot");
        }

        private ItemStack getResetValueItemStack() {
            return getYaml().getItemStack("value.reset.item_stack").clone();
        }

        private int getCopyValueSlot() {
            return getYaml().getInt("value.copy.slot");
        }

        private ItemStack getCopyValueItemStack() {
            return getYaml().getItemStack("value.copy.item_stack").clone();
        }
    }

}
