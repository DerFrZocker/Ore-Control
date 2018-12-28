package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.EmeraldSettings;
import de.derfrzocker.ore.control.api.LapisSettings;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class WorldOreConfigImpl implements WorldOreConfig {

    @NonNull
    private final String world;

    @NonNull
    private OreSettings diamondSettings;

    @NonNull
    private OreSettings redstoneSettings;

    @NonNull
    private OreSettings coalSettings;

    @NonNull
    private OreSettings goldSettings;

    @NonNull
    private OreSettings badlandsGoldSettings;

    @NonNull
    private OreSettings ironSettings;

    @NonNull
    private LapisSettings lapisSettings;

    @NonNull
    private EmeraldSettings emeraldSettings;

}
