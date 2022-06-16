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

package de.derfrzocker.ore.control.impl.v1_18_R2;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.biased.BiasedToBottomIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerValue;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.util.valueproviders.WeightedListInt;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConversionUtil {

    private ConversionUtil() {
    }

    public static IntegerValue convert(IntProvider intProvider) {
        try {
            if (intProvider.getType() == IntProviderType.CONSTANT) {
                return new FixedDoubleToIntegerValue(intProvider.getMinValue());
            } else if (intProvider.getType() == IntProviderType.UNIFORM) {
                return new UniformIntegerValue(new FixedDoubleToIntegerValue(intProvider.getMinValue()), new FixedDoubleToIntegerValue(intProvider.getMaxValue()));
            } else if (intProvider.getType() == IntProviderType.BIASED_TO_BOTTOM) {
                return new BiasedToBottomIntegerValue(new FixedDoubleToIntegerValue(intProvider.getMinValue()), new FixedDoubleToIntegerValue(intProvider.getMaxValue()));
            } else if (intProvider.getType() == IntProviderType.WEIGHTED_LIST) {
                Field distributionField = WeightedListInt.class.getDeclaredField(NMSReflectionNames.WEIGHTED_LIST_INT_DISTRIBUTION);
                distributionField.setAccessible(true);
                SimpleWeightedRandomList<IntProvider> distribution = (SimpleWeightedRandomList<IntProvider>) distributionField.get(intProvider);

                Map<IntegerValue, IntegerValue> values = new LinkedHashMap<>();
                for (WeightedEntry.Wrapper<IntProvider> wrapper : distribution.unwrap()) {
                    values.put(convert(wrapper.getData()), new FixedDoubleToIntegerValue(wrapper.getWeight().asInt()));
                }

                return new WeightedListIntegerValue(values);
            } else { // TODO add rest of IntProvider types
                throw new UnsupportedOperationException(String.format("No integer value equivalent for IntProvider '%s'", intProvider));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
