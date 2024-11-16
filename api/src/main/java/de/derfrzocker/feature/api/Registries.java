package de.derfrzocker.feature.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class Registries {

    private final Registry<Feature> featureRegistry = new Registry<>();
    private final Registry<FeatureGenerator<?>> featureGeneratorRegistry = new Registry<>();
    private final Registry<FeaturePlacementModifier<?>> placementModifierRegistry = new Registry<>();
    private final Map<Class<?>, Registry<?>> valueTypeRegistry = new LinkedHashMap<>();
    private final Registry<RuleTestType> ruleTestTypeRegistry = new Registry<>();

    public Registry<Feature> getFeatureRegistry() {
        return featureRegistry;
    }

    public Registry<FeatureGenerator<?>> getFeatureGeneratorRegistry() {
        return featureGeneratorRegistry;
    }

    public Registry<FeaturePlacementModifier<?>> getPlacementModifierRegistry() {
        return placementModifierRegistry;
    }

    public <O extends ValueType<?, O, ?>> Registry<O> getValueTypeRegistry(Class<O> clazz) {
        return (Registry<O>) valueTypeRegistry.computeIfAbsent(clazz, aClass -> new Registry<>());
    }

    public Registry<RuleTestType> getRuleTestTypeRegistry() {
        return ruleTestTypeRegistry;
    }
}
