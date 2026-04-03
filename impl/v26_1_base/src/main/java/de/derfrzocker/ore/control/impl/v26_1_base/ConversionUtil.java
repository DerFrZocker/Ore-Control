package de.derfrzocker.ore.control.impl.v26_1_base;

import de.derfrzocker.feature.common.value.number.FixedFloatValue;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.biased.BiasedToBottomIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.clamped.ClampedNormalIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.uniform.UniformIntegerValue;
import de.derfrzocker.feature.common.value.number.integer.weighted.WeightedListIntegerValue;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ClampedInt;
import net.minecraft.util.valueproviders.ClampedNormalInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;

public final class ConversionUtil {

    private ConversionUtil() {
    }

    public static IntegerValue convert(IntProvider intProvider) {
        try {
            if (intProvider instanceof ConstantInt) {
                return new FixedDoubleToIntegerValue(intProvider.minInclusive());
            } else if (intProvider instanceof UniformInt) {
                return new UniformIntegerValue(
                        new FixedDoubleToIntegerValue(intProvider.minInclusive()),
                        new FixedDoubleToIntegerValue(intProvider.maxInclusive()));
            } else if (intProvider instanceof BiasedToBottomInt) {
                return new BiasedToBottomIntegerValue(
                        new FixedDoubleToIntegerValue(intProvider.minInclusive()),
                        new FixedDoubleToIntegerValue(intProvider.maxInclusive()));
            } else if (intProvider instanceof ClampedInt clampedInt) {
                Field minField = ClampedInt.class.getDeclaredField(NMSReflectionNames.CLAMPED_INT_MIN_INCLUSIVE);
                Field maxField = ClampedInt.class.getDeclaredField(NMSReflectionNames.CLAMPED_INT_MAX_INCLUSIVE);
                minField.setAccessible(true);
                maxField.setAccessible(true);

                IntProvider source = clampedInt.source();
                int min = (int) minField.get(intProvider);
                int max = (int) maxField.get(intProvider);
                return new ClampedIntegerValue(
                        convert(source),
                        new FixedDoubleToIntegerValue(min),
                        new FixedDoubleToIntegerValue(max));
            } else if (intProvider instanceof WeightedListInt) {
                Field
                        distributionField
                        = WeightedListInt.class.getDeclaredField(NMSReflectionNames.WEIGHTED_LIST_INT_DISTRIBUTION);
                distributionField.setAccessible(true);
                WeightedList<IntProvider> distribution = (WeightedList<IntProvider>) distributionField.get(intProvider);

                Map<IntegerValue, IntegerValue> values = new LinkedHashMap<>();
                for (Weighted<IntProvider> wrapper : distribution.unwrap()) {
                    values.put(convert(wrapper.value()), new FixedDoubleToIntegerValue(wrapper.weight()));
                }

                return new WeightedListIntegerValue(values);
            } else if (intProvider instanceof ClampedNormalInt clampedNormalInt) {
                float mean = clampedNormalInt.mean();
                float deviation = clampedNormalInt.deviation();
                int min = intProvider.minInclusive();
                int max = intProvider.maxInclusive();
                return new ClampedNormalIntegerValue(
                        new FixedFloatValue(mean),
                        new FixedFloatValue(deviation),
                        new FixedDoubleToIntegerValue(min),
                        new FixedDoubleToIntegerValue(max));
            } else {
                throw new UnsupportedOperationException(String.format(
                        "No integer value equivalent for IntProvider '%s'",
                        intProvider));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
