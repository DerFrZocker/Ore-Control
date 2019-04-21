package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
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

import java.util.Arrays;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
@Setter
public class CopyOreAction implements CopyAction {

    private final boolean filterWorldOreConfig = false;

    private final WorldOreConfig worldOreConfigSource;

    private final Ore oreSource;

    private final Biome biomeSource;

    private Biome biomeTarget;

    private Ore oreTarget;

    private boolean chooseBiome = false;

    private WorldOreConfig worldOreConfigTarget;

    private int status = 0;

    @Override
    public void setSettingTarget(final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui) {
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
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, oreTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, oreTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });

            status++;
            return;
        }

        if (status == 3) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, oreTarget, biomeTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, oreTarget, biomeTarget);
                    OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
                });

            status++;

        }
    }

    @Override
    public boolean shouldSet(final Biome biome) {
        for (Ore ore : biome.getOres())
            if (Arrays.equals(ore.getSettings(), oreSource.getSettings()))
                return true;

        return false;
    }

    @Override
    public boolean shouldSet(final Ore ore) {
        if (!Arrays.equals(ore.getSettings(), oreSource.getSettings()))
            return false;

        if (biomeSource != null)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        return ore != oreSource;
    }

    @Override
    public boolean shouldSet(final Ore ore, final Biome biome) {
        if (!Arrays.equals(ore.getSettings(), oreSource.getSettings()))
            return false;

        if (biomeSource == null)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        if (biome != biomeSource)
            return true;

        return ore != oreSource;
    }

    @Override
    public boolean shouldSet(final Setting setting) {
        throw new UnsupportedOperationException();
    }

    private void openVerifyIfNeeded(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui, final @NonNull Consumer<InventoryClickEvent> acceptAction) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            inventoryGui.openSync(humanEntity, new VerifyGui(acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).getInventory());
            return;
        }

        acceptAction.accept(null);
    }

}
