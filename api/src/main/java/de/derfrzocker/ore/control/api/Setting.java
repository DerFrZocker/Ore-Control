package de.derfrzocker.ore.control.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Setting {

    VEIN_SIZE(1),
    VEINS_PER_CHUNK(0),
    HEIGHT_RANGE(1),
    HEIGHT_CENTER(0),
    MINIMUM_ORES_PER_CHUNK(-1),
    ORES_PER_CHUNK_RANGE(1),
    MINIMUM_HEIGHT(0),
    HEIGHT_SUBTRACT_VALUE(0),
    VEINS_PER_BIOME(0);

    @Getter
    private final int minimumValue;

}
