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

package de.derfrzocker.ore.control.impl.v1_16_R2;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import net.minecraft.server.v1_16_R2.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
class NMSReplacer_v1_16_R2 {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    NMSReplacer_v1_16_R2(@NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");

        this.serviceSupplier = serviceSupplier;
    }

    void replaceNMS() {
        final IRegistryWritable<BiomeBase> registry = getRegistry().b(IRegistry.ay);
        for (final BiomeBase biomeBase : registry) {
            try {
                replaceBase(biomeBase, registry.getKey(biomeBase));
            } catch (final Exception e) {
                throw new RuntimeException("Unexpected error while hook in NMS for Biome: " + biomeBase, e);
            }
        }
    }

    private void replaceBase(@NotNull final BiomeBase base, MinecraftKey minecraftKey) throws NoSuchFieldException, IllegalAccessException {
        final Biome biome;

        try {
            biome = Biome.valueOf(minecraftKey.getKey().toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }

        final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> decorations = base.e().c();

        final List<List<Supplier<WorldGenFeatureConfigured<?, ?>>>> newDecorations = new ArrayList<>();

        for (final List<Supplier<WorldGenFeatureConfigured<?, ?>>> list : decorations) {
            final List<Supplier<WorldGenFeatureConfigured<?, ?>>> newList = new ArrayList<>();
            newDecorations.add(decorations.indexOf(list), newList);

            for (final Supplier<WorldGenFeatureConfigured<?, ?>> featureSupplier : list) {
                final WorldGenFeatureConfigured<?, ?> feature = featureSupplier.get();

                WorldGenFeatureConfigured<?, ?> newFeature = check(biome, feature);

                if (newFeature != null) {
                    addToList(newList, list.indexOf(featureSupplier), newFeature);
                } else {
                    newList.add(list.indexOf(featureSupplier), featureSupplier);
                }
            }
        }

        final Field field = getField(BiomeSettingsGeneration.class, "f");
        field.setAccessible(true);
        field.set(base.e(), newDecorations);
    }

    private void addToList(List<Supplier<WorldGenFeatureConfigured<?, ?>>> list, int index, WorldGenFeatureConfigured<?, ?> feature) {
        list.add(index, () -> feature);
    }

    @Nullable
    private WorldGenFeatureConfigured<?, ?> check(final Biome biome, WorldGenFeatureConfigured<?, ?> feature) {
        //Not the best method to do this, but hey it works
        final IRegistryWritable<WorldGenFeatureConfigured<?, ?>> registry = getRegistry().b(IRegistry.au);
        feature = RegistryGeneration.e.get(registry.getKey(feature));

        if (feature == null) {
            return null;
        }

        if (feature == BiomeDecoratorGroups.ORE_GOLD_DELTAS || feature == BiomeDecoratorGroups.ORE_GOLD_NETHER) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.NETHER_GOLD, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_QUARTZ_DELTAS || feature == BiomeDecoratorGroups.ORE_QUARTZ_NETHER) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.NETHER_QUARTZ, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_DIRT) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.DIRT, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_GRAVEL) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.GRAVEL, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_GRANITE) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.GRANITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_DIORITE) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.DIORITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_ANDESITE) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.ANDESITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_COAL) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.COAL, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_IRON) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.IRON, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_GOLD_EXTRA) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.GOLD_BADLANDS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_GOLD) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.GOLD, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_REDSTONE) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.REDSTONE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_DIAMOND) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.DIAMOND, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_INFESTED) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.INFESTED_STONE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_DEBRIS_SMALL) {
            return new WorldGenFeatureConfigured<>(new RangeWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.ANCIENT_DEBRIS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_DEBRIS_LARGE) {
            return new WorldGenFeatureConfigured<>(new DepthAveragedWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.ANCIENT_DEBRIS_2, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_LAPIS) {
            return new WorldGenFeatureConfigured<>(new DepthAveragedWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.LAPIS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_EMERALD) {
            return new WorldGenFeatureConfigured<>(new EmeraldWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.EMERALD, serviceSupplier, getWorldGenFeatureEmeraldConfiguration(feature)), null);
        }
        if (feature == BiomeDecoratorGroups.ORE_MAGMA) {
            return new WorldGenFeatureConfigured<>(new MagmaWorldGeneratorOverrider(WorldGenFeatureEmptyConfiguration.a, biome, Ore.MAGMA, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }

        return null;
    }

    private WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> getWorldGenFeatureOreConfiguration(final WorldGenFeatureConfigured<?, ?> feature) {
        if (feature.c() instanceof WorldGenFeatureOreConfiguration) {
            return (WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?>) feature;
        }

        if (feature.c() instanceof WorldGenFeatureCompositeConfiguration) {
            final WorldGenFeatureCompositeConfiguration compositeConfiguration = (WorldGenFeatureCompositeConfiguration) feature.c();

            return getWorldGenFeatureOreConfiguration(compositeConfiguration.b.get());
        }

        throw new RuntimeException("No WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> found");
    }

    private WorldGenFeatureConfigured<WorldGenFeatureReplaceBlockConfiguration, ?> getWorldGenFeatureEmeraldConfiguration(final WorldGenFeatureConfigured<?, ?> feature) {
        if (feature.c() instanceof WorldGenFeatureReplaceBlockConfiguration) {
            return (WorldGenFeatureConfigured<WorldGenFeatureReplaceBlockConfiguration, ?>) feature;
        }

        if (feature.c() instanceof WorldGenFeatureCompositeConfiguration) {
            final WorldGenFeatureCompositeConfiguration compositeConfiguration = (WorldGenFeatureCompositeConfiguration) feature.c();

            return getWorldGenFeatureEmeraldConfiguration(compositeConfiguration.b.get());
        }

        throw new RuntimeException("No WorldGenFeatureConfigured<WorldGenFeatureOreConfiguration, ?> found");
    }

    @SuppressWarnings("rawtypes")
    private Field getField(@NotNull final Class clazz, @NotNull final String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
            final Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    @Nullable
    private IRegistryCustom iRegistryCustom;

    @NotNull
    private IRegistryCustom getRegistry() {
        if (iRegistryCustom != null) {
            return iRegistryCustom;
        }

        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();

        IRegistryCustom registryCustom = null;

        // 1.16.2 use the method name 'aX' and 1.16.3 'getCustomRegistry'
        try {
            registryCustom = server.getCustomRegistry();
        } catch (NoSuchMethodError e) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess") Method registryCustomMethod = MinecraftServer.class.getDeclaredMethod("aX");

                registryCustom = (IRegistryCustom) registryCustomMethod.invoke(server);

            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException noSuchFieldException) {
                throw new RuntimeException("Cannot find IRegistryCustom", e);
            }
        }

        if (registryCustom == null) {
            throw new RuntimeException("Cannot find IRegistryCustom");
        }

        return iRegistryCustom = registryCustom;
    }

}
