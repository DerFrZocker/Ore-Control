package de.derfrzocker.feature.impl.v1_18_R1.value.heightmap;

import de.derfrzocker.feature.common.AbstractValue;
import net.minecraft.world.level.levelgen.Heightmap;

public abstract class HeightmapValue extends AbstractValue<HeightmapValue, HeightmapType, Heightmap.Types> {

    @Override
    public abstract HeightmapValue clone();
}
