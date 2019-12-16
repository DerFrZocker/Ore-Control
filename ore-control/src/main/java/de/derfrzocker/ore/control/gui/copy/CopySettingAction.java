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

package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.OreGui;
import de.derfrzocker.ore.control.gui.OreSettingsGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CopySettingAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    @Nullable
    private final Biome biomeSource;
    @NotNull
    private final Ore oreSource;
    @NotNull
    private final Setting settingSource;
    private WorldOreConfig worldOreConfigTarget;
    private Biome biomeTarget;
    private Ore oreTarget;
    private Setting settingTarget;
    private boolean chooseBiome = false;
    private int status = 0;

    public CopySettingAction(@NotNull final OreControlValues oreControlValues, @NotNull final WorldOreConfig worldOreConfigSource, @Nullable final Biome biomeSource, @NotNull final Ore oreSource, @NotNull final Setting settingSource) {
        Validate.notNull(oreControlValues, "OreControlValues can not be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig can not be null");
        Validate.notNull(oreSource, "Ore can not be null");
        Validate.notNull(settingSource, "Setting can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfigSource = worldOreConfigSource;
        this.biomeSource = biomeSource;
        this.oreSource = oreSource;
        this.settingSource = settingSource;
    }

    @NotNull
    @Override
    public WorldOreConfig getWorldOreConfigSource() {
        return worldOreConfigSource;
    }

    @Override
    public void setWorldOreConfigTarget(@NotNull final WorldOreConfig worldOreConfig) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");

        this.worldOreConfigTarget = worldOreConfig;
    }

    @Override
    public void setBiomeTarget(@NotNull final Biome biome) {
        Validate.notNull(biome, "Biome can not be null");

        this.biomeTarget = biome;
    }

    @Override
    public void setSettingTarget(@NotNull final Setting setting) {
        Validate.notNull(setting, "Setting can not be null");

        this.settingTarget = setting;
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        this.chooseBiome = bool;
    }

    @Override
    public void setOreTarget(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore can not be null");

        this.oreTarget = ore;
    }

    @Override
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui) {
        if (status == 0) {
            new WorldConfigGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 1) {
            if (chooseBiome)
                new BiomeGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            else
                new OreGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, this).openSync(humanEntity);

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            new OreGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 2) {
            new OreSettingsGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, oreTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 3 && chooseBiome) {
            new OreSettingsGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, oreTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 3) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, settingTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, settingTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });

            status++;
            return;
        }

        if (status == 4) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });

            status++;
        }

    }

    @Override
    public boolean isFilterWorldOreConfig() {
        return false;
    }

    @Override
    public boolean shouldSet(@NotNull final Biome biome) {
        return true;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore) {
        return true;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore, @NotNull final Biome biome) {
        return true;
    }

    @Override
    public boolean shouldSet(@NotNull final Setting setting) {
        if (biomeSource != biomeTarget)
            return true;

        if (oreSource != oreTarget)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget || !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        return settingSource != setting;
    }

    private void openVerifyIfNeeded(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui, @NotNull final Consumer<InventoryClickEvent> acceptAction) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            new VerifyGui(oreControlValues.getJavaPlugin(), acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).openSync(humanEntity);
            return;
        }

        acceptAction.accept(null);
    }

}
