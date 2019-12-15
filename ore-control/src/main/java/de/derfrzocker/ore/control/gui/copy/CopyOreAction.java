package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.OreGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public class CopyOreAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    @Nullable
    private final Biome biomeSource;
    @NotNull
    private final Ore oreSource;
    private Biome biomeTarget;
    private Ore oreTarget;
    private boolean chooseBiome;
    private WorldOreConfig worldOreConfigTarget;
    private int status = 0;

    public CopyOreAction(@NotNull final OreControlValues oreControlValues, @NotNull final WorldOreConfig worldOreConfigSource, @Nullable final Biome biomeSource, @NotNull final Ore oreSource) {
        Validate.notNull(oreControlValues, "OreControlValues can not be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig can not be null");
        Validate.notNull(oreSource, "Ore can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfigSource = worldOreConfigSource;
        this.biomeSource = biomeSource;
        this.oreSource = oreSource;
    }

    @NotNull
    @Override
    public WorldOreConfig getWorldOreConfigSource() {
        return worldOreConfigSource;
    }

    @Override
    public void setWorldOreConfigTarget(@NotNull final WorldOreConfig worldOreConfig) {
        Validate.notNull(worldOreConfig, "WorldOreConfig can not be null");

        this.worldOreConfigTarget = worldOreConfig;
    }

    @Override
    public void setBiomeTarget(@NotNull final Biome biome) {
        Validate.notNull(biome, "Biome can not be null");

        this.biomeTarget = biome;
    }

    @Override
    public void setSettingTarget(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        this.chooseBiome = bool;
    }

    @Override
    public void setOreTarget(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore can not be null");

        this.oreTarget = ore;
    }

    @Override
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui) {
        Validate.notNull(humanEntity, "HumanEntity can not be null");
        Validate.notNull(inventoryGui, "InventoryGui can not be null");

        if (status == 0) {
            new WorldConfigGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 1) {
            if (chooseBiome)
                new BiomeGui(oreControlValues, humanEntity, worldOreConfigTarget, this).openSync(humanEntity);
            else
                new OreGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, this).openSync(humanEntity);

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            new OreGui(oreControlValues, humanEntity, worldOreConfigTarget, biomeTarget, this).openSync(humanEntity);
            status++;
            return;
        }

        if (status == 2) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, oreTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, oreTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });

            status++;
            return;
        }

        if (status == 3) {
            if (biomeSource == null)
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, oreTarget, biomeTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });
            else
                openVerifyIfNeeded(humanEntity, inventoryGui, event -> {
                    OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, oreSource, biomeSource, oreTarget, biomeTarget);
                    oreControlValues.getService().saveWorldOreConfig(worldOreConfigSource);
                    inventoryGui.closeSync(humanEntity);
                    oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
                });

            status++;

        }
    }

    @Override
    public boolean isFilterWorldOreConfig() {
        return false;
    }

    @Override
    public boolean shouldSet(@NotNull final Biome biome) {
        for (final Ore ore : biome.getOres())
            if (Arrays.equals(ore.getSettings(), oreSource.getSettings()))
                return true;

        return false;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore) {
        if (!Arrays.equals(ore.getSettings(), oreSource.getSettings()))
            return false;

        if (biomeSource != null)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        return ore != oreSource;
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore, @NotNull final Biome biome) {
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
    public boolean shouldSet(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    private void openVerifyIfNeeded(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui, @NotNull final Consumer<InventoryClickEvent> acceptAction) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            new VerifyGui(oreControlValues.getJavaPlugin(), acceptAction, clickEvent1 -> inventoryGui.closeSync(humanEntity)).openSync(humanEntity);
            return;
        }

        acceptAction.accept(null);
    }

}
