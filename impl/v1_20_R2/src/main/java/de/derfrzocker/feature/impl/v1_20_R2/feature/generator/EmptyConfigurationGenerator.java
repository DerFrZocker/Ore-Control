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

package de.derfrzocker.feature.impl.v1_20_R2.feature.generator;

import com.mojang.serialization.Codec;
import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.common.feature.generator.configuration.EmptyFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class EmptyConfigurationGenerator extends MinecraftFeatureGenerator<NoneFeatureConfiguration, EmptyFeatureConfiguration> {

    private EmptyConfigurationGenerator(Registries registries, Feature<NoneFeatureConfiguration> feature, String name) {
        super(registries, feature, name);
    }

    public static EmptyConfigurationGenerator createGlowstoneBlob(Registries registries) {
        return new EmptyConfigurationGenerator(registries, Feature.GLOWSTONE_BLOB, "glowstone_blob");
    }

    @Override
    public @NotNull Set<Setting> getSettings() {
        return Collections.emptySet();
    }

    @Override
    public @NotNull Configuration createEmptyConfiguration() {
        return new EmptyFeatureConfiguration(this);
    }

    @Override
    public EmptyFeatureConfiguration mergeConfig(EmptyFeatureConfiguration first, EmptyFeatureConfiguration second) {
        return first;
    }

    @Override
    public Codec<EmptyFeatureConfiguration> createCodec(Registries registries) {
        return Codec.unit(() -> new EmptyFeatureConfiguration(this));
    }

    @Override
    public NoneFeatureConfiguration createConfiguration(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull EmptyFeatureConfiguration configuration) {
        return NoneFeatureConfiguration.INSTANCE;
    }
}
