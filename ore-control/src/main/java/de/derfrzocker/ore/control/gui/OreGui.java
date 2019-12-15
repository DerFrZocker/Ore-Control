package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOresAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.gui.settings.OreGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.BasicGui;
import de.derfrzocker.spigot.utils.gui.InventoryUtil;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class OreGui extends BasicGui {

    private static OreGuiSettings oreGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Biome biome;
    @Nullable
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    @Nullable
    private final CopyAction copyAction;

    OreGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.copyAction = null;

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Ore[] ores = biome == null ? Ore.values() : biome.getOres();

        for (int i = 0; i < ores.length; i++)
            addItem(InventoryUtil.calculateSlot(i, oreGuiSettings.getOreGap()), getOreItemStack(ores[i]), new OreConsumer(ores[i]));

        addItem(oreGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreGuiSettings.getBackItemStack()),
                event -> (biome == null ? new WorldConfigGui(oreControlValues, event.getWhoClicked(), worldOreConfig) : new BiomeGui(oreControlValues, event.getWhoClicked(), worldOreConfig)).openSync(event.getWhoClicked()));

        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreGuiSettings.getInfoItemStack() : oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        if (permissions.getValueResetPermission().hasPermission(permissible))
            addItem(oreGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreGuiSettings.getResetValueItemStack()), this::handleResetValues);

        if (permissions.getValueCopyPermission().hasPermission(permissible))
            addItem(oreGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, oreGuiSettings.getCopyValueItemStack()), event -> new WorldGui(oreControlValues, new CopyOresAction(oreControlValues, worldOreConfig, biome, biome == null ? Ore.values() : biome.getOres())).openSync(event.getWhoClicked()));

    }

    public OreGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(copyAction, "CopyAction can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.copyAction = copyAction;

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Ore> ores = new LinkedHashSet<>();

        for (final Ore ore : biome == null ? Ore.values() : biome.getOres()) {
            if (biome == null) {
                if (copyAction.shouldSet(ore))
                    ores.add(ore);
            } else if (copyAction.shouldSet(ore, biome))
                ores.add(ore);
        }

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        for (int i = 0; i < oresArray.length; i++)
            addItem(InventoryUtil.calculateSlot(i, oreGuiSettings.getOreGap()), getOreItemStack(oresArray[i]), new OreCopyConsumer(oresArray[i]));

        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biome == null ? oreGuiSettings.getInfoItemStack() : oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
    }

    OreGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup, @NotNull final BiomeGuiSettings biomeGuiSettings) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(biomeGroup, "BiomeGroup can not be null");
        Validate.notNull(biomeGuiSettings, "BiomeGuiSettings can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.copyAction = null;

        addDecorations();

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Ore> ores = new LinkedHashSet<>();

        biomeGroup.getBiomes().forEach(biome -> ores.addAll(Sets.newHashSet(biome.getOres())));

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        for (int i = 0; i < oresArray.length; i++)
            addItem(InventoryUtil.calculateSlot(i, oreGuiSettings.getOreGap()), getOreItemStack(oresArray[i]), new OreBiomeGroupConsumer(oresArray[i], biomeGuiSettings));

        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
        addItem(oreGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, oreGuiSettings.getBackItemStack()),
                event -> new BiomeGroupGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGuiSettings).openSync(event.getWhoClicked()));
    }

    private static OreGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (oreGuiSettings == null)
            oreGuiSettings = new OreGuiSettings(javaPlugin, "data/gui/ore-gui.yml", true);

        return oreGuiSettings;
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString())};
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null)
                    for (Ore ore : biome.getOres())
                        OreControlUtil.reset(worldOreConfig, ore, biome);
                else
                    for (Ore ore : Ore.values())
                        OreControlUtil.reset(worldOreConfig, ore);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }
        if (biome != null)
            for (final Ore ore : biome.getOres())
                OreControlUtil.reset(worldOreConfig, ore, biome);
        else
            for (final Ore ore : Ore.values())
                OreControlUtil.reset(worldOreConfig, ore);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private ItemStack getOreItemStack(@NotNull final Ore ore) {
        ItemStack itemStack = oreGuiSettings.getDefaultOreItemStack();

        itemStack.setType(ore.getMaterial());

        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack, new MessageValue("ore", ore.toString()));

        return itemStack;
    }

    private final class OreConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Ore ore;

        private OreConsumer(@NotNull final Ore ore) {
            this.ore = ore;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            new OreSettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome, ore).openSync(event.getWhoClicked());
        }

    }

    private final class OreCopyConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Ore ore;

        private OreCopyConsumer(@NotNull final Ore ore) {
            this.ore = ore;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            copyAction.setOreTarget(ore);

            copyAction.next(event.getWhoClicked(), OreGui.this);
        }

    }

    private final class OreBiomeGroupConsumer implements Consumer<InventoryClickEvent> {

        @NotNull
        private final Ore ore;
        @NotNull
        private final BiomeGuiSettings biomeGuiSettings;

        private OreBiomeGroupConsumer(@NotNull final Ore ore, @NotNull final BiomeGuiSettings biomeGuiSettings) {
            this.ore = ore;
            this.biomeGuiSettings = biomeGuiSettings;
        }

        @Override
        public void accept(@NotNull final InventoryClickEvent event) {
            new OreSettingsGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGroup, ore, biomeGuiSettings).openSync(event.getWhoClicked());
        }

    }

}
