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

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings {

    @NonNull
    private final YamlConfiguration yaml;

    public Settings(final YamlConfiguration yaml) {
        this.yaml = yaml;
    }

    public OreSettings getDefaultSettings(final @NonNull Ore ore) {
        return get(String.format("defaults.%s", ore.toString().toLowerCase()));
    }

    private OreSettings get(final @NonNull String key) {
        final OreSettingsYamlImpl settings = yaml.getSerializable(key, OreSettingsYamlImpl.class);

        if (settings == null)
            throw new NullPointerException("settings: " + key + " can't be null!");

        return settings.clone();
    }

}
