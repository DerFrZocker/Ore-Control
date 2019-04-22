package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.InventoryGui;
import org.bukkit.entity.HumanEntity;

public interface CopyAction {

    WorldOreConfig getWorldOreConfigSource();

    void setWorldOreConfigTarget(WorldOreConfig worldOreConfig);

    void setBiomeTarget(Biome biome);

    void setSettingTarget(Setting setting);

    void setChooseBiome(boolean bool);

    void setOreTarget(Ore ore);

    void next(HumanEntity humanEntity, InventoryGui inventoryGui);

    boolean isFilterWorldOreConfig();

    boolean shouldSet(Biome biome);

    boolean shouldSet(Ore ore);

    boolean shouldSet(Ore ore, Biome biome);

    boolean shouldSet(Setting setting);

}
