package de.derfrzocker.ore.control.api;

import java.util.Map;
import java.util.Optional;

public interface OreSettings extends Cloneable {

    Ore getOre();

    Optional<Integer> getValue(Setting setting);

    void setValue(Setting setting, int value);

    Map<Setting, Integer> getSettings();

}
