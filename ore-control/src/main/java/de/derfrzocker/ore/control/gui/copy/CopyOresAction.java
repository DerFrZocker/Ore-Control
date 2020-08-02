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

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.CopyUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class CopyOresAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    @Nullable
    private final Biome biomeSource;
    @NotNull
    private final Ore[] oresSource;
    private WorldOreConfig worldOreConfigTarget;
    private Biome biomeTarget;
    private boolean chooseBiome;
    private int status = 0;

    public CopyOresAction(@NotNull final OreControlValues oreControlValues, @NotNull final WorldOreConfig worldOreConfigSource, @Nullable final Biome biomeSource, @NotNull final Ore[] oresSource) {
        Validate.notNull(oreControlValues, "OreControlValues cannot be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig cannot be null");
        Validate.notNull(oresSource, "Ores cannot be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfigSource = worldOreConfigSource;
        this.biomeSource = biomeSource;
        this.oresSource = oresSource;
    }

    @NotNull
    @Override
    public WorldOreConfig getWorldOreConfigSource() {
        return worldOreConfigSource;
    }

    @Override
    public void setWorldOreConfigTarget(@NotNull final WorldOreConfig worldOreConfig) {
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");

        this.worldOreConfigTarget = worldOreConfig;
    }

    @Override
    public void setBiomeTarget(@NotNull final Biome biome) {
        Validate.notNull(biome, "Biome cannot be null");

        this.biomeTarget = biome;
    }

    @Override
    public void setSettingTarget(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        this.chooseBiome = bool;
    }

    @Override
    public void setOreTarget(@NotNull final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui) {

        if (status == 0) {
            new WorldConfigGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 1) {
            if (chooseBiome) {
                new BiomeGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            } else {
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    if (biomeSource == null) {
                        for (Ore ore : oresSource) {
                            CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, ore, ore);
                        }
                    } else {
                        for (Ore ore : oresSource) {
                            CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, ore, biomeSource, ore);
                        }
                    }

                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });
            }

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                if (biomeSource == null) {
                    final Set<Ore> oreSet = Sets.newHashSet(biomeTarget.getOres());
                    for (final Ore ore : oresSource) {
                        if (oreSet.contains(ore)) {
                            CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, ore, ore, biomeTarget);
                        }
                    }
                } else {
                    final Set<Ore> oreSet = Sets.newHashSet(biomeTarget.getOres());
                    for (final Ore ore : oresSource) {
                        if (oreSet.contains(ore)) {
                            CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, ore, biomeSource, ore, biomeTarget);
                        }
                    }
                }

                status++;
                oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                inventoryGui.closeSync(humanEntity);
                oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
            });
        }

    }

    @Override
    public boolean isFilterWorldOreConfig() {
        return false;
    }

    @Override
    public boolean shouldSet(@NotNull final Biome biome) {
        if (biomeSource == null) {
            return true;
        }

        if (worldOreConfigSource != worldOreConfigTarget && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName())) {
            return true;
        }

        return biomeSource != biome;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore) {
        return (worldOreConfigTarget != worldOreConfigSource && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName())) || biomeSource != null;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore, @NotNull final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    private void openVerifyIfNeeded(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui, @NotNull final Consumer<InventoryClickEvent> acceptAction) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            new VerifyGui(oreControlValues.getJavaPlugin(), acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).openSync(humanEntity);
            return;
        }

        acceptAction.accept(null);
    }

}
