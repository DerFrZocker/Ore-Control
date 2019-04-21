package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
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
public class CopyBiomesAction implements CopyAction {

    private final boolean filterWorldOreConfig = true;

    private final WorldOreConfig worldOreConfigSource;

    private final Biome[] biomes;

    private WorldOreConfig worldOreConfigTarget = null;

    @Override
    public void setBiomeTarget(final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            inventoryGui.openSync(humanEntity, new VerifyGui(clickEvent -> {

                for (Biome biome : biomes)
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, biome, biome);

                OreControl.getService().saveWorldOreConfig(worldOreConfigTarget);
                inventoryGui.closeSync(humanEntity);
                OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
            }, clickEvent1 -> inventoryGui.openSync(humanEntity, inventoryGui.getInventory())).getInventory());
            return;
        }

        for (Biome biome : biomes)
            OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, biome, biome);

        OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
        inventoryGui.closeSync(humanEntity);
        OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
    }

    @Override
    public boolean shouldSet(final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final Ore ore, final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final Setting setting) {
        throw new UnsupportedOperationException();
    }

}
