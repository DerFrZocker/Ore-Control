package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
@Setter
public class CopyBiomesAction implements CopyAction {

    private final boolean filterWorldOreConfig = true;

    private final WorldOreConfig worldOreConfigSource;

    private final Biome[] biomes;

    private WorldOreConfig worldOreConfigTarget = null;

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public void setBiomeTarget(final @NonNull Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(final @NonNull Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(final @NonNull Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(final @NonNull HumanEntity humanEntity, final @NonNull InventoryGui inventoryGui) {
        if (OreControl.getInstance().getConfigValues().verifyCopyAction()) {
            new VerifyGui(OreControl.getInstance(), clickEvent -> {
                for (Biome biome : biomes)
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, biome, biome);

                serviceSupplier.get().saveWorldOreConfig(worldOreConfigTarget);
                inventoryGui.closeSync(humanEntity);
                OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
            }, clickEvent1 -> inventoryGui.openSync(humanEntity)).openSync(humanEntity);

            return;
        }

        for (Biome biome : biomes)
            OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, biome, biome);

        serviceSupplier.get().saveWorldOreConfig(worldOreConfigSource);
        inventoryGui.closeSync(humanEntity);
        OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
    }

    @Override
    public boolean shouldSet(final @NonNull Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final @NonNull Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final @NonNull Ore ore, final @NonNull Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(final @NonNull Setting setting) {
        throw new UnsupportedOperationException();
    }

}
