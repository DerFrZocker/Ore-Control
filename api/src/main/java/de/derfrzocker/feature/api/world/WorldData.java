package de.derfrzocker.feature.api.world;

import de.derfrzocker.feature.api.config.world.WorldId;
import de.derfrzocker.ore.control.api.Biome;

import java.util.List;

public interface WorldData {

    WorldId id();

    List<Biome> possibleBiomes();
}
