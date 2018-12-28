package de.derfrzocker.ore.control.api;

public interface EmeraldSettings extends Cloneable {

    default Ore getOre() {
        return Ore.EMERALD;
    }

    int getMinimumOresPerChunk();

    int getOresPerChunkRange();

    int getHeightRange();

    int getMinimumHeight();

    void setMinimumOresPerChunk(int amount);

    void setOresPerChunkRange(int amount);

    void setHeightRange(int amount);

    void setMinimumHeight(int amount);

}
