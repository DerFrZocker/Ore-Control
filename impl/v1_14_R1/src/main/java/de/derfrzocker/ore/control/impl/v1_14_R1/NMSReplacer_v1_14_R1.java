package de.derfrzocker.ore.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.OreControlService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_14_R1.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("Duplicates")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class NMSReplacer_v1_14_R1 {

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
            biome = Biome.valueOf(IRegistry.BIOME.getKey(base).getKey().toUpperCase());
        } catch (final IllegalArgumentException e) {
            return;
        }

        final Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>> map = get(base);

        final List<WorldGenFeatureConfigured<?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_ORES);

        for (final WorldGenFeatureConfigured<?> composite : list)
            replace(composite, biome);
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>> get(final @NonNull BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        final Field field = getField(BiomeBase.class, "r");
        field.setAccessible(true);

        return (Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>>) field.get(base);
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

    private void replace(final @NonNull WorldGenFeatureConfigured<?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (replaceBadlandsGold(composite, biome))
            return;

        if (replaceEmerald(composite, biome))
            return;

        if (replaceLapis(composite, biome))
            return;

        replaceNormal(composite, biome);
    }

    private boolean replaceBadlandsGold(final @NonNull WorldGenFeatureConfigured<?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (!(composite.b instanceof WorldGenFeatureCompositeConfiguration))
            return false;

        final WorldGenFeatureCompositeConfiguration worldGenFeatureDecoratorConfiguration = (WorldGenFeatureCompositeConfiguration) composite.b;

        if (!(worldGenFeatureDecoratorConfiguration.b.b instanceof WorldGenFeatureChanceDecoratorCountConfiguration))
            return false;

        final WorldGenFeatureChanceDecoratorCountConfiguration configuration = (WorldGenFeatureChanceDecoratorCountConfiguration) worldGenFeatureDecoratorConfiguration.b.b;

        if (configuration.a != 20)
            return false;

        if (configuration.b != 32)
            return false;

        if (configuration.c != 32)
            return false;

        if (configuration.d != 80)
            return false;

        {
            final Field field = getField(WorldGenDecoratorConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureDecoratorConfiguration.b, new WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_14_R1(getDynamicFunction(worldGenFeatureDecoratorConfiguration.b.a), biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceEmerald(final @NonNull WorldGenFeatureConfigured<?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (!(composite.b instanceof WorldGenFeatureCompositeConfiguration))
            return false;

        final WorldGenFeatureCompositeConfiguration worldGenFeatureDecoratorConfiguration = (WorldGenFeatureCompositeConfiguration) composite.b;

        if (!(worldGenFeatureDecoratorConfiguration.b.b instanceof WorldGenFeatureDecoratorEmptyConfiguration))
            return false;

        {
            final Field field = getField(WorldGenDecoratorConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureDecoratorConfiguration.b, new WorldGenDecoratorEmeraldOverrider_v1_14_R1(getDynamicFunction1(worldGenFeatureDecoratorConfiguration.b.a), biome, serviceSupplier));
        }

        return true;
    }

    private boolean replaceLapis(final @NonNull WorldGenFeatureConfigured<?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (!(composite.b instanceof WorldGenFeatureCompositeConfiguration))
            return false;

        final WorldGenFeatureCompositeConfiguration worldGenFeatureDecoratorConfiguration = (WorldGenFeatureCompositeConfiguration) composite.b;

        if (!(worldGenFeatureDecoratorConfiguration.b.b instanceof WorldGenDecoratorHeightAverageConfiguration))
            return false;

        {
            final Field field = getField(WorldGenDecoratorConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureDecoratorConfiguration.b, new WorldGenDecoratorHeightAverageOverrider_v1_14_R1(getDynamicFunction2(worldGenFeatureDecoratorConfiguration.b.a), biome, serviceSupplier));
        }

        return true;
    }

    private void replaceNormal(final @NonNull WorldGenFeatureConfigured<?> composite, final @NonNull Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (!(composite.b instanceof WorldGenFeatureCompositeConfiguration))
            return;

        final WorldGenFeatureCompositeConfiguration worldGenFeatureDecoratorConfiguration = (WorldGenFeatureCompositeConfiguration) composite.b;

        if (!(worldGenFeatureDecoratorConfiguration.b.a instanceof WorldGenDecoratorNetherHeight))
            return;

        {
            final Field field = getField(WorldGenDecoratorConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureDecoratorConfiguration.b, new WorldGenDecoratorNetherHeightNormalOverrider_v1_14_R1(getDynamicFunction(worldGenFeatureDecoratorConfiguration.b.a), biome, serviceSupplier));
        }
    }

    @SuppressWarnings("unchecked")
    private Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorCountConfiguration> getDynamicFunction(final @NonNull WorldGenDecorator<?> worldGenDecorator) throws IllegalAccessException, NoSuchFieldException {
        final Field field = getField(WorldGenDecorator.class, "M");
        field.setAccessible(true);
        return (Function<Dynamic<?>, ? extends WorldGenFeatureChanceDecoratorCountConfiguration>) field.get(worldGenDecorator);
    }

    @SuppressWarnings("unchecked")
    private Function<Dynamic<?>, ? extends WorldGenFeatureDecoratorEmptyConfiguration> getDynamicFunction1(final @NonNull WorldGenDecorator<?> worldGenDecorator) throws IllegalAccessException, NoSuchFieldException {
        final Field field = getField(WorldGenDecorator.class, "M");
        field.setAccessible(true);
        return (Function<Dynamic<?>, ? extends WorldGenFeatureDecoratorEmptyConfiguration>) field.get(worldGenDecorator);
    }

    @SuppressWarnings("unchecked")
    private Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration> getDynamicFunction2(final @NonNull WorldGenDecorator<?> worldGenDecorator) throws IllegalAccessException, NoSuchFieldException {
        final Field field = getField(WorldGenDecorator.class, "M");
        field.setAccessible(true);
        return (Function<Dynamic<?>, ? extends WorldGenDecoratorHeightAverageConfiguration>) field.get(worldGenDecorator);
    }

}
