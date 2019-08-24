package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_13_R2.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class NMSReplacer1_13_R2 {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    void replaceNMS() {
        for (Field field : Biomes.class.getFields()) {
            try {
                replaceBase((BiomeBase) field.get(null));
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error while hook in NMS for Biome field: " + field.getName(), e);
            }
        }
    }

    private void replaceBase(final BiomeBase base) throws NoSuchFieldException, IllegalAccessException {
        final Biome biome;

        try {
            biome = Biome.valueOf(IRegistry.BIOME.getKey(base).getKey().toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        final Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> map = get(base);

        final List<WorldGenFeatureComposite<?, ?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_ORES);

        for (WorldGenFeatureComposite<?, ?> composite : list)
            replace(composite, biome);
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> get(final BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        final Field field = getField(base.getClass(), "aW");
        field.setAccessible(true);

        return (Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>>) field.get(base);
    }

    @SuppressWarnings("rawtypes")
    private Field getField(final Class clazz, final String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            final Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    private void replace(final WorldGenFeatureComposite<?, ?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (replaceBadlandsGold(composite, biome))
            return;

        if (replaceEmerald(composite, biome))
            return;

        if (replaceLapis(composite, biome))
            return;

        replaceNormal(composite, biome);
    }

    private boolean replaceBadlandsGold(final WorldGenFeatureComposite<?, ?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(composite.getClass(), "d");
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
            final Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R2(biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceEmerald(final WorldGenFeatureComposite<?, ?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenFeatureDecoratorEmptyConfiguration))
            return false;

        {
            final Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorEmeraldOverrider_v1_13_R2(biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceLapis(final WorldGenFeatureComposite<?, ?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final Object object;

        {
            final Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenDecoratorHeightAverageConfiguration))
            return false;

        {
            final Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorHeightAverageOverrider_v1_13_R2(biome, serviceSupplier));
        }

        return true;
    }

    private void replaceNormal(final WorldGenFeatureComposite<?, ?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        {
            final Field field = getField(composite.getClass(), "b");
            field.setAccessible(true);
            if (!(field.get(composite) instanceof WorldGenFeatureOreConfiguration))
                return;
        }

        {
            final Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2(biome, serviceSupplier));
        }
    }

}
