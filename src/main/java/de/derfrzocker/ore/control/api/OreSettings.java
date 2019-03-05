package de.derfrzocker.ore.control.api;

import java.util.Map;
import java.util.Optional;

/**
 * The OreSettings class holds the different Settings for the Ores,
 * that are needed for the Ore Generation.
 */
public interface OreSettings extends Cloneable {

    /**
     * @return the Ore
     */
    Ore getOre();

    /**
     * If this OreSettings contains the value of the given Setting,
     * it returns an Optional that contains the value,
     * otherwise it return an empty Optional.
     *
     * @param setting which must be non-null
     * @return an Optional that hold the value of the given Setting,
     * or an empty Optional if the OreSetting not contain the given Setting.
     * @throws NullPointerException if setting is null
     */
    Optional<Integer> getValue(Setting setting);

    /**
     * This adds the given Setting with the given value to this OreSettings.
     * If this Object already have a value for the given Setting,
     * than it replaced the old value with given value.
     *
     * @param setting which must be non-null
     * @param value   for the given Setting
     * @throws NullPointerException if setting is null
     */
    void setValue(Setting setting, int value);

    /**
     * @return the  Map with all Settings and values  that this  OreSettings have.
     */
    Map<Setting, Integer> getSettings();

    /**
     * Return if this Ore should be generated or not.
     *
     * @return true if the Ore should generated,
     * false if not.
     */
    boolean isActivated();

    /**
     * Set if the Ore of this OreSetting should be generated or not.
     *
     * @param status true for generate, false for not-generate.
     */
    void setActivated(boolean status);

    /**
     * Clones all Settings and values of the OreSetting to a new one.
     *
     * @return a new OreSettings
     */
    OreSettings clone();

}
