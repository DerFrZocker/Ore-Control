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
