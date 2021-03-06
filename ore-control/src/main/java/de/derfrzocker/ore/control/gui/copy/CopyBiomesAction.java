/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.CopyUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CopyBiomesAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final Supplier<InventoryGui> startGui;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    @NotNull
    private final Biome[] biomes;
    private WorldOreConfig worldOreConfigTarget;

    public CopyBiomesAction(@NotNull final OreControlValues oreControlValues, @NotNull Supplier<InventoryGui> startGui, @NotNull final WorldOreConfig worldOreConfigSource, @NotNull final Biome[] biomes) {
        Validate.notNull(oreControlValues, "OreControlValues cannot be null");
        Validate.notNull(startGui, "Start Gui cannot be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig cannot be null");
        Validate.notNull(biomes, "Biomes cannot be null");

        this.oreControlValues = oreControlValues;
        this.startGui = startGui;
        this.worldOreConfigSource = worldOreConfigSource;
        this.biomes = biomes;
    }

    @Override
    public void abort(@NotNull HumanEntity humanEntity) {
        startGui.get().openSync(humanEntity);
    }

    @Override
    public void back(@NotNull HumanEntity humanEntity) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(@NotNull final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final Supplier<InventoryGui> inventoryGui) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            VerifyGui verifyGui = new VerifyGui(oreControlValues.getPlugin(), clickEvent -> {
                for (Biome biome : biomes) {
                    CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, biome, biome);
                }

                oreControlValues.getService().saveWorldOreConfig(worldOreConfigTarget);
                startGui.get().openSync(clickEvent.getWhoClicked());
                oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
            }, clickEvent1 -> inventoryGui.get().openSync(humanEntity));

            verifyGui.addDecorations();

            verifyGui.openSync(humanEntity);

            return;
        }

        for (Biome biome : biomes) {
            CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget, biome, biome);
        }

        oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
        startGui.get().openSync(humanEntity);
        oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
    }

    @Override
    public boolean allowBack() {
        return false;
    }

    @Override
    public boolean isFilterWorldOreConfig() {
        return true;
    }

    @Override
    public boolean shouldSet(@NotNull final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore, @NotNull final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

}
