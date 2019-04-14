package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
@Setter
public class CopySettingAction implements CopyAction {

    private final boolean filterWorldOreConfig = false;

    private final WorldOreConfig worldOreConfigSource;

    private final Ore oreSource;

    private final Biome biomeSource;

    private final Setting settingSource;

    private Biome biomeTarget;

    private Ore oreTarget;

    private boolean chooseBiome = false;

    private WorldOreConfig worldOreConfigTarget;

    private Setting settingTarget;

    private int status = 0;


    @Override
    public void setBiomesTarget(Biome[] biomes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(HumanEntity humanEntity, InventoryGui inventoryGui) {
        if (status == 0) {
            inventoryGui.openSync(humanEntity, new WorldConfigGui(worldOreConfigTarget, humanEntity, this).getInventory());
            status++;
            return;
        }

        if (status == 1) {
            if (chooseBiome)
                inventoryGui.openSync(humanEntity, new BiomeGui(worldOreConfigTarget, this).getInventory());
            else
                inventoryGui.openSync(humanEntity, new OreGui(worldOreConfigTarget, biomeTarget, this).getInventory());

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            inventoryGui.openSync(humanEntity, new OreGui(worldOreConfigTarget, biomeTarget, this).getInventory());
            status++;
            return;
        }

        if (status == 2) {
            inventoryGui.openSync(humanEntity, new OreSettingsGui(worldOreConfigTarget, oreTarget, biomeTarget, this).getInventory());
            status++;
            return;
        }

        if (status == 3 && chooseBiome) {
            inventoryGui.openSync(humanEntity, new OreSettingsGui(worldOreConfigTarget, oreTarget, biomeTarget, this).getInventory());
            status++;
            return;
        }

        if (status == 3) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, settingTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, settingTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                });

            status++;
            return;
        }

        if (status == 4) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, settingSource, oreTarget, biomeTarget, settingTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                });

            status++;
        }

    }


    private void openVerifyIfNeeded(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui, final @NonNull Consumer<InventoryClickEvent> acceptAction) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            inventoryGui.openSync(humanEntity, new VerifyGui(acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).getInventory());
            return;
        }

        acceptAction.accept(null);
    }

}
