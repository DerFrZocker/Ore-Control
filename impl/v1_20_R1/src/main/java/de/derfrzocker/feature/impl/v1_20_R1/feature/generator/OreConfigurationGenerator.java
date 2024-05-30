package de.derfrzocker.feature.impl.v1_20_R1.feature.generator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.derfrzocker.feature.api.Registries;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.util.Parser;
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
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
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

    @Override
    public Parser<OreFeatureConfiguration> createParser(Registries registries) {
        return new Parser<>() {
            @Override
            public JsonElement toJson(OreFeatureConfiguration value) {
                JsonObject jsonObject = new JsonObject();
                if (value.getTargets() != null) {
                    JsonObject entry = value.getTargets().getValueType().getParser().toJson(value.getTargets()).getAsJsonObject();
                    entry.addProperty("target_type", value.getTargets().getValueType().getKey().toString());

                    jsonObject.add("targets", entry);
                }

                if (value.getSize() != null) {
                    JsonObject size = value.getSize().getValueType().getParser().toJson(value.getSize()).getAsJsonObject();
                    size.addProperty("size_type", value.getSize().getValueType().getKey().toString());
                    jsonObject.add("size", size);
                }

                if (value.getDiscardChanceOnAirExposure() != null) {
                    JsonObject discardChanceOnAirExposure = value.getDiscardChanceOnAirExposure().getValueType().getParser().toJson(value.getDiscardChanceOnAirExposure()).getAsJsonObject();
                    discardChanceOnAirExposure.addProperty("discard_chance_on_air_exposure_type", value.getDiscardChanceOnAirExposure().getValueType().getKey().toString());
                }

                return jsonObject;
            }

            @Override
            public OreFeatureConfiguration fromJson(JsonElement jsonElement) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                TargetListValue targetValues = null;
                if (jsonObject.has("targets")) {
                    JsonObject targets = jsonObject.getAsJsonObject("targets");
                    targetValues = registries.getValueTypeRegistry(TargetListType.class).get(NamespacedKey.fromString(targets.getAsJsonPrimitive("target_type").getAsString())).get().getParser().fromJson(targets);
                }

                IntegerValue size = null;
                if (jsonObject.has("size")) {
                    JsonObject sizes = jsonObject.getAsJsonObject("size");
                    size = registries.getValueTypeRegistry(IntegerType.class).get(NamespacedKey.fromString(sizes.getAsJsonPrimitive("size_type").getAsString())).get().getParser().fromJson(sizes);
                }

                FloatValue discardChanceOnAirExposure = null;
                if (jsonObject.has("discard_chance_on_air_exposure")) {
                    JsonObject chance = jsonObject.getAsJsonObject("discard_chance_on_air_exposure");
                    discardChanceOnAirExposure = registries.getValueTypeRegistry(FloatType.class).get(NamespacedKey.fromString(chance.getAsJsonPrimitive("discard_chance_on_air_exposure_type").getAsString())).get().getParser().fromJson(chance);
                }

                return new OreFeatureConfiguration(OreConfigurationGenerator.this, targetValues, size, discardChanceOnAirExposure);
            }
        };
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
