/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SerializableAs("OreControl#OreSettings")
public class OreSettingsYamlImpl implements ConfigurationSerializable, OreSettings {

    private static final String ORE_KEY = "ore";
    private static final String STATUS_KEY = "status";
    private static final String SETTINGS_KEY = "settings";

    @NotNull
    private final Map<Setting, Double> settings = new ConcurrentHashMap<>();
    @NotNull
    private final Ore ore;
    private boolean activated = true;

    public OreSettingsYamlImpl(@NotNull final Ore ore) {
        Validate.notNull(ore, "Ore cannot be null");

        this.ore = ore;
    }

    public OreSettingsYamlImpl(@NotNull final Ore ore, @NotNull final Map<Setting, Double> settings) {
        this(ore);
        Validate.notNull(settings, "Settings map cannot be null");

        this.settings.putAll(settings);
    }

    @NotNull
    public static OreSettingsYamlImpl deserialize(@NotNull final Map<String, Object> map) {
        Validate.notNull(map, "Map cannot be null");

        final Map<Setting, Double> settings = new LinkedHashMap<>();

        // if no settings key is present we have
        // a) no settings in this ore config
        // b) the old storage type
        if (map.containsKey(SETTINGS_KEY)) {
            // new storage type
            ((Map<String, Object>) map.get(SETTINGS_KEY)).forEach((setting, value) -> settings.put(Setting.valueOf(setting.toUpperCase()), NumberConversions.toDouble(value)));
        } else {
            // old storage type
            final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

            Validate.notNull(service, "OreControlService cannot be null");

            map.entrySet().stream().filter(entry -> isSetting(entry.getKey())).
                    forEach(entry -> settings.put(Setting.valueOf(entry.getKey().toUpperCase()), NumberConversions.toDouble(entry.getValue())));
        }

        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(Ore.valueOf(((String) map.get(ORE_KEY)).toUpperCase()), settings);

        if (map.containsKey(STATUS_KEY)) {
            oreSettingsYaml.setActivated((boolean) map.get(STATUS_KEY));
        }

        return oreSettingsYaml;
    }

    private static boolean isSetting(@Nullable final String string) {
        if (string == null) {
            return false;
        }

        try {
            Setting.valueOf(string.toUpperCase());
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    @NotNull
    @Override
    public Ore getOre() {
        return this.ore;
    }

    @NotNull
    @Override
    public Optional<Double> getValue(@NotNull final Setting setting) {
        Validate.notNull(setting, "Setting cannot be null");

        return Optional.ofNullable(getSettings().get(setting));
    }

    @Override
    public void setValue(@NotNull final Setting setting, final double value) {
        Validate.notNull(setting, "Setting cannot be null");

        this.settings.put(setting, value);
    }

    @NotNull
    @Override
    public Map<Setting, Double> getSettings() {
        return this.settings;
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }

    @Override
    public void setActivated(final boolean status) {
        this.activated = status;
    }

    @NotNull
    @Override
    public OreSettingsYamlImpl clone() {
        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(getOre(), getSettings());

        oreSettingsYaml.setActivated(isActivated());

        return oreSettingsYaml;
    }

    @Override
    public boolean equals(@Nullable final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final OreSettingsYamlImpl that = (OreSettingsYamlImpl) object;

        return isActivated() == that.isActivated() &&
                getSettings().equals(that.getSettings()) &&
                getOre() == that.getOre();
    }

    @Override
    public int hashCode() {
        return getOre().hashCode();
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> serialize = new HashMap<>();

        serialize.put(ORE_KEY, getOre().toString());

        if (!isActivated()) {
            serialize.put(STATUS_KEY, false);
        }

        final Map<Setting, Double> settingsMap = getSettings();
        if (!settingsMap.isEmpty()) {
            final Map<String, Double> data = new LinkedHashMap<>();

            settingsMap.forEach((key, value) -> data.put(key.toString(), value));

            serialize.put(SETTINGS_KEY, data);
        }

        return serialize;
    }

}
