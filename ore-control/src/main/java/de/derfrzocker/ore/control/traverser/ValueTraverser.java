package de.derfrzocker.ore.control.traverser;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.LocatedAble;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.api.util.traverser.message.MessageTraversAble;
import de.derfrzocker.feature.api.util.traverser.message.StringFormatter;
import de.derfrzocker.feature.api.util.traverser.message.TraversKey;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ValueTraverser {

    private final Map<ValueLocation, StringFormatter> formatters = new EnumMap<>(ValueLocation.class);

    public void registerFormatter(ValueLocation valueLocation, StringFormatter formatter) {
        this.formatters.put(valueLocation, formatter);
    }

    public String traverse(Configuration configuration) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Setting setting : configuration.getSettings()) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append(traverse(configuration.getValue(setting), TraversKey.ofSetting(setting.name())));
        }

        return stringBuilder.toString();
    }

    public <T extends MessageTraversAble & LocatedAble> String traverse(T value, TraversKey keyTo) {
        List<String> list = value.traverse(formatters.get(value.getValueLocation()), 1, keyTo);
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String string : list) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }
}
