package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.InventoryGui;
import org.bukkit.entity.HumanEntity;

public interface CopyAction {

    WorldOreConfig getWorldOreConfigSource();

    Biome getBiomeSource();

    Ore getOreSource();

    void setWorldOreConfigTarget(WorldOreConfig worldOreConfig);

    WorldOreConfig getWorldOreConfigTarget();

    void setBiomeTarget(Biome biome);

    void setSettingTarget(Setting setting);

    void setBiomesTarget(Biome[] biomes);

    void setChooseBiome(boolean bool);

    void setOreTarget(Ore ore);

    void next(HumanEntity humanEntity, InventoryGui inventoryGui);

    boolean isFilterWorldOreConfig();

}
