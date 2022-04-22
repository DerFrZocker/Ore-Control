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

package de.derfrzocker.ore.control.impl.v1_18_R1;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.common.value.number.FixedFloatType;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerType;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedIntegerType;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerType;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerType;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerType;
import de.derfrzocker.feature.impl.v1_18_R1.feature.generator.OreFeatureGenerator;
import de.derfrzocker.feature.impl.v1_18_R1.feature.generator.ScatteredOreGenerator;
import de.derfrzocker.feature.impl.v1_18_R1.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.placement.*;
import de.derfrzocker.feature.impl.v1_18_R1.placement.configuration.CountModifierConfiguration;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightmap.FixedHeightmapType;
import de.derfrzocker.feature.impl.v1_18_R1.value.heightmap.HeightmapType;
import de.derfrzocker.feature.impl.v1_18_R1.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.impl.v1_18_R1.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.feature.impl.v1_18_R1.value.target.FixedTargetType;
import de.derfrzocker.feature.impl.v1_18_R1.value.target.TargetType;
import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.impl.v1_18_R1.feature.generator.OreFeatureGeneratorHook;
import de.derfrzocker.ore.control.impl.v1_18_R1.feature.generator.ScatteredOreFeatureGeneratorHook;
import de.derfrzocker.ore.control.impl.v1_18_R1.placement.*;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NMSReplacer_v1_18_R1 implements NMSReplacer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Codec<Config> configCodec;
    @NotNull
    private final OreControlRegistries registries;
    private final ConfigManager configManager;

    public NMSReplacer_v1_18_R1(@NotNull OreControlRegistries registries, ConfigManager configManager) {
        this.registries = registries;
        this.configManager = configManager;
        this.configCodec = configManager.getConfigCodec();
    }

    @Override
    public void register() {
        registerValueTypes();
        registerFeatureGenerators();
        registerPlacementModifier();
        registerFeatures();
        registerBiomes();
    }

    public void registerValueTypes() {
        registries.getValueTypeRegistry(IntegerType.class).register(FixedIntegerType.INSTANCE);
        registries.getValueTypeRegistry(IntegerType.class).register(FixedDoubleToIntegerType.INSTANCE);
        registries.getValueTypeRegistry(IntegerType.class).register(new UniformIntegerType(registries));
        registries.getValueTypeRegistry(IntegerType.class).register(new TrapezoidIntegerType(registries));
        registries.getValueTypeRegistry(IntegerType.class).register(new AboveBottomOffsetIntegerType(registries));
        registries.getValueTypeRegistry(IntegerType.class).register(new BelowTopOffsetIntegerType(registries));
        registries.getValueTypeRegistry(IntegerType.class).register(new WeightedListIntegerType(registries));
        registries.getValueTypeRegistry(TargetType.class).register(FixedTargetType.INSTANCE);
        registries.getValueTypeRegistry(HeightmapType.class).register(FixedHeightmapType.INSTANCE);
        registries.getValueTypeRegistry(FloatType.class).register(FixedFloatType.INSTANCE);
    }

    public void registerFeatureGenerators() {
        registries.getFeatureGeneratorRegistry().register(new OreFeatureGenerator(registries));
        registries.getFeatureGeneratorRegistry().register(new ScatteredOreGenerator(registries));
    }

    public void registerPlacementModifier() {
        registries.getPlacementModifierRegistry().register(new RarityModifier(registries));
        registries.getPlacementModifierRegistry().register(new SurfaceRelativeThresholdModifier(registries));
        registries.getPlacementModifierRegistry().register(new SurfaceWaterDepthModifier(registries));
        registries.getPlacementModifierRegistry().register(new CountModifier(registries));
        registries.getPlacementModifierRegistry().register(new HeightRangeModifier(registries));
    }

    public void registerFeatures() {
        Registry<PlacedFeature> placedFeatureRegistry = getRegistry().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        Registry<Feature<?>> featureRegistry = getRegistry().registryOrThrow(Registry.FEATURE_REGISTRY);
        Registry<PlacementModifierType<?>> placementModifierTypes = getRegistry().registryOrThrow(Registry.PLACEMENT_MODIFIER_REGISTRY);

        for (PlacedFeature placedFeature : placedFeatureRegistry) {
            Feature<?> feature = placedFeature.getFeatures().findFirst().get().feature();
            ResourceLocation resourceLocation = featureRegistry.getKey(feature);
            Optional<FeatureGenerator<?>> featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.fromString(resourceLocation.toString()));

            if (featureGenerator.isEmpty()) {
                continue;
            }

            boolean hasCount = false;
            List<FeaturePlacementModifier<?>> modifiers = new LinkedList<>();
            for (PlacementModifier modifier : placedFeature.getPlacement()) {
                if (modifier.type() == PlacementModifierType.COUNT) {
                    hasCount = true;
                }
                ResourceLocation placementModifierType = placementModifierTypes.getKey(modifier.type());
                Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString(placementModifierType.toString()));
                modifierOptional.ifPresent(modifiers::add);
            }

            if (!hasCount) {
                Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString("minecraft:count"));
                modifierOptional.ifPresent(modifiers::add);
            }

            registries.getFeatureRegistry().register(new de.derfrzocker.feature.api.Feature<>(NamespacedKey.fromString(placedFeatureRegistry.getKey(placedFeature).toString()), featureGenerator.get(), modifiers));
        }
    }

    public void registerBiomes() {
        Registry<PlacedFeature> placedFeatureRegistry = getRegistry().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        forEachBiome((biome, resourceLocation) -> {
            de.derfrzocker.ore.control.api.Biome bio = new de.derfrzocker.ore.control.api.Biome(CraftNamespacedKey.fromMinecraft(resourceLocation));

            biome.getGenerationSettings().features().forEach(featureSet -> {
                featureSet.forEach(holder -> {
                    PlacedFeature feature = holder.get();
                    ResourceLocation featureKey = placedFeatureRegistry.getKey(feature);
                    registries.getFeatureRegistry().get(CraftNamespacedKey.fromMinecraft(featureKey)).ifPresent(value -> bio.getFeatures().add(value));
                });
            });

            registries.getBiomeRegistry().register(bio);
        });
    }

    @Override
    public void saveDefaultValues(File directory) {
        File world = new File(directory, "world");
        File biomes = new File(directory, "biome");

        saveWorldValues(world);
        saveBiomeValues(biomes);
    }

    @Override
    public void hookIntoBiomes() {
        forEachBiome((biome, resourceLocation) -> {
            try {
                hookIntoBiome(biome, resourceLocation);
            } catch (final Exception e) {
                throw new RuntimeException("Unexpected error while hooking in NMS for Biome: " + biome, e);
            }
        });
    }

    @Override
    public Set<de.derfrzocker.ore.control.api.Biome> getBiomes(World world) {
        Set<de.derfrzocker.ore.control.api.Biome> biomes = new LinkedHashSet<>();
        ServerLevel level = ((CraftWorld) world).getHandle();
        Registry<Biome> registry = getRegistry().registryOrThrow(Registry.BIOME_REGISTRY);

        for (Biome biome : level.getChunkSource().chunkMap.generator.getBiomeSource().possibleBiomes()) {
            ResourceLocation key = registry.getKey(biome);

            biomes.add(registries.getBiomeRegistry().get(CraftNamespacedKey.fromMinecraft(key)).get());
        }

        return biomes;
    }

    private void saveWorldValues(File directory) {
        Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        for (Map.Entry<ResourceKey<PlacedFeature>, PlacedFeature> entry : registry.entrySet()) {
            ResourceLocation key = entry.getKey().location();
            File file = new File(directory, key.getNamespace() + "/" + key.getPath() + ".json");
            try {
                saveFeature(file, null, CraftNamespacedKey.fromMinecraft(key), entry.getValue());
            } catch (Exception e) {
                throw new RuntimeException(String.format("Error while saving feature '%s'", key), e);
            }
        }
    }

    private void saveBiomeValues(File directory) {
        forEachBiome((biome, biomeKey) -> {
            File biomeFile = new File(directory, biomeKey.getNamespace() + "/" + biomeKey.getPath());
            List<List<Supplier<PlacedFeature>>> decorations = biome.getGenerationSettings().features();
            for (List<Supplier<PlacedFeature>> list : decorations) {
                for (Supplier<PlacedFeature> featureSupplier : list) {
                    PlacedFeature feature = featureSupplier.get();
                    Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
                    ResourceLocation featureKey = registry.getKey(feature);
                    File featureFile = new File(biomeFile, featureKey.getNamespace() + "/" + featureKey.getPath() + ".json");
                    try {
                        saveFeature(featureFile, registries.getBiomeRegistry().get(CraftNamespacedKey.fromMinecraft(biomeKey)).get(), CraftNamespacedKey.fromMinecraft(featureKey), feature);
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Error while saving feature '%s' in biome '%s'", featureKey, biomeKey), e);
                    }
                }
            }
        });
    }

    private void saveFeature(File file, de.derfrzocker.ore.control.api.Biome biome, NamespacedKey featureKey, PlacedFeature feature) {
        ConfiguredFeature<?, ?> configuredFeature = feature.getFeatures().findFirst().orElse(null);

        if (configuredFeature == null) {
            return;
        }

        if (!(configuredFeature.feature() instanceof OreFeature) && !(configuredFeature.feature() instanceof ScatteredOreFeature)) {
            return;
        }

        file.getParentFile().mkdirs();

        OreConfiguration configuration = (OreConfiguration) configuredFeature.config();

        FeatureGenerator<?> featureGenerator;

        if (configuredFeature.feature() instanceof OreFeature) {
            featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.minecraft("ore")).get();
        } else if (configuredFeature.feature() instanceof ScatteredOreFeature) {
            featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.minecraft("scattered_ore")).get();
        } else {
            throw new RuntimeException("HOW?");
        }

        OreFeatureConfiguration featureConfiguration = new OreFeatureConfiguration(featureGenerator, null, new FixedDoubleToIntegerValue(configuration.size), new FixedFloatValue(configuration.discardChanceOnAirExposure));

        boolean hasCount = false;
        List<PlacementModifierConfiguration> placementConfiguration = new ArrayList<>();
        Registry<PlacementModifierType<?>> placementModifierTypes = getRegistry().registryOrThrow(Registry.PLACEMENT_MODIFIER_REGISTRY);
        for (PlacementModifier placement : feature.getPlacement()) {
            ResourceLocation placementModifierType = placementModifierTypes.getKey(placement.type());
            Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString(placementModifierType.toString()));

            if (modifierOptional.isEmpty()) {
                return;
            }

            if (placement.type() == PlacementModifierType.RARITY_FILTER && placement instanceof RarityFilter) {
                placementConfiguration.add(RarityModifierHook.createDefaultConfiguration((RarityFilter) placement, modifierOptional.get()));
            }
            if (placement.type() == PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER && placement instanceof SurfaceRelativeThresholdFilter) {
                placementConfiguration.add(SurfaceRelativeThresholdModifierHook.createDefaultConfiguration((SurfaceRelativeThresholdFilter) placement, modifierOptional.get()));
            }
            if (placement.type() == PlacementModifierType.SURFACE_WATER_DEPTH_FILTER && placement instanceof SurfaceWaterDepthFilter) {
                placementConfiguration.add(SurfaceWaterDepthModifierHook.createDefaultConfiguration((SurfaceWaterDepthFilter) placement, modifierOptional.get()));
            }
            if (placement.type() == PlacementModifierType.COUNT && placement instanceof CountPlacement) {
                placementConfiguration.add(CountModifierHook.createDefaultConfiguration((CountPlacement) placement, modifierOptional.get()));
                hasCount = true;
            }
            if (placement.type() == PlacementModifierType.HEIGHT_RANGE && placement instanceof HeightRangePlacement) {
                placementConfiguration.add(HeightRangeModifierHook.createDefaultConfiguration((HeightRangePlacement) placement, modifierOptional.get()));
            }
        }

        if (!hasCount) {
            Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString("minecraft:count"));

            if (modifierOptional.isEmpty()) {
                return;
            }

            placementConfiguration.add(0, CountModifierHook.createDefaultConfiguration(CountPlacement.of(1), modifierOptional.get()));
        }

        Config config = new Config(placementConfiguration, featureConfiguration);

        if (biome == null) {
            configManager.setDefaultConfig(featureKey, config);
        } else {
            configManager.setDefaultConfig(biome, featureKey, config);
        }

        if (file.exists()) {
            return;
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        DataResult<JsonElement> result = configCodec.encodeStart(JsonOps.INSTANCE, config);
        JsonElement element = result.get().left().get();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(element));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forEachBiome(BiConsumer<Biome, ResourceLocation> consumer) {
        Registry<Biome> registry = getRegistry().registryOrThrow(Registry.BIOME_REGISTRY);
        for (Biome biome : registry) {
            consumer.accept(biome, registry.getKey(biome));
        }
    }

    private void hookIntoBiome(@NotNull final Biome biome, ResourceLocation minecraftKey) throws NoSuchFieldException, IllegalAccessException {
        de.derfrzocker.ore.control.api.Biome oreControlBiome = registries.getBiomeRegistry().get(CraftNamespacedKey.fromMinecraft(minecraftKey)).get();
        List<List<Supplier<PlacedFeature>>> decorations = biome.getGenerationSettings().features();
        List<List<Supplier<PlacedFeature>>> newDecorations = new ArrayList<>();
        for (List<Supplier<PlacedFeature>> list : decorations) {
            List<Supplier<PlacedFeature>> newList = new ArrayList<>();

            for (Supplier<PlacedFeature> featureSupplier : list) {
                PlacedFeature feature = featureSupplier.get();

                PlacedFeature newFeature = check(oreControlBiome, minecraftKey, feature);

                if (newFeature != null) {
                    addToList(newList, list.indexOf(featureSupplier), newFeature);
                } else {
                    newList.add(featureSupplier);
                }
            }

            newDecorations.add(decorations.indexOf(list), newList);
        }

        {
            final Field field = getField(BiomeGenerationSettings.class, NMSReflectionNames.BIOME_GENERATION_SETTINGS_FEATURES);
            field.setAccessible(true);
            field.set(biome.getGenerationSettings(), newDecorations);
        }
        {
            final Field field = getField(BiomeGenerationSettings.class, NMSReflectionNames.BIOME_GENERATION_SETTINGS_FEATURE_SET);
            field.setAccessible(true);
            field.set(biome.getGenerationSettings(), newDecorations.stream().flatMap(Collection::stream).map(Supplier::get).collect(Collectors.toSet()));
        }
    }

    private void addToList(List<Supplier<PlacedFeature>> list, int index, PlacedFeature feature) {
        list.add(index, () -> feature);
    }

    @Nullable
    private PlacedFeature check(de.derfrzocker.ore.control.api.Biome biome, ResourceLocation biomeKey, PlacedFeature feature) {
        // Not the best method to do this, but hey it works
        final Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registry.PLACED_FEATURE_REGISTRY);
        ResourceLocation featureKey = registry.getKey(feature);
        NamespacedKey key = CraftNamespacedKey.fromMinecraft(featureKey);
        feature = BuiltinRegistries.PLACED_FEATURE.get(featureKey);

        if (feature == null) {
            return null;
        }

        ConfiguredFeature<?, ?> configuredFeature = feature.getFeatures().findFirst().orElse(null);

        if (configuredFeature == null) {
            return null;
        }

        if (!(configuredFeature.feature() instanceof OreFeature) && !(configuredFeature.feature() instanceof ScatteredOreFeature)) {
            return null;
        }

        boolean hasCount = false;
        List<PlacementModifier> placementModifiers = new ArrayList<>();
        for (PlacementModifier placement : feature.getPlacement()) {
            if (placement.type() == PlacementModifierType.RARITY_FILTER) {
                placementModifiers.add(new RarityModifierHook(registries, configManager, biome, key, (RarityFilter) placement));
            } else if (placement.type() == PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER) {
                placementModifiers.add(new SurfaceRelativeThresholdModifierHook(registries, configManager, biome, key, (SurfaceRelativeThresholdFilter) placement));
            } else if (placement.type() == PlacementModifierType.SURFACE_WATER_DEPTH_FILTER) {
                placementModifiers.add(new SurfaceWaterDepthModifierHook(registries, configManager, biome, key, (SurfaceWaterDepthFilter) placement));
            } else if (placement.type() == PlacementModifierType.COUNT) {
                hasCount = true;
                placementModifiers.add(new CountModifierHook(registries, configManager, biome, key, (CountPlacement) placement));
            } else if (placement.type() == PlacementModifierType.HEIGHT_RANGE) {
                placementModifiers.add(new HeightRangeModifierHook(registries, configManager, biome, key, (HeightRangePlacement) placement));
            } else {
                placementModifiers.add(placement);
            }
        }

        if (!hasCount) {
            placementModifiers.add(0, new CountModifierHook(registries, configManager, biome, key, CountPlacement.of(1)));
        }

        if (configuredFeature.feature() instanceof OreFeature) {
            return new PlacedFeature(() -> new ConfiguredFeature(new OreFeatureGeneratorHook(registries, configManager, key, biome), configuredFeature.config()), placementModifiers);
        } else if (configuredFeature.feature() instanceof ScatteredOreFeature) {
            return new PlacedFeature(() -> new ConfiguredFeature(new ScatteredOreFeatureGeneratorHook(registries, configManager, key, biome), configuredFeature.config()), placementModifiers);
        }

        throw new RuntimeException("HOW?");
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
