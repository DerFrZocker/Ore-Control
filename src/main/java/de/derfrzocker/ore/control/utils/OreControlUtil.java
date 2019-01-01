package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import lombok.NonNull;

@SuppressWarnings("Duplicates")
public class OreControlUtil {

    public static int getAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config) {
        return config.getOreSettings(ore).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!"));
    }

    public static void setAmount(@NonNull Ore ore, @NonNull Setting setting, @NonNull WorldOreConfig config, int amount) {
        config.getOreSettings(ore).setValue(setting, amount);
    }

    public static int getDefault(@NonNull Ore ore, @NonNull Setting setting) {
        return OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!"));
    }

    public static boolean isSave(@NonNull Ore ore, @NonNull Setting setting, int amount) {
        return OreControl.getInstance().getSettings().getMinSettings(ore).getValue(setting).orElseThrow(() -> new IllegalArgumentException("The ore '" + ore.toString() + "' don't have the setting '" + setting + "'!")) <= amount;
    }

}
