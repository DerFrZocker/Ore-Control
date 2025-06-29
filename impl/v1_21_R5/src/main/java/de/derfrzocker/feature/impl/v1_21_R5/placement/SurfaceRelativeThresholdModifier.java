package de.derfrzocker.feature.impl.v1_21_R5.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.impl.v1_21_R5.placement.configuration.SurfaceRelativeThresholdModifierConfiguration;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class SurfaceRelativeThresholdModifier extends MinecraftPlacementModifier<SurfaceRelativeThresholdFilter, SurfaceRelativeThresholdModifierConfiguration> {
    public SurfaceRelativeThresholdModifier(@NotNull Registries registries) {
        super(registries, "surface_relative_threshold_filter");
    }

    @Override
    public SurfaceRelativeThresholdModifierConfiguration mergeConfig(SurfaceRelativeThresholdModifierConfiguration first, SurfaceRelativeThresholdModifierConfiguration second) {
        return new SurfaceRelativeThresholdModifierConfiguration(this,
                first.getHeightmap() != null ? first.getHeightmap() : second.getHeightmap(),
                first.getMinInclusive() != null ? first.getMinInclusive() : second.getMinInclusive(),
                first.getMaxInclusive() != null ? first.getMaxInclusive() : second.getMaxInclusive());
    }

    @Override
    public Parser<SurfaceRelativeThresholdModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(SurfaceRelativeThresholdModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getMinInclusive() != null) {
                    JsonObject entry = value.getMinInclusive().getValueType().getParser().toJson(value.getMinInclusive()).getAsJsonObject();
                    entry.addProperty("min_inclusive_type", value.getMinInclusive().getValueType().getKey().toString());
                    jsonObject.add("min_inclusive", entry);
                }

                if (value.getMaxInclusive() != null) {
                    JsonObject entry = value.getMaxInclusive().getValueType().getParser().toJson(value.getMaxInclusive()).getAsJsonObject();
                    entry.addProperty("max_inclusive_type", value.getMaxInclusive().getValueType().getKey().toString());
                    jsonObject.add("max_inclusive", entry);
                }

                return jsonObject;
            }

            @Override
            public SurfaceRelativeThresholdModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue minInclusive = null;
                if (jsonObject.has("min_inclusive")) {
                    JsonObject entry = jsonObject.getAsJsonObject("min_inclusive");
                    minInclusive = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("min_inclusive_type").getAsString())).get().getParser().fromJson(entry);
                }

                IntegerValue maxInclusive = null;
                if (jsonObject.has("max_inclusive")) {
                    JsonObject entry = jsonObject.getAsJsonObject("max_inclusive");
                    maxInclusive = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("max_inclusive_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new SurfaceRelativeThresholdModifierConfiguration(SurfaceRelativeThresholdModifier.this, null, minInclusive, maxInclusive);
            }
        };
    }

    @Override
    public SurfaceRelativeThresholdFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceRelativeThresholdModifierConfiguration configuration) {
        Heightmap.Types heightmap = Heightmap.Types.MOTION_BLOCKING;
        if (configuration.getHeightmap() != null) {
            heightmap = configuration.getHeightmap().getValue(worldInfo, random, position, limitedRegion);
        }

        int minInclusive = 0;
        if (configuration.getMinInclusive() != null) {
            minInclusive = configuration.getMinInclusive().getValue(worldInfo, random, position, limitedRegion);
        }

        int maxInclusive = 0;
        if (configuration.getMaxInclusive() != null) {
            maxInclusive = configuration.getMaxInclusive().getValue(worldInfo, random, position, limitedRegion);
        }
        return SurfaceRelativeThresholdFilter.of(heightmap, minInclusive, maxInclusive);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SurfaceRelativeThresholdModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public SurfaceRelativeThresholdModifierConfiguration createEmptyConfiguration() {
        return new SurfaceRelativeThresholdModifierConfiguration(this, null, null, null);
    }
}
