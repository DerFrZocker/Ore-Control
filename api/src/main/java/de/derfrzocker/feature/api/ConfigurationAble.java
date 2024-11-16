package de.derfrzocker.feature.api;

import org.bukkit.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents an object which can have a {@link Configuration}.
 */
public interface ConfigurationAble extends Keyed {

    /**
     * Returns an unmodifiable set containing all
     * {@link Setting settings} this object will use.
     *
     * @return the settings which will be used.
     */
    @NotNull
    Set<Setting> getSettings();

    /**
     * Creates and returns a new empty configuration,
     * for this object.
     *
     * @return a new empty configuration
     */
    @NotNull
    Configuration createEmptyConfiguration();
}
