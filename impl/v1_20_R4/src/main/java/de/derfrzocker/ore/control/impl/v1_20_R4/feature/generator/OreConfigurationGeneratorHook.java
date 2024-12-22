package de.derfrzocker.ore.control.impl.v1_20_R4.feature.generator;

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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public class OreConfigurationGeneratorHook extends MinecraftFeatureGeneratorHook<OreConfiguration, OreFeatureConfiguration> {

    private OreConfigurationGeneratorHook(Feature<OreConfiguration> feature, @NotNull OreControlManager oreControlManager, @NotNull String name, Biome biome, NamespacedKey namespacedKey) {
        super(OreConfiguration.CODEC, feature, oreControlManager, name, biome, namespacedKey);
    }

    public static OreConfigurationGeneratorHook createOreHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        return new OreConfigurationGeneratorHook(ORE, oreControlManager, "ore", biome, namespacedKey);
    }

    public static OreConfigurationGeneratorHook createScatteredOreHook(@NotNull OreControlManager oreControlManager, @NotNull NamespacedKey namespacedKey, @NotNull Biome biome) {
        return new OreConfigurationGeneratorHook(SCATTERED_ORE, oreControlManager, "scattered_ore", biome, namespacedKey);
    }

    public static OreFeatureConfiguration createDefaultConfiguration(@NotNull OreConfiguration configuration, @NotNull FeatureGenerator<OreFeatureConfiguration> feature) {
        List<TargetBlockState> targetBlockStates = new ArrayList<>();
        for (OreConfiguration.TargetBlockState targetBlockState : configuration.targetStates) {
            de.derfrzocker.feature.api.RuleTest ruleTest;
            if (targetBlockState.target instanceof AlwaysTrueTest) {
                ruleTest = new AlwaysTrueRuleTest();
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

                if (targetValue.getTarget() instanceof AlwaysTrueRuleTest) {
                    ruleTest = AlwaysTrueTest.INSTANCE;
                } else if (targetValue.getTarget() instanceof BlockMatchRuleTest rule) {
                    ruleTest = new BlockMatchTest(CraftMagicNumbers.getBlock(rule.getBlock()));
                } else if (targetValue.getTarget() instanceof BlockStateMatchRuleTest rule) {
                    ruleTest = new BlockStateMatchTest(((CraftBlockData) rule.getBlockData()).getState());
                } else if (targetValue.getTarget() instanceof RandomBlockMatchRuleTest rule) {
                    ruleTest = new RandomBlockMatchTest(CraftMagicNumbers.getBlock(rule.getMaterial()), rule.getProbability());
                } else if (targetValue.getTarget() instanceof RandomBlockStateMatchRuleTest rule) {
                    ruleTest = new RandomBlockStateMatchTest(((CraftBlockData) rule.getBlockData()).getState(), rule.getProbability());
                } else if (targetValue.getTarget() instanceof TagMatchRuleTest rule) {
                    ruleTest = new TagMatchTest(TagKey.create(net.minecraft.core.registries.Registries.BLOCK, CraftNamespacedKey.toMinecraft(rule.getTag())));
                } else {
                    throw new IllegalArgumentException("Got unexpected rule test from class " + targetValue.getTarget().getClass());
                }

                blockStates.add(OreConfiguration.target(ruleTest, ((CraftBlockData) targetValue.getState()).getState()));
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
