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

import de.derfrzocker.ore.control.api.*;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class BiomeOreSettingsYamlImpl implements ConfigurationSerializable, BiomeOreSettings {

    private static final String BIOME_KEY = "biome";

    @Getter
    @NonNull
    private final Biome biome;

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    public BiomeOreSettingsYamlImpl(final @NonNull Biome biome, final @NonNull Map<Ore, OreSettings> oreSettings) {
        this.biome = biome;

        oreSettings.forEach((key, value) -> this.oreSettings.put(key, value.clone()));
    }

    @Override
    public Optional<OreSettings> getOreSettings(final @NonNull Ore ore) {
        return Optional.ofNullable(oreSettings.get(ore));
    }

    @Override
    public void setOreSettings(final @NonNull OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }

    @Override
    public BiomeOreSettings clone() {
        return new BiomeOreSettingsYamlImpl(biome, oreSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(BIOME_KEY, getBiome().toString());

        getOreSettings().entrySet().stream().filter(entry -> !entry.getValue().getSettings().isEmpty() || !entry.getValue().isActivated()).
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(entry.getKey(), entry.getValue().getSettings());
                    oreSettingsYaml.setActivated(entry.getValue().isActivated());
                    return oreSettingsYaml;
                }).forEach(value -> map.put(value.getOre().toString(), value));

        return map;
    }


    @SuppressWarnings("Duplicates")
    public static BiomeOreSettingsYamlImpl deserialize(final @NonNull Map<String, Object> map) {
        final Map<Ore, OreSettings> oreSettings = new HashMap<>();
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        map.entrySet().stream().filter(entry -> service.isOre(entry.getKey())).
                forEach(entry -> oreSettings.put(Ore.valueOf(entry.getKey().toUpperCase()), (OreSettings) entry.getValue()));

        return new BiomeOreSettingsYamlImpl(Biome.valueOf(((String) map.get(BIOME_KEY)).toUpperCase()), oreSettings);
    }

}
