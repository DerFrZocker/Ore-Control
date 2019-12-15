package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyBiomesAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Version;
import de.derfrzocker.spigot.utils.gui.PageGui;
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

public class BiomeGui extends PageGui<Biome> {

    private static BiomeGuiSettings biomeGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final CopyAction copyAction;

    BiomeGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig) {
        super(oreControlValues.getJavaPlugin());

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.copyAction = null;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Set<Biome> biomes = new LinkedHashSet<>();

        for (final Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            biomes.add(biome);
        }

        init(biomes.toArray(new Biome[0]), Biome[]::new, biomeGuiSettings, this::getItemStack, this::handleNormalClick);
        addDecorations();

        addItem(biomeGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getInfoItemStack(), getMessagesValues()));
        addItem(biomeGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getBackItemStack()), event -> new WorldConfigGui(oreControlValues, event.getWhoClicked(), worldOreConfig).openSync(event.getWhoClicked()));
        addItem(biomeGuiSettings.getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getBiomeGroupItemStack()), event -> new BiomeGroupGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biomeGuiSettings).openSync(event.getWhoClicked()));

        if (permissions.getValueResetPermission().hasPermission(permissible))
            addItem(biomeGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getResetValueItemStack()), this::handleResetValues);

        if (permissions.getValueCopyPermission().hasPermission(permissible))
            addItem(biomeGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getCopyValueItemStack()), event -> new WorldGui(oreControlValues, new CopyBiomesAction(oreControlValues, worldOreConfig, Biome.values())).openSync(event.getWhoClicked()));
    }

    public BiomeGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin());

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(copyAction, "CopyAction can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.copyAction = copyAction;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Biome> biomes = new LinkedHashSet<>();

        for (final Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            if (copyAction.shouldSet(biome))
                biomes.add(biome);
        }

        init(biomes.toArray(new Biome[0]), Biome[]::new, biomeGuiSettings, this::getItemStack, this::handleCopyAction);
        addDecorations();

        addItem(biomeGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getInfoItemStack(), getMessagesValues()));
    }

    private static void checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (biomeGuiSettings == null) {
            biomeGuiSettings = new BiomeGuiSettings(javaPlugin, "data/gui/biome-gui.yml", true);
            if (Version.v1_14_R1.isNewerOrSameVersion(Version.getCurrent()))
                biomeGuiSettings.addValues("data/gui/biome-gui_v1.14.yml", true);
        }
    }

    private ItemStack getItemStack(@NotNull final Biome biome) {
        return MessageUtil.replaceItemStack(getPlugin(), biomeGuiSettings.getBiomeItemStack(biome.toString()));
    }

    private void handleNormalClick(@NotNull final Biome biome, @NotNull final InventoryClickEvent event) {
        new OreGui(oreControlValues, event.getWhoClicked(), worldOreConfig, biome).openSync(event.getWhoClicked());
    }

    private void handleCopyAction(@NotNull final Biome biome, @NotNull final InventoryClickEvent event) {
        copyAction.setBiomeTarget(biome);
        copyAction.next(event.getWhoClicked(), this);
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                for (Biome biome : Biome.values())
                    OreControlUtil.reset(this.worldOreConfig, biome);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }

        for (final Biome biome : Biome.values())
            OreControlUtil.reset(this.worldOreConfig, biome);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

}
