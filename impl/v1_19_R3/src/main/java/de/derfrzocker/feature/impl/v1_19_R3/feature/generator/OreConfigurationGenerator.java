package de.derfrzocker.feature.impl.v1_19_R3.feature.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.common.feature.generator.configuration.OreFeatureConfiguration;
import de.derfrzocker.feature.common.ruletest.AlwaysTrueRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.BlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.RandomBlockStateMatchRuleTest;
import de.derfrzocker.feature.common.ruletest.TagMatchRuleTest;
import de.derfrzocker.feature.common.value.number.FloatType;
import de.derfrzocker.feature.common.value.number.FloatValue;
import de.derfrzocker.feature.common.value.number.IntegerType;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.target.TargetBlockState;
import de.derfrzocker.feature.common.value.target.TargetListType;
import de.derfrzocker.feature.common.value.target.TargetListValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class OreConfigurationGenerator extends MinecraftFeatureGenerator<OreConfiguration, OreFeatureConfiguration> {

    private OreConfigurationGenerator(Registries registries, Feature<OreConfiguration> feature, String name) {
        super(registries, feature, name);
    }

    public static OreConfigurationGenerator createOre(Registries registries) {
        return new OreConfigurationGenerator(registries, Feature.ORE, "ore");
    }

    public static OreConfigurationGenerator createScatteredOre(Registries registries) {
        return new OreConfigurationGenerator(registries, Feature.SCATTERED_ORE, "scattered_ore");
    }

    public Codec<OreFeatureConfiguration> createCodec(Registries registries) {
        return RecordCodecBuilder.create((builder) -> builder.group(
                registries.getValueTypeRegistry(TargetListType.class).
                        dispatch("target_type", TargetListValue::getValueType, TargetListType::getCodec).
                        optionalFieldOf("targets").
                        forGetter(config -> Optional.ofNullable(config.getTargets())),
                registries.getValueTypeRegistry(IntegerType.class).dispatch("size_type", IntegerValue::getValueType, IntegerType::getCodec).
                        optionalFieldOf("size").forGetter(config -> Optional.ofNullable(config.getSize())),
                registries.getValueTypeRegistry(FloatType.class).dispatch("discard_chance_on_air_exposure_type", FloatValue::getValueType, FloatType::getCodec).
                        optionalFieldOf("discard_chance_on_air_exposure").forGetter(config -> Optional.ofNullable(config.getDiscardChanceOnAirExposure()))
        ).apply(builder, (targets, size, discardChanceOnAirExposure) -> new OreFeatureConfiguration(this, targets.orElse(null), size.orElse(null), discardChanceOnAirExposure.orElse(null))));
    }

    @Override
    public OreFeatureConfiguration mergeConfig(OreFeatureConfiguration first, OreFeatureConfiguration second) {
        return new OreFeatureConfiguration(this,
                first.getTargets() != null ? first.getTargets() : second.getTargets(),
                first.getSize() != null ? first.getSize() : second.getSize(),
                first.getDiscardChanceOnAirExposure() != null ? first.getDiscardChanceOnAirExposure() : second.getDiscardChanceOnAirExposure());
    }

    @Override
    public OreConfiguration createConfiguration(@NotNull WorldInfo worldInfo, @NotNull Random random, @NotNull BlockVector position, @NotNull LimitedRegion limitedRegion, @NotNull OreFeatureConfiguration configuration) {
        List<OreConfiguration.TargetBlockState> blockStates = new ArrayList<>();
        if (configuration.getTargets() != null) {
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

        int size = 0;
        if (configuration.getSize() != null) {
            size = configuration.getSize().getValue(worldInfo, random, position, limitedRegion);
        }

        float discardChanceOnAirExposure = 0f;
        if (configuration.getDiscardChanceOnAirExposure() != null) {
            discardChanceOnAirExposure = configuration.getDiscardChanceOnAirExposure().getValue(worldInfo, random, position, limitedRegion);
        }

        return new OreConfiguration(blockStates, size, discardChanceOnAirExposure);
    }

    @NotNull
    @Override
    public Set<Setting> getSettings() {
        return OreFeatureConfiguration.SETTINGS;
    }

    @NotNull
    @Override
    public OreFeatureConfiguration createEmptyConfiguration() {
        return new OreFeatureConfiguration(this, null, null, null);
    }
}
