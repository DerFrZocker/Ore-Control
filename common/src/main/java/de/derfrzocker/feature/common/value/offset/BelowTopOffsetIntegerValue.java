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

package de.derfrzocker.feature.common.value.offset;

import de.derfrzocker.feature.api.ValueLocation;
import de.derfrzocker.feature.common.util.MessageTraversUtil;
import de.derfrzocker.feature.common.value.number.IntegerValue;
import de.derfrzocker.spigot.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BelowTopOffsetIntegerValue extends IntegerValue {

    private IntegerValue base;
    private boolean dirty = false;

    public BelowTopOffsetIntegerValue(IntegerValue base) {
        this.base = base;
    }

    @Override
    public BelowTopOffsetIntegerType getValueType() {
        return BelowTopOffsetIntegerType.type();
    }

    @Override
    public boolean isDirty() {
        if (dirty) {
            return true;
        }

        return base != null && base.isDirty();
    }

    @Override
    public void saved() {
        dirty = false;

        if (base != null) {
            base.saved();
        }
    }

    public IntegerValue getBase() {
        return base;
    }

    public void setBase(IntegerValue integerValue) {
        integerValue.setValueLocation(getValueLocation());
        this.base = integerValue;
        dirty = true;
    }

    @Override
    public void setValueLocation(@NotNull ValueLocation valueLocation) {
        super.setValueLocation(valueLocation);
        getBase().setValueLocation(valueLocation);
    }

    @Override
    public List<String> traverse(StringFormatter formatter, int depth, String key) {
        return MessageTraversUtil.multiple(formatter, depth, key, new Pair<>("base", getBase()));
    }
}
