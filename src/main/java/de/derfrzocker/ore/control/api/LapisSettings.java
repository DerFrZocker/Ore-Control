package de.derfrzocker.ore.control.api;

public interface LapisSettings extends Cloneable {

    default Ore getOre() {
        return Ore.LAPIS;
    }

    int getVeinSize();

    int getVeinsPerChunk();

    int getHeightRange();

    int getHeightCenter();

    void setVeinSize(int amount);

    void setVeinsPerChunk(int amount);

    void setHeightRange(int amount);

    void setHeightCenter(int amount);


}
