package de.derfrzocker.ore.control.api;

public interface OreSettings extends Cloneable {

    Ore getOre();

    int getVeinSize();

    int getVeinsPerChunk();

    int getMinimumHeight();

    int getHeightRange();

    int getHeightSubtractValue();

    void setVeinSize(int amount);

    void setVeinsPerChunk(int amount);

    void setMinimumHeight(int amount);

    void setHeightRange(int amount);

    void setHeightSubtractValue(int amount);

}
