/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control.impl.dao;

import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.spigot.utils.dao.yaml.BasicYamlDao;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

@Deprecated
public class WorldOreConfigYamlDao_Old extends BasicYamlDao<String, WorldOreConfig> implements WorldOreConfigDao {

    public WorldOreConfigYamlDao_Old(@NotNull final File file) {
        super(file);
    }

    @Override
    public Optional<WorldOreConfig> get(@NotNull final String name) {
        return getFromStringKey(name);
    }

    @Override
    public void remove(@NotNull final WorldOreConfig config) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void save(@NotNull final WorldOreConfig config) {
        throw new UnsupportedOperationException("Not supported");
    }

}
