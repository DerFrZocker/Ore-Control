package de.derfrzocker.ore.control.impl.v1_21_R2;

import static de.derfrzocker.feature.common.util.Refraction.pickName;

public final class NMSReflectionNames {

    public final static String BIOME_GENERATION_SETTINGS_FEATURES = pickName("features", "e");
    public final static String BIOME_GENERATION_SETTINGS_FEATURE_SET = pickName("featureSet", "g");
    public final static String WEIGHTED_LIST_INT_DISTRIBUTION = pickName("distribution", "b");
    public final static String COUNT_PLACEMENT_COUNT = pickName("count", "c");
    public final static String HEIGHT_RANGE_PLACEMENT_HEIGHT = pickName("height", "c");
    public final static String RARITY_FILTER_CHANCE = pickName("chance", "c");
    public final static String SURFACE_RELATIVE_THRESHOLD_FILTER_HEIGHTMAP = pickName("heightmap", "c");
    public final static String SURFACE_RELATIVE_THRESHOLD_FILTER_MIN_INCLUSIVE = pickName("minInclusive", "d");
    public final static String SURFACE_RELATIVE_THRESHOLD_FILTER_MAX_INCLUSIVE = pickName("maxInclusive", "e");
    public final static String SURFACE_WATER_DEPTH_FILTER_MAX_WATER_DEPTH = pickName("maxWaterDepth", "c");
    public static final String CLAMPED_INT_SOURCE = pickName("source","b");
    public static final String CLAMPED_INT_MIN_INCLUSIVE = pickName("minInclusive", "f");
    public static final String CLAMPED_INT_MAX_INCLUSIVE = pickName("maxInclusive", "g");
    public static final String CLAMPED_NORMAL_INT_MEAN = pickName("mean", "b");
    public static final String CLAMPED_NORMAL_INT_DEVIATION = pickName("deviation", "f");
    public static final String CHUNK_MAP_WORLD_GEN_CONTEXT = pickName("worldGenContext", "R");
    public static final String NOISE_GENERATOR_SETTINGS = pickName("settings", "e");

    private NMSReflectionNames() {
    }
}
