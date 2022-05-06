package de.derfrzocker.feature.common.feature.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.*;
import de.derfrzocker.feature.common.feature.placement.configuration.ActivationConfiguration;
import de.derfrzocker.feature.common.value.bool.BooleanType;
import de.derfrzocker.feature.common.value.bool.BooleanValue;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

public class ActivationModifier implements FeaturePlacementModifier<ActivationConfiguration> {

    public static final NamespacedKey KEY = NamespacedKey.fromString("feature:activation");

    private final Codec<PlacementModifierConfiguration> codec;

    public ActivationModifier(Registries registries) {
        codec = RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(BooleanType.class).dispatch("activate_type", BooleanValue::getValueType, BooleanType::getCodec).
                        optionalFieldOf("activate").forGetter(config -> Optional.ofNullable(((ActivationConfiguration) config).getActivate()))
        ).apply(builder, (activate) -> new ActivationConfiguration(this, activate.orElse(null))));
    }

    @Override
    public Set<Setting> getSettings() {
        return ActivationConfiguration.SETTINGS;
    }

    @Override
    public Configuration createEmptyConfiguration() {
        return new ActivationConfiguration(this, null);
    }

    @Override
    public Codec<PlacementModifierConfiguration> getCodec() {
        return codec;
    }

    @Override
    public ActivationConfiguration merge(PlacementModifierConfiguration first, PlacementModifierConfiguration second) {
        return new ActivationConfiguration(this,
                ((ActivationConfiguration) first).getActivate() != null ? ((ActivationConfiguration) first).getActivate() : ((ActivationConfiguration) second).getActivate());
    }

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
