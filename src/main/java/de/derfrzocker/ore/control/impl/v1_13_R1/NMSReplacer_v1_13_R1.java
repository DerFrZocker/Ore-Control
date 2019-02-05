package de.derfrzocker.ore.control.impl.v1_13_R1;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.NMSReplacer;
import net.minecraft.server.v1_13_R1.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class NMSReplacer_v1_13_R1 implements NMSReplacer {

    @Override
    public boolean replaceNMS() {
        for (Field field : Biomes.class.getFields()) {
            try {
                replaceBase((BiomeBase) field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void replaceBase(BiomeBase base) throws Exception {
        Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> map = get(base);

        List<WorldGenFeatureComposite<?, ?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_ORES);

        Biome biome;
        try {
            biome = Biome.valueOf(BiomeBase.REGISTRY_ID.b(base).getKey().toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        list.forEach(value -> {
            try {

                if (replaceBadlandsGold(value, biome))
                    return;

                if (replaceEmerald(value, biome))
                    return;

                if (replaceLapis(value, biome))
                    return;

                replaceNormal(value, biome);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> get(BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        Field field = getField(base.getClass(), "aX");

        field.setAccessible(true);

        return (Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>>) field.get(base);
    }

    @SuppressWarnings("rawtypes")
    private Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    private boolean replaceBadlandsGold(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        Object object;

        {
            Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenFeatureChanceDecoratorCountConfiguration))
            return false;

        WorldGenFeatureChanceDecoratorCountConfiguration configuration = (WorldGenFeatureChanceDecoratorCountConfiguration) object;

        if (configuration.a != 20)
            return false;

        if (configuration.b != 32)
            return false;

        if (configuration.c != 32)
            return false;

        if (configuration.d != 80)
            return false;

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R1(biome));
        }

        return true;
    }

    private boolean replaceEmerald(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        Object object;

        {
            Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenFeatureDecoratorEmptyConfiguration))
            return false;

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorEmeraldOverrider_v1_13_R1(biome));
        }

        return true;
    }

    private boolean replaceLapis(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        Object object;

        {
            Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenDecoratorHeightAverageConfiguration))
            return false;

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorHeightAverageOverrider_v1_13_R1(biome));
        }

        return true;
    }

    private void replaceNormal(WorldGenFeatureComposite<?, ?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        {
            Field field = getField(composite.getClass(), "b");
            field.setAccessible(true);
            if (!(field.get(composite) instanceof WorldGenFeatureOreConfiguration))
                return;
        }

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, new WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R1(biome));
        }
    }

}
