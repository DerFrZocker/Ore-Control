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
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public interface CopyAction {

    @NotNull
    WorldOreConfig getWorldOreConfigSource();

    void setWorldOreConfigTarget(@NotNull WorldOreConfig worldOreConfig);

    void setBiomeTarget(@NotNull Biome biome);

    void setSettingTarget(@NotNull Setting setting);

    void setChooseBiome(boolean bool);

    void setOreTarget(@NotNull Ore ore);

    void next(@NotNull HumanEntity humanEntity, @NotNull InventoryGui inventoryGui);

    boolean isFilterWorldOreConfig();

    boolean shouldSet(@NotNull Biome biome);

    boolean shouldSet(@NotNull Ore ore);

    boolean shouldSet(@NotNull Ore ore, @NotNull Biome biome);

    boolean shouldSet(@NotNull Setting setting);

}
