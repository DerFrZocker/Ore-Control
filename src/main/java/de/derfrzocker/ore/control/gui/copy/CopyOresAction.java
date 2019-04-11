package de.derfrzocker.ore.control.gui.copy;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.BiomeGui;
import de.derfrzocker.ore.control.gui.InventoryGui;
import de.derfrzocker.ore.control.gui.WorldConfigGui;
import de.derfrzocker.ore.control.utils.OreControlUtil;
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
    public Ore getOreSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSettingTarget(Setting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBiomesTarget(Biome[] biomes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setOreTarget(Ore ore) {
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
                        OreControlUtil.copy(worldOreConfigSource, worldOreConfigTarget, ore, biomeSource, ore);
            }

            status++;
            OreControl.getService().saveWorldOreConfig(worldOreConfigSource);
            inventoryGui.closeSync(humanEntity);
        }

    }

}
