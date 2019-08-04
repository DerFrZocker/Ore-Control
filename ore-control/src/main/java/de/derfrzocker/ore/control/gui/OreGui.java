package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOresAction;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.BasicSettings;
import de.derfrzocker.spigot.utils.gui.InventoryUtil;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class OreGui extends BasicGui {

    @NonNull
    private final WorldOreConfig worldOreConfig;

    private final Biome biome;
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    private final CopyAction copyAction;

    OreGui(final WorldOreConfig worldOreConfig, final Biome biome, final Permissible permissible) {
        super(OreControl.getInstance(), OreGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.copyAction = null;
        this.biomeGroup = null;

        final Ore[] ores = biome == null ? Ore.values() : biome.getOres();

        for (int i = 0; i < ores.length; i++)
            addItem(InventoryUtil.calculateSlot(i, OreGuiSettings.getInstance().getOreGap()), getOreItemStack(ores[i]), new OreConsumer(ores[i]));

        addItem(OreGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreGuiSettings.getInstance().getBackItemStack()),
                event -> (biome == null ? new WorldConfigGui(worldOreConfig, event.getWhoClicked()) : new BiomeGui(event.getWhoClicked(), worldOreConfig)).openSync(event.getWhoClicked()));

        addItem(OreGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), biome == null ? OreGuiSettings.getInstance().getInfoItemStack() : OreGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));

        if (Permissions.RESET_VALUE_PERMISSION.hasPermission(permissible))
            addItem(OreGuiSettings.getInstance().getResetValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreGuiSettings.getInstance().getResetValueItemStack()), this::handleResetValues);

        if (Permissions.COPY_VALUE_PERMISSION.hasPermission(permissible))
            addItem(OreGuiSettings.getInstance().getCopyValueSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreGuiSettings.getInstance().getCopyValueItemStack()), event -> new WorldGui(new CopyOresAction(worldOreConfig, biome == null ? Ore.values() : biome.getOres(), biome)).openSync(event.getWhoClicked()));

    }

    public OreGui(final WorldOreConfig worldOreConfig, final Biome biome, final @NonNull CopyAction copyAction) {
        super(OreControl.getInstance(), OreGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.copyAction = copyAction;
        this.biomeGroup = null;

        final Set<Ore> ores = new LinkedHashSet<>();

        for (Ore ore : biome == null ? Ore.values() : biome.getOres()) {
            if (biome == null) {
                if (copyAction.shouldSet(ore))
                    ores.add(ore);
            } else if (copyAction.shouldSet(ore, biome))
                ores.add(ore);
        }

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        for (int i = 0; i < oresArray.length; i++)
            addItem(InventoryUtil.calculateSlot(i, OreGuiSettings.getInstance().getOreGap()), getOreItemStack(oresArray[i]), new OreCopyConsumer(oresArray[i]));

        addItem(OreGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), biome == null ? OreGuiSettings.getInstance().getInfoItemStack() : OreGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreGui(final WorldOreConfig worldOreConfig, final BiomeGroupGui.BiomeGroup biomeGroup) {
        super(OreControl.getInstance(), OreGuiSettings.getInstance());
        this.worldOreConfig = worldOreConfig;
        this.biomeGroup = biomeGroup;
        this.biome = null;
        this.copyAction = null;

        final Set<Ore> ores = new LinkedHashSet<>();

        biomeGroup.getBiomes().forEach(biome -> ores.addAll(Sets.newHashSet(biome.getOres())));

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        for (int i = 0; i < oresArray.length; i++)
            addItem(InventoryUtil.calculateSlot(i, OreGuiSettings.getInstance().getOreGap()), getOreItemStack(oresArray[i]), new OreBiomeGroupConsumer(oresArray[i]));

        addItem(OreGuiSettings.getInstance().getInfoSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreGuiSettings.getInstance().getInfoBiomeItemStack(), getMessagesValues()));
        addItem(OreGuiSettings.getInstance().getBackSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), OreGuiSettings.getInstance().getBackItemStack()),
                event -> new BiomeGroupGui(worldOreConfig).openSync(event.getWhoClicked()));
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString())};
    }

    private void handleResetValues(final InventoryClickEvent event) {
        if (OreControl.getInstance().getConfigValues().verifyResetAction()) {
            new VerifyGui(OreControl.getInstance(), clickEvent -> {
                if (biome != null)
                    for (Ore ore : biome.getOres())
                        OreControlUtil.reset(worldOreConfig, ore, biome);
                else
                    for (Ore ore : Ore.values())
                        OreControlUtil.reset(worldOreConfig, ore);

                OreControl.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            for (Ore ore : biome.getOres())
                OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            for (Ore ore : Ore.values())
                OreControlUtil.reset(worldOreConfig, ore);

        OreControl.getService().saveWorldOreConfig(worldOreConfig);
        OreControlMessages.RESET_VALUE_SUCCESS.sendMessage(event.getWhoClicked());
    }

    private ItemStack getOreItemStack(final Ore ore) {
        ItemStack itemStack = OreGuiSettings.getInstance().getDefaultOreItemStack();

        itemStack.setType(ore.getMaterial());

        itemStack = MessageUtil.replaceItemStack(OreControl.getInstance(), itemStack, new MessageValue("ore", ore.toString()));

        return itemStack;
    }

    private static final class OreGuiSettings extends BasicSettings {

        private static OreGuiSettings instance = null;

        private static OreGuiSettings getInstance() {
            if (instance == null)
                instance = new OreGuiSettings();

            return instance;
        }

        private OreGuiSettings() {
            super(OreControl.getInstance(), "data/ore_gui.yml");
        }

        private int getOreGap() {
            return getYaml().getInt("inventory.ore_gap");
        }

        private ItemStack getInfoItemStack() {
            return getYaml().getItemStack("info.item_stack").clone();
        }

        private ItemStack getInfoBiomeItemStack() {
            return getYaml().getItemStack("info.biome_item_stack").clone();
        }

        private int getInfoSlot() {
            return getYaml().getInt("info.slot");
        }

        private ItemStack getDefaultOreItemStack() {
            return getYaml().getItemStack("default_ore_item_stack").clone();
        }

        private ItemStack getBackItemStack() {
            return getYaml().getItemStack("back.item_stack").clone();
        }

        private int getBackSlot() {
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OreConsumer implements Consumer<InventoryClickEvent> {

        private final Ore ore;

        @Override
        public void accept(final InventoryClickEvent event) {
            new OreSettingsGui(worldOreConfig, ore, biome, event.getWhoClicked()).openSync(event.getWhoClicked());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OreCopyConsumer implements Consumer<InventoryClickEvent> {

        private final Ore ore;

        @Override
        public void accept(final InventoryClickEvent event) {
            copyAction.setOreTarget(ore);

            copyAction.next(event.getWhoClicked(), OreGui.this);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class OreBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        private final Ore ore;

        @Override
        public void accept(final InventoryClickEvent event) {
            new OreSettingsGui(worldOreConfig, ore, biomeGroup).openSync(event.getWhoClicked());
        }
    }

}
