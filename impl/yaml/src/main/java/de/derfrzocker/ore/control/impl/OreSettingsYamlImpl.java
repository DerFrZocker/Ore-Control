/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.spigot.utils.NumberUtil;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class OreSettingsYamlImpl implements ConfigurationSerializable, OreSettings {

    private static final String ORE_KEY = "ore";
    private static final String STATUS_KEY = "status";

    @Getter
    private final Map<Setting, Double> settings = new ConcurrentHashMap<>();

    @NonNull
    @Getter
    private final Ore ore;

    @Getter
    @Setter
    private boolean activated = true;

    public OreSettingsYamlImpl(final @NonNull Ore ore, final @NonNull Map<Setting, Double> settings) {
        this.ore = ore;
        this.settings.putAll(settings);
    }

    @Override
    public Optional<Double> getValue(final @NonNull Setting setting) {
        return Optional.ofNullable(settings.get(setting));
    }

    @Override
    public void setValue(final @NonNull Setting setting, final double value) {
        settings.put(setting, value);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(ORE_KEY, getOre().toString());

        if (!activated)
            map.put(STATUS_KEY, false);

        getSettings().forEach((key, value) -> map.put(key.toString(), value));

        return map;
    }

    @Override
    public OreSettingsYamlImpl clone() {
        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(getOre(), getSettings());
        oreSettingsYaml.setActivated(isActivated());
        return oreSettingsYaml;
    }

    public static OreSettingsYamlImpl deserialize(final @NonNull Map<String, Object> map) {
        final Map<Setting, Double> settings = new LinkedHashMap<>();
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        map.entrySet().stream().filter(entry -> service.isSetting(entry.getKey())).
                forEach(entry -> settings.put(Setting.valueOf(entry.getKey().toUpperCase()), NumberConversions.toDouble(entry.getValue())));

        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(Ore.valueOf(((String) map.get(ORE_KEY)).toUpperCase()), settings);

        if (map.containsKey(STATUS_KEY))
            oreSettingsYaml.setActivated((boolean) map.get(STATUS_KEY));

        return oreSettingsYaml;
    }

}
