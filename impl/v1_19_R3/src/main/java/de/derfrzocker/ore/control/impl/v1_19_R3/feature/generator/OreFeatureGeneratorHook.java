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

package de.derfrzocker.ore.control.impl.v1_19_R3.feature.generator;

import de.derfrzocker.feature.api.FeatureGenerator;
import de.derfrzocker.feature.common.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.common.ruletest.AlwaysTrueRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.TagMatchRuleTest;
import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.target.FixedTargetListValue;
import de.derfrzocker.feature.common.value.target.TargetBlockState;
import de.derfrzocker.feature.common.value.target.TargetListValue;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OreFeatureGeneratorHook extends MinecraftFeatureGeneratorHook<OreConfiguration, OreFeatureConfiguration> {

    public OreFeatureGeneratorHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        super(OreConfiguration.CODEC, ORE, oreControlManager, "ore", biome, namespacedKey);
    }

    public static OreFeatureConfiguration createDefaultConfiguration(@NotNull OreConfiguration configuration, @NotNull FeatureGenerator<OreFeatureConfiguration> feature) {
        List<TargetBlockState> targetBlockStates = new ArrayList<>();
        for (OreConfiguration.TargetBlockState targetBlockState : configuration.targetStates) {
            de.derfrzocker.feature.api.RuleTest ruleTest;
            if (targetBlockState.target instanceof AlwaysTrueTest) {
                ruleTest = AlwaysTrueRuleTest.INSTANCE;
            } else if (targetBlockState.target instanceof BlockMatchTest rule) {
                ruleTest = new BlockMatchRuleTest(CraftMagicNumbers.getMaterial(getFieldValueFromType(rule, Block.class)));
            } else if (targetBlockState.target instanceof BlockStateMatchTest rule) {
                ruleTest = new BlockStateMatchRuleTest(CraftBlockData.fromData(getFieldValueFromType(rule, BlockState.class)));
            } else if (targetBlockState.target instanceof RandomBlockMatchTest rule) {
                ruleTest = new RandomBlockMatchRuleTest(CraftMagicNumbers.getMaterial(getFieldValueFromType(rule, Block.class)), getFieldValueFromType(rule, Float.TYPE));
            } else if (targetBlockState.target instanceof RandomBlockStateMatchTest rule) {
                ruleTest = new RandomBlockStateMatchRuleTest(CraftBlockData.fromData(getFieldValueFromType(rule, BlockState.class)), getFieldValueFromType(rule, Float.TYPE));
            } else if (targetBlockState.target instanceof TagMatchTest rule) {
                ruleTest = new TagMatchRuleTest(CraftNamespacedKey.fromMinecraft(getFieldValueFromType(rule, TagKey.class).location()));
            } else {
                throw new IllegalArgumentException("Got unexpected rule test from class " + targetBlockState.target.getClass());
            }

            System.out.println(ruleTest.getClass());
            targetBlockStates.add(new TargetBlockState(ruleTest, CraftBlockData.fromData(targetBlockState.state)));
        }

        TargetListValue targetListValue = new FixedTargetListValue(targetBlockStates);

        return new OreFeatureConfiguration(feature, targetListValue, new FixedDoubleToIntegerValue(configuration.size), new FixedFloatValue(configuration.discardChanceOnAirExposure));
    }

    private static <V> V getFieldValueFromType(Object from, Class<V> type) {
        for (Field field : from.getClass().getDeclaredFields()) {
            if (field.getType() == type) {
                field.setAccessible(true);
                try {
                    return (V) field.get(from);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        throw new IllegalStateException("Didn't found field with type " + type + " from class " + from.getClass()
                + ", following fields are present " + Arrays.toString(from.getClass().getDeclaredFields()));
    }

    @Override
    public OreConfiguration createConfig(@NotNull OreConfiguration defaultConfiguration, @NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull OreFeatureConfiguration configuration) {
        List<OreConfiguration.TargetBlockState> blockStates;
        if (configuration.getTargets() == null) {
            blockStates = defaultConfiguration.targetStates;
        } else {
            blockStates = new ArrayList<>();

            for (TargetBlockState targetValue : configuration.getTargets().getValue(worldInfo, random, position, limitedRegion)) {
                RuleTest ruleTest;

                if (targetValue.getRuleTest() instanceof AlwaysTrueRuleTest) {
                    ruleTest = AlwaysTrueTest.INSTANCE;
                } else if (targetValue.getRuleTest() instanceof BlockMatchRuleTest rule) {
                    ruleTest = new BlockMatchTest(CraftMagicNumbers.getBlock(rule.getMaterial()));
                } else if (targetValue.getRuleTest() instanceof BlockStateMatchRuleTest rule) {
                    ruleTest = new BlockStateMatchTest(((CraftBlockData) rule.getBlockData()).getState());
                } else if (targetValue.getRuleTest() instanceof RandomBlockMatchRuleTest rule) {
                    ruleTest = new RandomBlockMatchTest(CraftMagicNumbers.getBlock(rule.getMaterial()), rule.getProbability());
                } else if (targetValue.getRuleTest() instanceof RandomBlockStateMatchRuleTest rule) {
                    ruleTest = new RandomBlockStateMatchTest(((CraftBlockData) rule.getBlockData()).getState(), rule.getProbability());
                } else if (targetValue.getRuleTest() instanceof TagMatchRuleTest rule) {
                    ruleTest = new TagMatchTest(TagKey.create(net.minecraft.core.registries.Registries.BLOCK, CraftNamespacedKey.toMinecraft(rule.getTag())));
                } else {
                    throw new IllegalArgumentException("Got unexpected rule test from class " + targetValue.getRuleTest().getClass());
                }

                blockStates.add(OreConfiguration.target(ruleTest, ((CraftBlockData) targetValue.getBlockData()).getState()));
            }
        }

        int size;
        if (configuration.getSize() == null) {
            size = defaultConfiguration.size;
        } else {
            size = configuration.getSize().getValue(worldInfo, random, position, limitedRegion);
        }

        float discardChanceOnAirExposure;
        if (configuration.getDiscardChanceOnAirExposure() == null) {
            discardChanceOnAirExposure = defaultConfiguration.discardChanceOnAirExposure;
        } else {
            discardChanceOnAirExposure = configuration.getDiscardChanceOnAirExposure().getValue(worldInfo, random, position, limitedRegion);
        }

        return new OreConfiguration(blockStates, size, discardChanceOnAirExposure);
    }
}
