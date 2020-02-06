/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
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
 */

package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R1.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class NMSReplacer_v1_13_R1 {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    void replaceNMS() {
        for (final Field field : Biomes.class.getFields()) {
            try {
                replaceBase((BiomeBase) field.get(null));
            } catch (final Exception e) {
                throw new RuntimeException("Unexpected error while hook in NMS for Biome field: " + field.getName(), e);
            }
        }
    }

    private void replaceBase(final @NonNull BiomeBase base) throws NoSuchFieldException, IllegalAccessException {
        final Biome biome;

        try {
            biome = Biome.valueOf(BiomeBase.REGISTRY_ID.b(base).getKey().toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }

        final Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> map = get(base);

        final List<WorldGenFeatureComposite<?, ?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_ORES);

        for (final WorldGenFeatureComposite<?, ?> composite : list)
            replace(composite, biome);

        final List<WorldGenFeatureComposite<?, ?>> decorations = map.get(WorldGenStage.Decoration.UNDERGROUND_DECORATION);

        for (final WorldGenFeatureComposite<?, ?> composite : decorations)
            replaceDecorations(composite, biome);
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> get(final @NonNull BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        final Field field = getField(BiomeBase.class, "aX");
        field.setAccessible(true);

        return (Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>>) field.get(base);
    }

    @SuppressWarnings("rawtypes")
    private Field getField(final @NonNull Class clazz, final @NonNull String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (final NoSuchFieldException e) {
            final Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    private void replace(final @NonNull WorldGenFeatureComposite<?, ?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (replaceBadlandsGold(composite, biome))
            return;

        if (replaceEmerald(composite, biome))
            return;

        if (replaceLapis(composite, biome))
            return;

        replaceNormal(composite, biome);
    }

    private void replaceDecorations(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (replaceNetherQuarz(composite, biome))
            return;
    }

    private boolean replaceNetherQuarz(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {

        {
            final Field field = getField(WorldGenFeatureComposite.class, "b");
            field.setAccessible(true);
            if (!(field.get(composite) instanceof WorldGenFeatureOreConfiguration))
                return false;

            final WorldGenFeatureOreConfiguration configuration = (WorldGenFeatureOreConfiguration) field.get(composite);

            if (configuration.d.getBlock() != Blocks.NETHER_QUARTZ_ORE)
                return false;
        }

        {
            final Field field = getField(WorldGenFeatureComposite.class, "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R1(biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceBadlandsGold(final @NonNull WorldGenFeatureComposite<?, ?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenFeatureChanceDecoratorCountConfiguration))
            return false;

        final WorldGenFeatureChanceDecoratorCountConfiguration configuration = (WorldGenFeatureChanceDecoratorCountConfiguration) object;

        if (configuration.a != 20)
            return false;

        if (configuration.b != 32)
            return false;

        if (configuration.c != 32)
            return false;

        if (configuration.d != 80)
            return false;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R1(biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceEmerald(final @NonNull WorldGenFeatureComposite<?, ?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenFeatureDecoratorEmptyConfiguration))
            return false;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorEmeraldOverrider_v1_13_R1(biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceLapis(final @NonNull WorldGenFeatureComposite<?, ?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenDecoratorHeightAverageConfiguration))
            return false;

        {
            final Field field = getField(WorldGenFeatureComposite.class, "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorHeightAverageOverrider_v1_13_R1(biome, serviceSupplier));
        }

        return true;
    }

    private void replaceNormal(final @NonNull WorldGenFeatureComposite<?, ?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        {
            final Field field = getField(WorldGenFeatureComposite.class, "b");
            field.setAccessible(true);
            if (!(field.get(composite) instanceof WorldGenFeatureOreConfiguration))
                return;
        }

        {
            final Field field = getField(WorldGenFeatureComposite.class, "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R1(biome, serviceSupplier));
        }
    }

}
