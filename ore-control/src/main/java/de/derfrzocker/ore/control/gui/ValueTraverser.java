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

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.Configuration;
import de.derfrzocker.feature.api.Setting;
import de.derfrzocker.feature.api.Value;
import de.derfrzocker.feature.api.util.MessageTraversAble;

import java.util.List;

// TODO clean up / make more flexible
public class ValueTraverser {

    private final MessageTraversAble.StringFormatter formatter = (depth, key, value) -> {
        StringBuilder builder = new StringBuilder();

        builder.append(" ".repeat(Math.max(0, depth)));
        builder.append("§r§7>§r§f ");

        if (key.startsWith("%%")) {
            builder.append(key);
        } else {
            builder.append(getTranslationValueSettingKey(key));
        }
        builder.append(":");
        if (value != null) {
            builder.append(" ");
            builder.append(value);
        }

        return builder.toString();
    };

    public String traverse(Configuration configuration) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Setting setting : configuration.getSettings()) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append("%%new-line%");
            }
            stringBuilder.append(traverse(configuration.getValue(setting), getTranslationSettingKey(setting.name())));
        }

        return stringBuilder.toString();
    }

    public String traverse(Value<?, ?, ?> valueTo, String keyTo) {
        List<String> list = valueTo.traverse(formatter, 1, keyTo);
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

    private String getTranslationValueSettingKey(String value) {
        return "%%translation:[" + "value-settings." + value + ".name]%";
    }

    private String getTranslationSettingKey(String value) {
        return "%%translation:[" + "settings." + value + ".name]%";
    }
}
