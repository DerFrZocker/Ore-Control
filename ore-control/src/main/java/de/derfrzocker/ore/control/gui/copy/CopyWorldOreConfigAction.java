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
import de.derfrzocker.ore.control.utils.CopyUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class CopyWorldOreConfigAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    private WorldOreConfig worldOreConfigTarget = null;

    public CopyWorldOreConfigAction(@NotNull final OreControlValues oreControlValues, @NotNull final WorldOreConfig worldOreConfigSource) {
        Validate.notNull(oreControlValues, "OreControlValues cannot be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig cannot be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfigSource = worldOreConfigSource;
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
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            new VerifyGui(oreControlValues.getPlugin(), clickEvent -> {
                CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget);
                oreControlValues.getService().saveWorldOreConfig(worldOreConfigTarget);
                inventoryGui.closeSync(humanEntity);
                oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
            }, clickEvent1 -> inventoryGui.openSync(humanEntity)).openSync(humanEntity);
            return;
        }

        CopyUtil.copy(oreControlValues.getService(), worldOreConfigSource, worldOreConfigTarget);
        oreControlValues.getService().saveWorldOreConfig(worldOreConfigTarget);
        inventoryGui.closeSync(humanEntity);
        oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
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
