/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package de.derfrzocker.feature.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Holds values for features.
 */
public interface Configuration {

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
     * @param value The new value for the given setting.
     * @throws IllegalArgumentException if the given setting is not allowed in this configuration.
     * @throws IllegalArgumentException if the given value is not the right type for the setting.
     */
    void setValue(@NotNull Setting setting, @Nullable Value<?, ?, ?> value);

    /**
     * Checks and returns true if this configuration or any value in this configuration
     * is dirty.
     *
     * @return true if this configuration is dirty otherwise false.
     */
    boolean isDirty();

    /**
     * Marks this configuration and any value this configuration holds as saved and not dirty.
     */
    void saved();
}
