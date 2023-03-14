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

package de.derfrzocker.ore.control.impl.v1_19_R3.placement;

import de.derfrzocker.feature.api.FeaturePlacementModifier;
import de.derfrzocker.feature.common.feature.placement.configuration.HeightRangeModifierConfiguration;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.trapezoid.TrapezoidIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.feature.impl.v1_19_R3.value.offset.NMSAboveBottomOffsetIntegerValue;
import de.derfrzocker.feature.impl.v1_19_R3.value.offset.NMSBelowTopOffsetIntegerValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.impl.v1_19_R3.NMSReflectionNames;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Random;

public class HeightRangeModifierHook extends MinecraftPlacementModifierHook<HeightRangePlacement, HeightRangeModifierConfiguration> {

    public HeightRangeModifierHook(@NotNull OreControlManager oreControlManager, @NotNull Biome biome, @NotNull NamespacedKey namespacedKey, @NotNull HeightRangePlacement defaultModifier) {
        super(oreControlManager, "height_range", defaultModifier, biome, namespacedKey);
    }

    public static HeightRangeModifierConfiguration createDefaultConfiguration(@NotNull HeightRangePlacement defaultModifier, @NotNull FeaturePlacementModifier<?> modifier) {
        try {
            Field height = HeightRangePlacement.class.getDeclaredField(NMSReflectionNames.HEIGHT_RANGE_PLACEMENT_HEIGHT);
            height.setAccessible(true);
            HeightProvider value = (HeightProvider) height.get(defaultModifier);

            IntegerValue integerValue;
            if (value.getType() == HeightProviderType.CONSTANT) {
                integerValue = getIntegerValue(value.toString());
            } else if (value.getType() == HeightProviderType.UNIFORM) {
                String uniform = value.toString();
                uniform = uniform.substring(1);
                uniform = uniform.substring(0, uniform.length() - 1);
                int charType = uniform.indexOf("-", 1);
                String[] anchors = new String[]{uniform.substring(0, charType), uniform.substring(charType + 1)};

                integerValue = new UniformIntegerValue(getIntegerValue(anchors[0]), getIntegerValue(anchors[1]));
            } else if (value.getType() == HeightProviderType.TRAPEZOID) {
                String trapezoid = value.toString();
                if (trapezoid.startsWith("triangle")) {
                    trapezoid = trapezoid.replace("triangle (", "");
                    trapezoid = trapezoid.substring(0, trapezoid.length() - 1);
                    int charType = trapezoid.indexOf("-", 1);
                    String[] anchors = new String[]{trapezoid.substring(0, charType), trapezoid.substring(charType + 1)};

                    integerValue = new TrapezoidIntegerValue(getIntegerValue(anchors[0]), getIntegerValue(anchors[1]), new FixedDoubleToIntegerValue(0));
                } else if (trapezoid.startsWith("trapezoid")) {
                    trapezoid = trapezoid.replace("trapezoid (", "");
                    trapezoid = trapezoid.substring(0, trapezoid.length() - 1);
                    String[] split = trapezoid.split("\\) in \\[");

                    if (split.length != 2) {
                        throw new IllegalStateException(String.format("Expected a split of size '2', but got '%s' for input '%s'", split.length, trapezoid));
                    }

                    int plateau = Integer.parseInt(split[0]);
                    int charType = trapezoid.indexOf("-", 1);
                    String[] anchors = new String[]{trapezoid.substring(0, charType), trapezoid.substring(charType + 1)};
                    integerValue = new TrapezoidIntegerValue(getIntegerValue(anchors[0]), getIntegerValue(anchors[1]), new FixedDoubleToIntegerValue(plateau));
                } else {
                    throw new UnsupportedOperationException(String.format("Unknown trapezoid value '%s'", trapezoid));
                }

            } else { // TODO add rest of HeightProvider types
                throw new UnsupportedOperationException(String.format("No integer value equivalent for HeightProvider '%s'", value));
            }

            return new HeightRangeModifierConfiguration(modifier, integerValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static IntegerValue getIntegerValue(String anchor) {
        String[] values = anchor.split(" ");
        int value = Integer.parseInt(values[0]);

        if (values.length == 2 && values[1].equals("absolute")) {
            return new FixedDoubleToIntegerValue(value);
        }

        if (values.length == 3 && values[1].equals("above") && values[2].equals("bottom")) {
            return new NMSAboveBottomOffsetIntegerValue(new FixedDoubleToIntegerValue(value));
        }

        if (values.length == 3 && values[1].equals("below") && values[2].equals("top")) {
            return new NMSBelowTopOffsetIntegerValue(new FixedDoubleToIntegerValue(value));
        }

        throw new UnsupportedOperationException(String.format("Unknown vertical anchor '%s'", anchor));
    }

    @Override
    public HeightRangeModifierConfiguration createDefaultConfiguration(@NotNull HeightRangePlacement defaultModifier) {
        return createDefaultConfiguration(defaultModifier, getPlacementModifier());
    }

    @Override
    public HeightRangePlacement createModifier(@NotNull HeightRangeModifierConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull HeightRangeModifierConfiguration configuration) {
        int height;
        if (configuration.getHeight() == null) {
            height = defaultConfiguration.getHeight().getValue(worldInfo, random, position, limitedRegion);
        } else {
            height = configuration.getHeight().getValue(worldInfo, random, position, limitedRegion);
        }

        return HeightRangePlacement.of(ConstantHeight.of(VerticalAnchor.absolute(height)));
    }
}
