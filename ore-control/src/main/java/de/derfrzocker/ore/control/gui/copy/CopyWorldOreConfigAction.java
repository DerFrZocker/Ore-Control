package de.derfrzocker.ore.control.gui.copy;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

public class CopyWorldOreConfigAction implements CopyAction {

    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfigSource;
    private WorldOreConfig worldOreConfigTarget = null;

    public CopyWorldOreConfigAction(@NotNull final OreControlValues oreControlValues, @NotNull final WorldOreConfig worldOreConfigSource) {
        Validate.notNull(oreControlValues, "OreControlValues can not be null");
        Validate.notNull(worldOreConfigSource, "WorldOreConfig can not be null");

        this.oreControlValues = oreControlValues;
        this.worldOreConfigSource = worldOreConfigSource;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChooseBiome(final boolean bool) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(@NotNull final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void next(@NotNull final HumanEntity humanEntity, @NotNull final InventoryGui inventoryGui) {
        if (oreControlValues.getConfigValues().verifyCopyAction()) {
            new VerifyGui(oreControlValues.getJavaPlugin(), clickEvent -> {
                OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget);
                oreControlValues.getService().saveWorldOreConfig(worldOreConfigTarget);
                inventoryGui.closeSync(humanEntity);
                oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
            }, clickEvent1 -> inventoryGui.openSync(humanEntity)).openSync(humanEntity);
            return;
        }

        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget);
        oreControlValues.getService().saveWorldOreConfig(worldOreConfigTarget);
        inventoryGui.closeSync(humanEntity);
        oreControlValues.getOreControlMessages().getGuiCopySuccessMessage().sendMessage(humanEntity);
    }

    @Override
    public boolean isFilterWorldOreConfig() {
        return true;
    }

    @Override
    public boolean shouldSet(@NotNull final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Ore ore, @NotNull final Biome biome) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSet(@NotNull final Setting setting) {
        throw new UnsupportedOperationException();
    }

}
