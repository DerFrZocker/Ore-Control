package de.derfrzocker.ore.control.gui.copy;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;

import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
public class CopyOresAction implements CopyAction {

    private final boolean filterWorldOreConfig = false;

    private final WorldOreConfig worldOreConfigSource;

    private final Ore[] oresSource;

    private final Biome biomeSource;

    private Biome biomeTarget;

    private boolean chooseBiome = false;

    private WorldOreConfig worldOreConfigTarget;

    private int status = 0;

    @Override
    public void setSettingTarget(final Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(final Ore ore) {
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
            if (chooseBiome) {
                inventoryGui.openSync(humanEntity, new BiomeGui(worldOreConfigTarget, this).getInventory());
            } else {
                if (biomeSource == null)
                    for (Ore ore : oresSource)
                        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, ore, ore);
                else
                    for (Ore ore : oresSource)
                        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, ore, biomeSource, ore);

                OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
                inventoryGui.closeSync(humanEntity);
                OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
            }

            status++;
            return;
        }

        if (status == 2 && chooseBiome) {
            if (biomeSource == null) {
                final Set<Ore> oreSet = Sets.newHashSet(biomeTarget.getOres());
                for (Ore ore : oresSource)
                    if (oreSet.contains(ore))
                        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, ore, ore, biomeTarget);
            } else {
                final Set<Ore> oreSet = Sets.newHashSet(biomeTarget.getOres());
                for (Ore ore : oresSource)
                    if (oreSet.contains(ore))
                        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, ore, biomeSource, ore, biomeTarget);
            }

            status++;
            OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
            inventoryGui.closeSync(humanEntity);
            OreControlMessages.COPY_VALUE_SUCCESS.sendMessage(humanEntity);
        }

    }

    @Override
    public boolean shouldSet(final Biome biome) {
        if (biomeSource == null)
            return true;

        if (worldOreConfigSource != worldOreConfigTarget && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName()))
            return true;

        return biomeSource != biome;
    }

    @Override
    public boolean shouldSet(final Ore ore) {
        return (worldOreConfigTarget != worldOreConfigSource && !worldOreConfigSource.getName().equals(worldOreConfigTarget.getName())) || biomeSource != null;
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