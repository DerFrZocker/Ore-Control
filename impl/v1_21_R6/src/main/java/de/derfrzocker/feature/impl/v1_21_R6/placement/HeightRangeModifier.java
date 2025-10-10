package de.derfrzocker.feature.impl.v1_21_R6.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.HeightRangeModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class HeightRangeModifier extends MinecraftPlacementModifier<HeightRangePlacement, HeightRangeModifierConfiguration> {

    public HeightRangeModifier(@NotNull Registries registries) {
        super(registries, "height_range");
    }

    @Override
    public HeightRangeModifierConfiguration mergeConfig(HeightRangeModifierConfiguration first, HeightRangeModifierConfiguration second) {
        return new HeightRangeModifierConfiguration(this,
                first.getHeight() != null ? first.getHeight() : second.getHeight());
    }

    @Override
    public Parser<HeightRangeModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(HeightRangeModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getHeight() != null) {
                    JsonObject entry = value.getHeight().getValueType().getParser().toJson(value.getHeight()).getAsJsonObject();
                    entry.addProperty("height_range_type", value.getHeight().getValueType().getKey().toString());
                    jsonObject.add("height", entry);
                }
                return jsonObject;
            }

            @Override
            public HeightRangeModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("height")) {
                    JsonObject entry = jsonObject.getAsJsonObject("height");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("height_range_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new HeightRangeModifierConfiguration(HeightRangeModifier.this, count);
            }
        };
    }

    @Override
    public HeightRangePlacement createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull HeightRangeModifierConfiguration configuration) {
        HeightProvider height;
        if (configuration.getHeight() != null) {
            height = ConstantHeight.of(VerticalAnchor.absolute(configuration.getHeight().getValue(worldInfo, random, position, limitedRegion)));
        } else {
            height = ConstantHeight.of(VerticalAnchor.bottom());
        }

        return HeightRangePlacement.of(height);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return HeightRangeModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public HeightRangeModifierConfiguration createEmptyConfiguration() {
        return new HeightRangeModifierConfiguration(this, null);
    }
}
