package de.derfrzocker.ore.control.impl.v1_18_R2;

import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.feature.common.value.number.integer.FixedDoubleToIntegerValue;
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
            } else if (intProvider.getType() == IntProviderType.WEIGHTED_LIST) {
                Field distributionField = WeightedListInt.class.getDeclaredField("b");
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
