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

package de.derfrzocker.feature.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class Registries {

    private final Registry<Feature> featureRegistry = new Registry<>();
    private final Registry<FeatureGenerator<?>> featureGeneratorRegistry = new Registry<>();
    private final Registry<FeaturePlacementModifier<?>> placementModifierRegistry = new Registry<>();
    private final Map<Class<?>, Registry<?>> valueTypeRegistry = new LinkedHashMap<>();

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
}
