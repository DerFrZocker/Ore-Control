package de.derfrzocker.feature.impl.v1_18_R1.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.SurfaceWaterDepthModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;

public class SurfaceWaterDepthModifier extends MinecraftPlacementModifier<SurfaceWaterDepthFilter, SurfaceWaterDepthModifierConfiguration> {

    public SurfaceWaterDepthModifier(@NotNull Registries registries) {
        super(registries, "surface_water_depth_filter");
    }

    @Override
    public SurfaceWaterDepthModifierConfiguration mergeConfig(SurfaceWaterDepthModifierConfiguration first, SurfaceWaterDepthModifierConfiguration second) {
        return new SurfaceWaterDepthModifierConfiguration(this,
                first.getMaxWaterDepth() != null ? first.getMaxWaterDepth() : second.getMaxWaterDepth());
    }

    @Override
    public Parser<SurfaceWaterDepthModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(SurfaceWaterDepthModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getMaxWaterDepth() != null) {
                    JsonObject entry = value.getMaxWaterDepth().getValueType().getParser().toJson(value.getMaxWaterDepth()).getAsJsonObject();
                    entry.addProperty("max_water_depth_type", value.getMaxWaterDepth().getValueType().getKey().toString());
                    jsonObject.add("max_water_depth", entry);
                }
                return jsonObject;
            }

            @Override
            public SurfaceWaterDepthModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("max_water_depth")) {
                    JsonObject entry = jsonObject.getAsJsonObject("max_water_depth");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("max_water_depth_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new SurfaceWaterDepthModifierConfiguration(SurfaceWaterDepthModifier.this, count);
            }
        };
    }

    @Override
    public SurfaceWaterDepthFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull SurfaceWaterDepthModifierConfiguration configuration) {
        int maxWaterDepth = 0;
        if (configuration.getMaxWaterDepth() != null) {
            maxWaterDepth = configuration.getMaxWaterDepth().getValue(worldInfo, random, position, limitedRegion);
        }
        return SurfaceWaterDepthFilter.forMaxDepth(maxWaterDepth);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return SurfaceWaterDepthModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public SurfaceWaterDepthModifierConfiguration createEmptyConfiguration() {
        return new SurfaceWaterDepthModifierConfiguration(this, null);
    }
}