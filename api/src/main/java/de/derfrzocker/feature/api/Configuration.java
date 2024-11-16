package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.SaveAble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Holds values for features.
 */
public interface Configuration extends SaveAble {

    /**
     * Returns the owner of this configuration.
     *
     * @return the owner of this Configuration.
     */
    @NotNull
    ConfigurationAble getOwner();

    /**
     * Returns an unmodifiable set containing the allowed
     * {@link Setting settings} for this configuration.
     *
     * @return the allowed settings.
     */
    @NotNull
    Set<Setting> getSettings();

    /**
     * Returns the value associated with the given setting.
     * If this configuration allows but does not have a value set,
     * it will return null.
     * <br>
     * If this configuration does not allow the given setting,
     * an exception will be thrown. Use {@link #getSettings()}
     * for a set of allowed settings.
     *
     * @param setting The setting to get the value from.
     * @return the value associated with the setting.
     * @throws IllegalArgumentException if the given setting is not allowed in this configuration.
     */
    @Nullable
    Value<?, ?, ?> getValue(@NotNull Setting setting);

    /**
     * Sets the value for the given setting to the given value.
     * An exception is thrown when the given setting is not allowed by
     * this configuration or when the given value is not of the right type.
     * Use {@link #getSettings()} for a set of allowed settings.
     *
     * @param setting The setting which should get the new value.
     * @param value   The new value for the given setting.
     * @throws IllegalArgumentException if the given setting is not allowed in this configuration.
     * @throws IllegalArgumentException if the given value is not the right type for the setting.
     */
    void setValue(@NotNull Setting setting, @Nullable Value<?, ?, ?> value);
}
