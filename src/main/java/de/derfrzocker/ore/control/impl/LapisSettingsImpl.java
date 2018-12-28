package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.LapisSettings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LapisSettingsImpl implements LapisSettings {

    private int veinSize = 0;

    private int veinsPerChunk = 0;

    private int heightRange = 0;

    private int heightCenter = 0;

    @Override
    public LapisSettingsImpl clone() {
        return new LapisSettingsImpl(veinSize, veinsPerChunk, heightRange, heightCenter);
    }

}
