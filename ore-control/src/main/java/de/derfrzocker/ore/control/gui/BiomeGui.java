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

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyBiomesAction;
import de.derfrzocker.ore.control.gui.settings.BiomeGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
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
    private final Dimension dimension;
    @Nullable
    private final CopyAction copyAction;

    BiomeGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = dimension;
        this.copyAction = null;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Set<Biome> biomes = new LinkedHashSet<>();

        for (final Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            if (biome.getUntil() != null && Version.getCurrent().isOlderVersion(biome.getUntil())) {
                continue;
            }

            if (dimension != null && biome.getDimension() != dimension) {
                continue;
            }

            biomes.add(biome);
        }

        addDecorations();
        init(biomes.toArray(new Biome[0]), Biome[]::new, this::getItemStack, this::handleNormalClick);

        addItem(biomeGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getInfoItemStack(), getMessagesValues()));
        addItem(biomeGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getBackItemStack()), event -> new WorldConfigGui(oreControlValues, event.getWhoClicked(), worldOreConfig, dimension).openSync(event.getWhoClicked()));

        if (dimension == null || dimension == Dimension.OVERWORLD) { //TODO check biome groups for place able
            addItem(biomeGuiSettings.getBiomeGroupSwitchSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getBiomeGroupItemStack()), event -> new BiomeGroupGui(oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGuiSettings).openSync(event.getWhoClicked()));
        }

        if (permissions.getValueResetPermission().hasPermission(permissible))
            addItem(biomeGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getResetValueItemStack()), this::handleResetValues);

        if (permissions.getValueCopyPermission().hasPermission(permissible))
            addItem(biomeGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getCopyValueItemStack()), event -> new WorldGui(oreControlValues, new CopyBiomesAction(oreControlValues, worldOreConfig, Biome.values())).openSync(event.getWhoClicked()));
    }

    public BiomeGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @NotNull CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin(), checkSettings(oreControlValues.getJavaPlugin()));

        Validate.notNull(permissible, "Permissible can not be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");
        Validate.notNull(copyAction, "CopyAction can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.dimension = null;
        this.copyAction = copyAction;

        final JavaPlugin javaPlugin = oreControlValues.getJavaPlugin();
        final Set<Biome> biomes = new LinkedHashSet<>();

        for (final Biome biome : Biome.values()) {
            if (Version.getCurrent().isNewerVersion(biome.getSince()))
                continue;

            if (biome.getUntil() != null && Version.getCurrent().isOlderVersion(biome.getUntil())) {
                continue;
            }

            if (copyAction.shouldSet(biome))
                biomes.add(biome);
        }

        addDecorations();
        init(biomes.toArray(new Biome[0]), Biome[]::new, this::getItemStack, this::handleCopyAction);

        addItem(biomeGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(javaPlugin, biomeGuiSettings.getInfoItemStack(), getMessagesValues()));
    }

    private static BiomeGuiSettings checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (biomeGuiSettings == null) {
            if (Version.getCurrent() == Version.v1_13_R1 || Version.getCurrent() == Version.v1_13_R2) {
                biomeGuiSettings = new BiomeGuiSettings(javaPlugin, "data/gui/biome-gui_v1.13.yml", true);
            } else {
                biomeGuiSettings = new BiomeGuiSettings(javaPlugin, "data/gui/biome-gui.yml", true);
                if (Version.v1_14_R1.isNewerOrSameVersion(Version.getCurrent()))
                    biomeGuiSettings.addValues("data/gui/biome-gui_v1.14.yml", true);
            }
        }

        return biomeGuiSettings;
    }

    private ItemStack getItemStack(@NotNull final Biome biome) {
        return MessageUtil.replaceItemStack(getPlugin(), biomeGuiSettings.getBiomeItemStack(biome.toString()));
    }

    private void handleNormalClick(@NotNull final Biome biome, @NotNull final InventoryClickEvent event) {
        new OreGui(oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome).openSync(event.getWhoClicked());
    }

    private void handleCopyAction(@NotNull final Biome biome, @NotNull final InventoryClickEvent event) {
        copyAction.setBiomeTarget(biome);
        copyAction.next(event.getWhoClicked(), this);
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            new VerifyGui(getPlugin(), clickEvent -> {
                for (Biome biome : Biome.values())
                    ResetUtil.reset(this.worldOreConfig, biome);

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                openSync(event.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
            }, clickEvent1 -> openSync(event.getWhoClicked())).openSync(event.getWhoClicked());
            return;
        }

        for (final Biome biome : Biome.values())
            ResetUtil.reset(this.worldOreConfig, biome);

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName())};
    }

}
