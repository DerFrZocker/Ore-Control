package de.derfrzocker.feature.impl.v1_21_R4.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.CountModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class CountModifier extends MinecraftPlacementModifier<CountPlacement, CountModifierConfiguration> {

    public CountModifier(@NotNull Registries registries) {
        super(registries, "count");
    }

    @Override
    public CountModifierConfiguration mergeConfig(CountModifierConfiguration first, CountModifierConfiguration second) {
        return new CountModifierConfiguration(this,
                first.getCount() != null ? first.getCount() : second.getCount());
    }

    @Override
    public Parser<CountModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(CountModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getCount() != null) {
                    JsonObject entry = value.getCount().getValueType().getParser().toJson(value.getCount()).getAsJsonObject();
                    entry.addProperty("count_type", value.getCount().getValueType().getKey().toString());
                    jsonObject.add("count", entry);
                }
                return jsonObject;
            }

            @Override
            public CountModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("count")) {
                    JsonObject entry = jsonObject.getAsJsonObject("count");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("count_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new CountModifierConfiguration(CountModifier.this, count);
            }
        };
    }

    @Override
    public CountPlacement createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull CountModifierConfiguration configuration) {
        int count;
        if (configuration.getCount() != null) {
            count = configuration.getCount().getValue(worldInfo, random, position, limitedRegion);
        } else {
            count = 0;
        }

        return CountPlacement.of(count);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return CountModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public CountModifierConfiguration createEmptyConfiguration() {
        return new CountModifierConfiguration(this, null);
    }
}
