package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class OreSettingsImpl implements OreSettings {

    @NonNull
    private final Ore ore;

    private int veinSize = 0;

    private int veinsPerChunk = 0;

    private int minimumHeight = 0;

    private int heightRange = 0;

    private int heightSubtractValue = 0;

    @Override
    public OreSettingsImpl clone() {
        return new OreSettingsImpl(ore, veinSize, veinsPerChunk, minimumHeight, heightRange, heightSubtractValue);
    }

}
