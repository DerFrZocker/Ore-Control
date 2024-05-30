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

package de.derfrzocker.ore.control.impl.v1_20_R3;

import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.api.FeatureGeneratorConfiguration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.generator.configuration.EmptyFeatureConfiguration;
import de.derfrzocker.feature.common.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.common.feature.placement.ActivationModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.util.ValueLocationUtil;
import de.derfrzocker.feature.common.value.bool.FixedBooleanValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.offset.AboveBottomOffsetIntegerType;
import de.derfrzocker.feature.common.value.offset.BelowTopOffsetIntegerType;
import de.derfrzocker.feature.impl.v1_20_R3.extra.OreVeinHandler;
import de.derfrzocker.feature.impl.v1_20_R3.feature.generator.EmptyConfigurationGenerator;
import de.derfrzocker.feature.impl.v1_20_R3.feature.generator.OreConfigurationGenerator;
import de.derfrzocker.feature.impl.v1_20_R3.placement.CountModifier;
import de.derfrzocker.feature.impl.v1_20_R3.placement.HeightRangeModifier;
import de.derfrzocker.feature.impl.v1_20_R3.placement.RarityModifier;
import de.derfrzocker.feature.impl.v1_20_R3.placement.SurfaceRelativeThresholdModifier;
import de.derfrzocker.feature.impl.v1_20_R3.placement.SurfaceWaterDepthModifier;
import de.derfrzocker.feature.impl.v1_20_R3.value.heightmap.FixedHeightmapType;
import de.derfrzocker.feature.impl.v1_20_R3.value.heightmap.HeightmapType;
import de.derfrzocker.feature.impl.v1_20_R3.value.offset.NMSAboveBottomOffsetIntegerValue;
import de.derfrzocker.feature.impl.v1_20_R3.value.offset.NMSBelowTopOffsetIntegerValue;
import de.derfrzocker.ore.control.api.NMSReplacer;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.OreControlRegistries;
import de.derfrzocker.ore.control.api.config.Config;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.impl.v1_20_R3.feature.generator.EmptyConfigurationGeneratorHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.feature.generator.OreConfigurationGeneratorHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.ActivationModifierHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.CountModifierHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.HeightRangeModifierHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.RarityModifierHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.SurfaceRelativeThresholdModifierHook;
import de.derfrzocker.ore.control.impl.v1_20_R3.placement.SurfaceWaterDepthModifierHook;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.ScatteredOreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import org.apache.maven.model.Parent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NMSReplacer_v1_20_R3 implements NMSReplacer {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Plugin plugin;
    private final Parser<Config> configParser;
    private final OreControlManager oreControlManager;
    @NotNull
    private final OreControlRegistries registries;
    private final ConfigManager configManager;

    public NMSReplacer_v1_20_R3(@NotNull Plugin plugin, @NotNull OreControlManager oreControlManager, Parser<Config> configParser) {
        this.plugin = plugin;
        this.oreControlManager = oreControlManager;
        this.registries = oreControlManager.getRegistries();
        this.configManager = oreControlManager.getConfigManager();
        this.configParser = configParser;
    }

    @Override
    public void register() {
        registerValueTypes();
        registerFeatureGenerators();
        registerPlacementModifier();
        registerFeatures();
        registerBiomes();
        registerExtraValues();
    }

    private void registerValueTypes() {
        registries.getValueTypeRegistry(IntegerType.class).register(new AboveBottomOffsetIntegerType(registries, NMSAboveBottomOffsetIntegerValue::new));
        registries.getValueTypeRegistry(IntegerType.class).register(new BelowTopOffsetIntegerType(registries, NMSBelowTopOffsetIntegerValue::new));
        registries.getValueTypeRegistry(HeightmapType.class).register(FixedHeightmapType.INSTANCE);
    }

    private void registerFeatureGenerators() {
        registries.getFeatureGeneratorRegistry().register(OreConfigurationGenerator.createOre(registries));
        registries.getFeatureGeneratorRegistry().register(OreConfigurationGenerator.createScatteredOre(registries));
        registries.getFeatureGeneratorRegistry().register(EmptyConfigurationGenerator.createGlowstoneBlob(registries));
    }

    private void registerPlacementModifier() {
        registries.getPlacementModifierRegistry().register(new RarityModifier(registries));
        registries.getPlacementModifierRegistry().register(new SurfaceRelativeThresholdModifier(registries));
        registries.getPlacementModifierRegistry().register(new SurfaceWaterDepthModifier(registries));
        registries.getPlacementModifierRegistry().register(new CountModifier(registries));
        registries.getPlacementModifierRegistry().register(new HeightRangeModifier(registries));
    }

    private void registerFeatures() {
        Registry<PlacedFeature> placedFeatureRegistry = getRegistry().registryOrThrow(Registries.PLACED_FEATURE);
        Registry<Feature<?>> featureRegistry = getRegistry().registryOrThrow(Registries.FEATURE);
        Registry<PlacementModifierType<?>> placementModifierTypes = getRegistry().registryOrThrow(Registries.PLACEMENT_MODIFIER_TYPE);

        for (PlacedFeature placedFeature : placedFeatureRegistry) {
            Feature<?> feature = placedFeature.getFeatures().findFirst().get().feature();
            ResourceLocation resourceLocation = featureRegistry.getKey(feature);
            Optional<FeatureGenerator<?>> featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.fromString(resourceLocation.toString()));

            if (featureGenerator.isEmpty()) {
                continue;
            }

            boolean hasCount = false;
            List<FeaturePlacementModifier<?>> modifiers = new LinkedList<>();
            for (PlacementModifier modifier : placedFeature.placement()) {
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

            // inject activation / deactivation logic
            Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(ActivationModifier.KEY);
            modifierOptional.ifPresent(modifiers::add);

            registries.getFeatureRegistry().register(new de.derfrzocker.feature.api.Feature(NamespacedKey.fromString(placedFeatureRegistry.getKey(placedFeature).toString()), featureGenerator.get(), modifiers));
        }
    }

    private void registerBiomes() {
        Registry<PlacedFeature> placedFeatureRegistry = getRegistry().registryOrThrow(Registries.PLACED_FEATURE);
        forEachBiome((biome, resourceLocation) -> {
            de.derfrzocker.ore.control.api.Biome bio = new de.derfrzocker.ore.control.api.Biome(CraftNamespacedKey.fromMinecraft(resourceLocation));

            biome.getGenerationSettings().features().forEach(featureSet -> featureSet.forEach(holder -> {
                PlacedFeature feature = holder.value();
                ResourceLocation featureKey = placedFeatureRegistry.getKey(feature);

                if (featureKey == null) {
                    // Skip if the feature is not registered, this can happen if another plugin adds a feature without registering it
                    return;
                }

                registries.getFeatureRegistry().get(CraftNamespacedKey.fromMinecraft(featureKey)).ifPresent(value -> bio.getFeatures().add(value));
            }));

            registries.getBiomeRegistry().register(bio);
        });
    }

    private void registerExtraValues() {
        plugin.getServer().getPluginManager().registerEvents(new OreVeinHandler(oreControlManager), plugin);
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
        Registry<Biome> registry = getRegistry().registryOrThrow(Registries.BIOME);

        for (Holder<Biome> biomeHolder : level.getChunkSource().chunkMap.generator.getBiomeSource().possibleBiomes()) {
            Biome biome = biomeHolder.value();

            ResourceLocation key = registry.getKey(biome);

            biomes.add(registries.getBiomeRegistry().get(CraftNamespacedKey.fromMinecraft(key)).get());
        }

        return biomes;
    }

    private void saveWorldValues(File directory) {
        Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registries.PLACED_FEATURE);
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
            List<HolderSet<PlacedFeature>> decorations = biome.getGenerationSettings().features();
            for (HolderSet<PlacedFeature> list : decorations) {
                for (Holder<PlacedFeature> featureSupplier : list) {
                    PlacedFeature feature = featureSupplier.value();
                    Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registries.PLACED_FEATURE);
                    ResourceLocation featureKey = registry.getKey(feature);

                    if (featureKey == null) {
                        // Skip if the feature is not registered, this can happen if another plugin adds a feature without registering it
                        return;
                    }

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

    // TODO clean up
    private void saveFeature(File file, de.derfrzocker.ore.control.api.Biome biome, NamespacedKey featureKey, PlacedFeature feature) {
        ConfiguredFeature<?, ?> configuredFeature = feature.getFeatures().findFirst().orElse(null);

        if (configuredFeature == null) {
            return;
        }

        if (!(configuredFeature.feature() instanceof OreFeature) && !(configuredFeature.feature() instanceof ScatteredOreFeature) && !(configuredFeature.feature() instanceof GlowstoneFeature)) {
            return;
        }

        file.getParentFile().mkdirs();

        FeatureGenerator<?> featureGenerator;
        FeatureGeneratorConfiguration featureConfiguration;

        if (configuredFeature.feature() instanceof OreFeature) {
            OreConfiguration configuration = (OreConfiguration) configuredFeature.config();
            featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.minecraft("ore")).get();
            featureConfiguration = OreConfigurationGeneratorHook.createDefaultConfiguration(configuration, (FeatureGenerator<OreFeatureConfiguration>) featureGenerator);
        } else if (configuredFeature.feature() instanceof ScatteredOreFeature) {
            OreConfiguration configuration = (OreConfiguration) configuredFeature.config();
            featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.minecraft("scattered_ore")).get();
            featureConfiguration = OreConfigurationGeneratorHook.createDefaultConfiguration(configuration, (FeatureGenerator<OreFeatureConfiguration>) featureGenerator);
        } else if (configuredFeature.feature() instanceof GlowstoneFeature) {
            featureGenerator = registries.getFeatureGeneratorRegistry().get(NamespacedKey.minecraft("glowstone_blob")).get();
            featureConfiguration = new EmptyFeatureConfiguration(featureGenerator);
        } else {
            throw new RuntimeException("HOW?");
        }

        boolean hasCount = false;
        List<PlacementModifierConfiguration> placementConfiguration = new ArrayList<>();
        Registry<PlacementModifierType<?>> placementModifierTypes = getRegistry().registryOrThrow(Registries.PLACEMENT_MODIFIER_TYPE);
        for (PlacementModifier placement : feature.placement()) {
            ResourceLocation placementModifierType = placementModifierTypes.getKey(placement.type());
            Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(NamespacedKey.fromString(placementModifierType.toString()));

            if (modifierOptional.isEmpty()) {
                continue;
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

            modifierOptional.ifPresent(modifier -> placementConfiguration.add(0, CountModifierHook.createDefaultConfiguration(CountPlacement.of(1), modifier)));
        }

        // inject activation / deactivation logic
        Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(ActivationModifier.KEY);
        modifierOptional.ifPresent(modifier -> placementConfiguration.add(0, new ActivationConfiguration(modifier, new FixedBooleanValue(true))));

        Config config = new Config(placementConfiguration, featureConfiguration);

        if (biome == null) {
            ValueLocationUtil.setValueLocation(Optional.of(config), ValueLocation.DEFAULT_WORLD);
            configManager.setDefaultConfig(featureKey, config);
        } else {
            ValueLocationUtil.setValueLocation(Optional.of(config), ValueLocation.DEFAULT_BIOME);
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

        JsonElement element = configParser.toJson(config);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(element));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forEachBiome(BiConsumer<Biome, ResourceLocation> consumer) {
        Registry<Biome> registry = getRegistry().registryOrThrow(Registries.BIOME);
        for (Biome biome : registry) {
            consumer.accept(biome, registry.getKey(biome));
        }
    }

    private void hookIntoBiome(@NotNull final Biome biome, ResourceLocation minecraftKey) throws NoSuchFieldException, IllegalAccessException {
        de.derfrzocker.ore.control.api.Biome oreControlBiome = registries.getBiomeRegistry().get(CraftNamespacedKey.fromMinecraft(minecraftKey)).get();
        List<HolderSet<PlacedFeature>> decorations = biome.getGenerationSettings().features();
        List<HolderSet<PlacedFeature>> newDecorations = new ArrayList<>();
        for (HolderSet<PlacedFeature> list : decorations) {
            List<Holder<PlacedFeature>> newList = new ArrayList<>();

            for (Holder<PlacedFeature> featureSupplier : list) {
                PlacedFeature feature = featureSupplier.value();

                PlacedFeature newFeature = check(oreControlBiome, minecraftKey, feature);

                if (newFeature != null) {
                    newList.add(Holder.direct(newFeature));
                } else {
                    newList.add(featureSupplier);
                }
            }

            newDecorations.add(decorations.indexOf(list), HolderSet.direct(newList));
        }

        {
            final Field field = getField(BiomeGenerationSettings.class, NMSReflectionNames.BIOME_GENERATION_SETTINGS_FEATURES);
            field.setAccessible(true);
            field.set(biome.getGenerationSettings(), newDecorations);
        }
        {
            final Field field = getField(BiomeGenerationSettings.class, NMSReflectionNames.BIOME_GENERATION_SETTINGS_FEATURE_SET);
            field.setAccessible(true);
            field.set(biome.getGenerationSettings(), Suppliers.memoize(() -> newDecorations.stream().flatMap(HolderSet::stream).map(Holder::value).collect(Collectors.toSet())));
        }
    }

    private void addToList(List<Supplier<PlacedFeature>> list, int index, PlacedFeature feature) {
        list.add(index, () -> feature);
    }

    @Nullable
    private PlacedFeature check(de.derfrzocker.ore.control.api.Biome biome, ResourceLocation biomeKey, PlacedFeature feature) {
        // Not the best method to do this, but hey it works
        final Registry<PlacedFeature> registry = getRegistry().registryOrThrow(Registries.PLACED_FEATURE);
        ResourceLocation featureKey = registry.getKey(feature);

        // Return if the featureKey is null, this can happen when plugins are adding placed features to the biome without registering them
        if (featureKey == null) {
            // TODO add debug message
            return null;
        }

        NamespacedKey key = CraftNamespacedKey.fromMinecraft(featureKey);
        ConfiguredFeature<?, ?> configuredFeature = feature.getFeatures().findFirst().orElse(null);

        if (configuredFeature == null) {
            return null;
        }

        if (!(configuredFeature.feature() instanceof OreFeature) && !(configuredFeature.feature() instanceof ScatteredOreFeature) && !(configuredFeature.feature() instanceof GlowstoneFeature)) {
            return null;
        }

        boolean hasCount = false;
        List<PlacementModifier> placementModifiers = new ArrayList<>();
        for (PlacementModifier placement : feature.placement()) {
            if (placement.type() == PlacementModifierType.RARITY_FILTER) {
                placementModifiers.add(new RarityModifierHook(oreControlManager, biome, key, (RarityFilter) placement));
            } else if (placement.type() == PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER) {
                placementModifiers.add(new SurfaceRelativeThresholdModifierHook(oreControlManager, biome, key, (SurfaceRelativeThresholdFilter) placement));
            } else if (placement.type() == PlacementModifierType.SURFACE_WATER_DEPTH_FILTER) {
                placementModifiers.add(new SurfaceWaterDepthModifierHook(oreControlManager, biome, key, (SurfaceWaterDepthFilter) placement));
            } else if (placement.type() == PlacementModifierType.COUNT) {
                hasCount = true;
                placementModifiers.add(new CountModifierHook(oreControlManager, biome, key, (CountPlacement) placement));
            } else if (placement.type() == PlacementModifierType.HEIGHT_RANGE) {
                placementModifiers.add(new HeightRangeModifierHook(oreControlManager, biome, key, (HeightRangePlacement) placement));
            } else {
                placementModifiers.add(placement);
            }
        }

        if (!hasCount) {
            placementModifiers.add(0, new CountModifierHook(oreControlManager, biome, key, CountPlacement.of(1)));
        }

        // inject activation / deactivation logic
        Optional<FeaturePlacementModifier<?>> modifierOptional = registries.getPlacementModifierRegistry().get(ActivationModifier.KEY);
        modifierOptional.ifPresent(modifier -> placementModifiers.add(0, new ActivationModifierHook(oreControlManager, biome, key)));

        if (configuredFeature.feature() instanceof OreFeature) {
            return new PlacedFeature(Holder.direct(new ConfiguredFeature(OreConfigurationGeneratorHook.createOreHook(oreControlManager, key, biome), configuredFeature.config())), placementModifiers);
        } else if (configuredFeature.feature() instanceof ScatteredOreFeature) {
            return new PlacedFeature(Holder.direct(new ConfiguredFeature(OreConfigurationGeneratorHook.createScatteredOreHook(oreControlManager, key, biome), configuredFeature.config())), placementModifiers);
        } else if (configuredFeature.feature() instanceof GlowstoneFeature) {
            return new PlacedFeature(Holder.direct(new ConfiguredFeature(EmptyConfigurationGeneratorHook.createGlowstoneBlobHook(oreControlManager, key, biome), configuredFeature.config())), placementModifiers);
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
