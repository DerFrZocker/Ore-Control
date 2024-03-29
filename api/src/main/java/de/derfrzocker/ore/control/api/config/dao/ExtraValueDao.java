/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.api.config.dao;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ExtraValueDao {

    private static final String EXTRA_VALUES_FILE = "extra_values/extra_values.yml";

    public Optional<ExtraValues> getExtraValues(ConfigInfo configInfo) {
        File extraValuesFile = new File(configInfo.getDataDirectory(), EXTRA_VALUES_FILE);

        if (!extraValuesFile.exists()) {
            return Optional.empty();
        }

        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(extraValuesFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Cannot load configuration from file " + extraValuesFile, e);
        }

        Optional<Boolean> generatedBigOreVeins;
        if (config.contains("generated-big-ore-veins")) {
            generatedBigOreVeins = Optional.of(config.getBoolean("generated-big-ore-veins"));
        } else {
            generatedBigOreVeins = Optional.empty();
        }

        return Optional.of(new ExtraValues(generatedBigOreVeins));
    }

    public void save(ConfigInfo configInfo, ExtraValues extraValues) {
        File extraValuesFile = new File(configInfo.getDataDirectory(), EXTRA_VALUES_FILE);

        if (!extraValuesFile.exists()) {
            extraValuesFile.getParentFile().mkdirs();
            try {
                extraValuesFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(extraValuesFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        config.set("generated-big-ore-veins", extraValues.shouldGeneratedBigOreVeins().orElse(null));

        try {
            config.save(extraValuesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
