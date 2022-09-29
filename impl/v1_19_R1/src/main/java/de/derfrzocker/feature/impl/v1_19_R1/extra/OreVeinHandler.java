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

package de.derfrzocker.feature.impl.v1_19_R1.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.impl.v1_19_R1.NMSReflectionNames;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.lang.reflect.Field;
import java.util.Optional;

public class OreVeinHandler implements Listener {

    private final static Field CHUNK_GENERATOR_FIELD;

    static {
        try {
            CHUNK_GENERATOR_FIELD = ChunkMap.class.getDeclaredField(NMSReflectionNames.CHUNK_MAP_GENERATOR);
            CHUNK_GENERATOR_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final ConfigManager configManager;

    public OreVeinHandler(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInit(WorldInitEvent event) {
        if (!(((CraftWorld) event.getWorld()).getHandle().getChunkSource().chunkMap.generator instanceof NoiseBasedChunkGenerator noiseChunkGenerator)) {
            return;
        }

        ConfigInfo configInfo = configManager.getOrCreateConfigInfo(event.getWorld().getName());
        Optional<ExtraValues> optionalExtraValues = configManager.getExtraValues(configInfo);

        if (optionalExtraValues.isEmpty()) {
            return;
        }

        if (optionalExtraValues.get().shouldGeneratedBigOreVeins().isEmpty()) {
            return;
        }

        if (optionalExtraValues.get().shouldGeneratedBigOreVeins().get() == noiseChunkGenerator.settings.value().oreVeinsEnabled()) {
            return;
        }

        NoiseGeneratorSettings old = noiseChunkGenerator.settings.value();
        NoiseGeneratorSettings newSettings = new NoiseGeneratorSettings(old.noiseSettings(), old.defaultBlock(), old.defaultFluid(),
                old.noiseRouter(), old.surfaceRule(), old.spawnTarget(), old.seaLevel(), old.disableMobGeneration(), old.aquifersEnabled(), !old.oreVeinsEnabled(), old.useLegacyRandomSource());

        NoiseBasedChunkGenerator newGenerator = new NoiseBasedChunkGenerator(noiseChunkGenerator.structureSets, noiseChunkGenerator.noises, noiseChunkGenerator.getBiomeSource(), Holder.direct(newSettings));

        try {
            newGenerator.conf = noiseChunkGenerator.conf;
        } catch (Throwable ignore) {
        }

        try {
            CHUNK_GENERATOR_FIELD.set(((CraftWorld) event.getWorld()).getHandle().getChunkSource().chunkMap, newGenerator);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
