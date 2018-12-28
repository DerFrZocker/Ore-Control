package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.EmeraldSettings;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmeraldSettingsImpl implements EmeraldSettings {

    private int minimumOresPerChunk = 0;

    private int oresPerChunkRange = 0;

    private int heightRange = 0;

    private int minimumHeight = 0;

    @Override
    public EmeraldSettingsImpl clone(){
        return new EmeraldSettingsImpl(minimumOresPerChunk, oresPerChunkRange, heightRange, minimumHeight);
    }

}
