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
import de.derfrzocker.spigot.utils.ChunkCoordIntPair;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NMSServiceImpl implements NMSService {

    private final Map<Ore, GenerationHandler> generationHandlerMap = new LinkedHashMap<>();
    @NotNull
    private final NMSUtil nMSUtil;
    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    public NMSServiceImpl(@NotNull final NMSUtil nMSUtil, @NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(nMSUtil, "NMSUtil can not be null");
        Validate.notNull(serviceSupplier, "Service Supplier can not be null");

        this.nMSUtil = nMSUtil;
        this.serviceSupplier = serviceSupplier;
    }

    @NotNull
    @Override
    public NMSUtil getNMSUtil() {
        return this.nMSUtil;
    }

    @Override
    public void registerGenerationHandler(@NotNull final Ore ore, @NotNull final GenerationHandler generationHandler) {
        Validate.notNull(ore, "Ore can not be null");
        Validate.notNull(generationHandler, "GenerationHandler can not be null");

        this.generationHandlerMap.put(ore, generationHandler);
    }

    @Override
    public void replaceNMS() {
        this.nMSUtil.replaceNMS();
    }

    @Override
    public boolean generate(@NotNull final World world, @NotNull final Biome biome, final Ore ore, @NotNull final ChunkCoordIntPair chunkCoordIntPair,
                            @NotNull final Object defaultConfiguration, @NotNull final Object defaultFeatureConfiguration,
                            @Nullable final BiFunction<Location, Integer, Boolean> generateFunction, @NotNull final BiFunction<Object, Object, Boolean> passFunction, @NotNull final Random random) {

        final OreControlService service = serviceSupplier.get();

        if (ore == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);


        final WorldOreConfig worldOreConfig = service.getWorldOreConfig(world).orElse(null);

        if (worldOreConfig == null)
            return passFunction.apply(defaultConfiguration, defaultFeatureConfiguration);

        try {
            if (!service.isActivated(worldOreConfig, biome, ore))
                return true;

            final GenerationHandler generationHandler = generationHandlerMap.get(ore);

            if (generationHandler == null) {
                throw new IllegalArgumentException("There is no GenerationHandler for ore '" + ore + "' registered");
            }

            return generationHandler.generate(world, worldOreConfig, service, biome, ore, chunkCoordIntPair, defaultConfiguration, defaultFeatureConfiguration, generateFunction, passFunction, random);

        } catch (final Exception e) {
            final StringBuilder errorMessage = new StringBuilder("Error while generate Chunk" +
                    ", Worldname: " + worldOreConfig.getName() +
                    ", Ore: " + ore +
                    ", Biome: " + biome);

            try {
                for (final Setting setting : ore.getSettings())
                    errorMessage.append(", ").append(setting).append(": ").append(service.getValue(worldOreConfig, biome, ore, setting));
            } catch (final Exception e1) {
                e1.printStackTrace();
            }

            final RuntimeException runtimeException = new RuntimeException(errorMessage.toString(), e);

            runtimeException.printStackTrace();

            throw runtimeException;
        }
    }

}
