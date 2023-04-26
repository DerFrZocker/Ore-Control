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

import de.derfrzocker.feature.common.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.common.ruletest.AlwaysTrueRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.TagMatchRuleTest;
import de.derfrzocker.feature.common.value.target.TargetBlockState;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlManager;
import net.minecraft.tags.TagKey;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScatteredOreFeatureGeneratorHook extends MinecraftFeatureGeneratorHook<OreConfiguration, OreFeatureConfiguration> {

    public ScatteredOreFeatureGeneratorHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        super(OreConfiguration.CODEC, SCATTERED_ORE, oreControlManager, "scattered_ore", biome, namespacedKey);
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
