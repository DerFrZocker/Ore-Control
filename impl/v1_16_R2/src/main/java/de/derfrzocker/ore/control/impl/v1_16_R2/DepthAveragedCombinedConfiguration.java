/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.impl.v1_16_R2;

import net.minecraft.server.v1_16_R2.WorldGenDecoratorHeightAverageConfiguration;
import org.jetbrains.annotations.NotNull;

public class DepthAveragedCombinedConfiguration {

    private final int veinCount;
    @NotNull
    private final WorldGenDecoratorHeightAverageConfiguration configuration;

    public DepthAveragedCombinedConfiguration(final int veinCount, @NotNull final WorldGenDecoratorHeightAverageConfiguration configuration) {
        this.veinCount = veinCount;
        this.configuration = configuration;
    }

    public int getVeinCount() {
        return this.veinCount;
    }

    @NotNull
    public WorldGenDecoratorHeightAverageConfiguration getConfiguration() {
        return configuration;
    }

}
