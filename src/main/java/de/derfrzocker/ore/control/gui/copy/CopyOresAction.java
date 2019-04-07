package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.InventoryGui;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;

@RequiredArgsConstructor
@Getter
@Setter
public class CopyOresAction implements CopyAction {

    private final WorldOreConfig worldOreConfigSource;

    private final Ore[] ores;

    @Override
    public void setWorldOreConfigTarget(WorldOreConfig worldOreConfig) {

    }

    @Override
    public WorldOreConfig getWorldOreConfigTarget() {
        return null;
    }

    @Override
    public void setBiomeTarget(Biome biome) {

    }

    @Override
    public void setSettingTarget(Setting setting) {

    }

    @Override
    public void setBiomesTarget(Biome[] biomes) {

    }

    @Override
    public void next(HumanEntity humanEntity, InventoryGui inventoryGui) {

    }
}
