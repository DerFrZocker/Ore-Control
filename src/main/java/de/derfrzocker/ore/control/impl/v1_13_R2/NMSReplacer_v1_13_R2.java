package de.derfrzocker.ore.control.impl.v1_13_R2;

import de.derfrzocker.ore.control.api.NMSReplacer;
import net.minecraft.server.v1_13_R2.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class NMSReplacer_v1_13_R2 implements NMSReplacer {

    private WorldGenMinableBadlandsGoldOverrider_v1_13_R2 badlandsGoldOverrider = null;

    private WorldGenMinableNormalOverrider_v1_13_R2 normalOverrider = null;

    private WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R2 decoratorBadlandsGoldOverrider = null;

    private WorldGenDecoratorEmeraldOverrider_v1_13_R2 emeraldOverrider = null;

    private WorldGenDecoratorHeightAverageOverrider_v1_13_R2 averageOverrider = null;

    private WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2 decoratorNormalOverrider = null;

    @Override
    public boolean replaceNMS() {
        badlandsGoldOverrider = new WorldGenMinableBadlandsGoldOverrider_v1_13_R2();
        normalOverrider = new WorldGenMinableNormalOverrider_v1_13_R2();
        decoratorBadlandsGoldOverrider = new WorldGenDecoratorNetherHeightBadlandsGoldOverrider_v1_13_R2();
        emeraldOverrider = new WorldGenDecoratorEmeraldOverrider_v1_13_R2();
        averageOverrider = new WorldGenDecoratorHeightAverageOverrider_v1_13_R2();
        decoratorNormalOverrider = new WorldGenDecoratorNetherHeightNormalOverrider_v1_13_R2();

        for (Field field : Biomes.class.getFields()) {
            try {
                replaceBase((BiomeBase) field.get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void replaceBase(BiomeBase base) throws NoSuchFieldException, IllegalAccessException {
        Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> map = get(base);

        List<WorldGenFeatureComposite<?, ?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_ORES);

        list.forEach(value -> {
            try {

                if (replaceBadlandsGold(value))
                    return;

                if (replaceEmerald(value))
                    return;

                if (replaceLapis(value))
                    return;

                replaceNormal(value);
            } catch (NoSuchFieldException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureComposite<?, ?>>> get(BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        Field field = getField(base.getClass(), "aW");

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

    private boolean replaceBadlandsGold(WorldGenFeatureComposite<?, ?> composite) throws NoSuchFieldException, IllegalAccessException {
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
            Field field = getField(composite.getClass(), "a");
            field.setAccessible(true);
            field.set(composite, badlandsGoldOverrider);
        }

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, decoratorBadlandsGoldOverrider);
        }

        return true;
    }

    private boolean replaceEmerald(WorldGenFeatureComposite<?, ?> composite) throws NoSuchFieldException, IllegalAccessException {
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
            field.set(composite, emeraldOverrider);
        }

        return true;
    }

    private boolean replaceLapis(WorldGenFeatureComposite<?, ?> composite) throws NoSuchFieldException, IllegalAccessException {
        Object object;

        {
            Field field = getField(composite.getClass(), "d");
            field.setAccessible(true);
            object = field.get(composite);
        }

        if (!(object instanceof WorldGenDecoratorHeightAverageConfiguration))
            return false;

        {
            Field field = getField(composite.getClass(), "a");
            field.setAccessible(true);
            field.set(composite, normalOverrider);
        }

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, averageOverrider);
        }

        return true;
    }

    private void replaceNormal(WorldGenFeatureComposite<?, ?> composite) throws NoSuchFieldException, IllegalAccessException {
        Object object;

        {
            Field field = getField(composite.getClass(), "b");
            field.setAccessible(true);
            if (!(field.get(composite) instanceof WorldGenFeatureOreConfiguration))
                return;
        }

        {
            Field field = getField(composite.getClass(), "a");
            field.setAccessible(true);
            field.set(composite, normalOverrider);
        }

        {
            Field field = getField(composite.getClass(), "c");
            field.setAccessible(true);
            field.set(composite, decoratorNormalOverrider);
        }

    }

}
