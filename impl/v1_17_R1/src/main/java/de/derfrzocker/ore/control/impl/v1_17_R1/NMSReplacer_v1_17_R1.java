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

package de.derfrzocker.ore.control.impl.v1_17_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Features;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


@SuppressWarnings("Duplicates")
class NMSReplacer_v1_17_R1 {

    @NotNull
    private final Supplier<OreControlService> serviceSupplier;

    NMSReplacer_v1_17_R1(@NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(serviceSupplier, "Service Supplier cannot be null");

        this.serviceSupplier = serviceSupplier;
    }

    void replaceNMS() {
        final Registry<net.minecraft.world.level.biome.Biome> registry = getRegistry().registryOrThrow(Registry.BIOME_REGISTRY);
        for (final net.minecraft.world.level.biome.Biome biomeBase : registry) {
            try {
                replaceBase(biomeBase, registry.getKey(biomeBase));
            } catch (final Exception e) {
                throw new RuntimeException("Unexpected error while hook in NMS for Biome: " + biomeBase, e);
            }
        }
    }

    private void replaceBase(@NotNull final net.minecraft.world.level.biome.Biome base, ResourceLocation minecraftKey) throws NoSuchFieldException, IllegalAccessException {
        final Biome biome;

        try {
            biome = Biome.valueOf(minecraftKey.getPath().toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }

        final List<List<Supplier<ConfiguredFeature<?, ?>>>> decorations = base.getGenerationSettings().features();

        final List<List<Supplier<ConfiguredFeature<?, ?>>>> newDecorations = new ArrayList<>();

        for (final List<Supplier<ConfiguredFeature<?, ?>>> list : decorations) {
            final List<Supplier<ConfiguredFeature<?, ?>>> newList = new ArrayList<>();
            newDecorations.add(decorations.indexOf(list), newList);

            for (final Supplier<ConfiguredFeature<?, ?>> featureSupplier : list) {
                final ConfiguredFeature<?, ?> feature = featureSupplier.get();

                ConfiguredFeature<?, ?> newFeature = check(biome, feature);

                if (newFeature != null) {
                    addToList(newList, list.indexOf(featureSupplier), newFeature);
                } else {
                    newList.add(list.indexOf(featureSupplier), featureSupplier);
                }
            }
        }

        final Field field = getField(BiomeGenerationSettings.class, "f");
        field.setAccessible(true);
        field.set(base.getGenerationSettings(), newDecorations);
    }

    private void addToList(List<Supplier<ConfiguredFeature<?, ?>>> list, int index, ConfiguredFeature<?, ?> feature) {
        list.add(index, () -> feature);
    }

    @Nullable
    private ConfiguredFeature<?, ?> check(final Biome biome, ConfiguredFeature<?, ?> feature) {
        //Not the best method to do this, but hey it works
        final Registry<ConfiguredFeature<?, ?>> registry = getRegistry().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
        feature = BuiltinRegistries.CONFIGURED_FEATURE.get(registry.getKey(feature));

        if (feature == null) {
            return null;
        }

        if (feature == Features.ORE_GOLD_DELTAS || feature == Features.ORE_GOLD_NETHER) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.NETHER_GOLD, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_QUARTZ_DELTAS || feature == Features.ORE_QUARTZ_NETHER) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.NETHER_QUARTZ, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_DIRT) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.DIRT, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_GRAVEL) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.GRAVEL, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_GRANITE) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.GRANITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_DIORITE) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.DIORITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_ANDESITE) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.ANDESITE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_COAL) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.COAL, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_IRON) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.IRON, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_GOLD_EXTRA) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.GOLD_BADLANDS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_GOLD) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.GOLD, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_REDSTONE) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.REDSTONE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_DIAMOND) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.DIAMOND, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_INFESTED) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.INFESTED_STONE, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_DEBRIS_SMALL) {
            return new ConfiguredFeature<>(new RangeWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.ANCIENT_DEBRIS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_DEBRIS_LARGE) {
            return new ConfiguredFeature<>(new DepthAveragedWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.ANCIENT_DEBRIS_2, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_LAPIS) {
            return new ConfiguredFeature<>(new DepthAveragedWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.LAPIS, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }
        if (feature == Features.ORE_EMERALD) {
            return new ConfiguredFeature<>(new EmeraldWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.EMERALD, serviceSupplier, getWorldGenFeatureEmeraldConfiguration(feature)), null);
        }
        if (feature == Features.ORE_MAGMA) {
            return new ConfiguredFeature<>(new MagmaWorldGeneratorOverrider(NoneFeatureConfiguration.CODEC, biome, Ore.MAGMA, serviceSupplier, getWorldGenFeatureOreConfiguration(feature)), null);
        }

        return null;
    }

    private ConfiguredFeature<OreConfiguration, ?> getWorldGenFeatureOreConfiguration(final ConfiguredFeature<?, ?> feature) {
        if (feature.config() instanceof OreConfiguration) {
            return (ConfiguredFeature<OreConfiguration, ?>) feature;
        }

        if (feature.config() instanceof DecoratedFeatureConfiguration) {
            final DecoratedFeatureConfiguration compositeConfiguration = (DecoratedFeatureConfiguration) feature.config();

            return getWorldGenFeatureOreConfiguration(compositeConfiguration.feature.get());
        }

        throw new RuntimeException("No ConfiguredFeature<OreConfiguration, ?> found");
    }

    private ConfiguredFeature<ReplaceBlockConfiguration, ?> getWorldGenFeatureEmeraldConfiguration(final ConfiguredFeature<?, ?> feature) {
        if (feature.config() instanceof ReplaceBlockConfiguration) {
            return (ConfiguredFeature<ReplaceBlockConfiguration, ?>) feature;
        }

        if (feature.config() instanceof DecoratedFeatureConfiguration) {
            final DecoratedFeatureConfiguration compositeConfiguration = (DecoratedFeatureConfiguration) feature.config();

            return getWorldGenFeatureEmeraldConfiguration(compositeConfiguration.feature.get());
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

    @NotNull
    private RegistryAccess getRegistry() {
        final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();

        return server.registryAccess();
    }

}