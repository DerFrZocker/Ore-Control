package de.derfrzocker.feature.impl.v1_18_R2.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.RarityModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;

public class RarityModifier extends MinecraftPlacementModifier<RarityFilter, RarityModifierConfiguration> {

    public RarityModifier(@NotNull Registries registries) {
        super(registries, "rarity_filter");
    }


    @Override
    public RarityModifierConfiguration mergeConfig(RarityModifierConfiguration first, RarityModifierConfiguration second) {
        return new RarityModifierConfiguration(this,
                first.getChance() != null ? first.getChance() : second.getChance());
    }

    @Override
    public Parser<RarityModifierConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(RarityModifierConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getChance() != null) {
                    JsonObject entry = value.getChance().getValueType().getParser().toJson(value.getChance()).getAsJsonObject();
                    entry.addProperty("chance_type", value.getChance().getValueType().getKey().toString());
                    jsonObject.add("chance", entry);
                }
                return jsonObject;
            }

            @Override
            public RarityModifierConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                IntegerValue count = null;
                if (jsonObject.has("chance")) {
                    JsonObject entry = jsonObject.getAsJsonObject("chance");
                    count = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("chance_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new RarityModifierConfiguration(RarityModifier.this, count);
            }
        };
    }

    @Override
    public RarityFilter createPlacementModifier(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull RarityModifierConfiguration configuration) {
        int chance = 0;
        if (configuration.getChance() != null) {
            chance = configuration.getChance().getValue(worldInfo, random, position, limitedRegion);
        }
        return RarityFilter.onAverageOnceEvery(chance);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return RarityModifierConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public RarityModifierConfiguration createEmptyConfiguration() {
        return new RarityModifierConfiguration(this, null);
    }
}
