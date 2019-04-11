package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.InventoryGui;
import de.derfrzocker.ore.control.gui.VerifyGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;

@RequiredArgsConstructor
@Getter
@Setter
public class CopyWorldOreConfigAction implements CopyAction {

    private final boolean filterWorldOreConfig = true;

    private final WorldOreConfig worldOreConfigSource;

    private WorldOreConfig worldOreConfigTarget = null;

    @Override
    public Biome getBiomeSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Ore getOreSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBiomeTarget(final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBiomesTarget(final Biome[] biomes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(Ore ore) {

    }

    @Override
    public void next(final @NonNull HumanEntity humanEntity, InventoryGui inventoryGui) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            inventoryGui.openSync(humanEntity, new VerifyGui(clickEvent -> {
                OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget);
                OreControl.getService().saveWorldOreConfig(worldOreConfigTarget);
                inventoryGui.closeSync(humanEntity);
            }, clickEvent1 -> inventoryGui.openSync(humanEntity, inventoryGui.getInventory())).getInventory());
            return;
        }

        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget);
        OreControl.getService().saveWorldOreConfig(worldOreConfigTarget);
        inventoryGui.closeSync(humanEntity);
    }
}
