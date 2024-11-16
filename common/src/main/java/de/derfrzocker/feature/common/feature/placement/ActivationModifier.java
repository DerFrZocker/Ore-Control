package de.derfrzocker.feature.common.feature.placement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.api.PlacementModifierConfiguration;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.value.bool.BooleanType;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class ActivationModifier implements FeaturePlacementModifier<ActivationConfiguration> {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:activation");

    private final Parser<PlacementModifierConfiguration> parser;

    public ActivationModifier(Registries registries) {
        parser = new Parser<>() {
            @Override
            public JsonElement toJson(PlacementModifierConfiguration v) {
                ActivationConfiguration value = (ActivationConfiguration) v;
                JsonObject jsonObject = new JsonObject();

                if (value.getActivate() != null) {
                    JsonObject entry = value.getActivate().getValueType().getParser().toJson(value.getActivate()).getAsJsonObject();
                    entry.addProperty("activate_type", value.getActivate().getValueType().getKey().toString());
                    jsonObject.add("activate", entry);
                }

                return jsonObject;
            }

            @Override
            public ActivationConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                BooleanValue activate = null;
                if (jsonObject.has("activate")) {
                    JsonObject entry = jsonObject.getAsJsonObject("activate");
                    activate = registries.getValueTypeRegistry(BooleanType.class).get(NamespacedKey.fromString(entry.getAsJsonPrimitive("activate_type").getAsString())).get().getParser().fromJson(entry);
                }

                return new ActivationConfiguration(ActivationModifier.this, activate);
            }
        };
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return ActivationConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public Configuration createEmptyConfiguration() {
        return new ActivationConfiguration(this, null);
    }

    @NotNull
    @Override
    public Parser<PlacementModifierConfiguration> getParser() {
        return parser;
    }

    @NotNull
    @Override
    public ActivationConfiguration merge(@NotNull PlacementModifierConfiguration first, @NotNull PlacementModifierConfiguration second) {
        return new ActivationConfiguration(this,
                ((ActivationConfiguration) first).getActivate() != null ? ((ActivationConfiguration) first).getActivate() : ((ActivationConfiguration) second).getActivate());
    }

    @NotNull
    @Override
    public Stream<BlockVector> getPositions(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull ActivationConfiguration configuration) {
        if (configuration.getActivate() == null || configuration.getActivate().getValue(worldInfo, random, position, limitedRegion)) {
            return Stream.of(position);
        }

        return Stream.empty();
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return KEY;
    }
}
