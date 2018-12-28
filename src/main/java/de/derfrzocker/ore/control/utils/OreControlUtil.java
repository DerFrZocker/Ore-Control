package de.derfrzocker.ore.control.utils;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import lombok.NonNull;

@SuppressWarnings("Duplicates")
public class OreControlUtil {

    public static int getAmount(@NonNull Ore ore, @NonNull String method, @NonNull WorldOreConfig config) {
        if (ore == Ore.LAPIS)
            switch (method.toLowerCase()) {
                case "vein_size":
                    return config.getLapisSettings().getVeinSize();
                case "veins_per_chunk":
                    return config.getLapisSettings().getVeinsPerChunk();
                case "height_range":
                    return config.getLapisSettings().getHeightRange();
                case "height_center":
                    return config.getLapisSettings().getHeightCenter();
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        if (ore == Ore.EMERALD)
            switch (method.toLowerCase()) {
                case "minimum_ores_per_chunk":
                    return config.getEmeraldSettings().getMinimumOresPerChunk();
                case "ores_per_chunk_range":
                    return config.getEmeraldSettings().getOresPerChunkRange();
                case "height_range":
                    return config.getEmeraldSettings().getHeightRange();
                case "minimum_height":
                    return config.getEmeraldSettings().getMinimumHeight();
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        OreSettings settings = getOreSettings(config, ore);

        switch (method.toLowerCase()) {
            case "vein_size":
                return settings.getVeinSize();
            case "veins_per_chunk":
                return settings.getVeinsPerChunk();
            case "minimum_height":
                return settings.getMinimumHeight();
            case "height_range":
                return settings.getHeightRange();
            case "height_subtract_value":
                return settings.getHeightSubtractValue();
            default:
                throw new IllegalArgumentException(method + " is not a valid method");
        }
    }

    public static void setAmount(@NonNull Ore ore, @NonNull String method, @NonNull WorldOreConfig config, int amount) {
        if (ore == Ore.LAPIS)
            switch (method.toLowerCase()) {
                case "vein_size":
                    config.getLapisSettings().setVeinSize(amount);
                    return;
                case "veins_per_chunk":
                    config.getLapisSettings().setVeinsPerChunk(amount);
                    return;
                case "height_range":
                    config.getLapisSettings().setHeightRange(amount);
                    return;
                case "height_center":
                    config.getLapisSettings().setHeightCenter(amount);
                    return;
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        if (ore == Ore.EMERALD)
            switch (method.toLowerCase()) {
                case "minimum_ores_per_chunk":
                    config.getEmeraldSettings().setMinimumOresPerChunk(amount);
                    return;
                case "ores_per_chunk_range":
                    config.getEmeraldSettings().setOresPerChunkRange(amount);
                    return;
                case "height_range":
                    config.getEmeraldSettings().setHeightRange(amount);
                    return;
                case "minimum_height":
                    config.getEmeraldSettings().setMinimumHeight(amount);
                    return;
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        OreSettings settings = getOreSettings(config, ore);

        switch (method.toLowerCase()) {
            case "vein_size":
                settings.setVeinSize(amount);
                return;
            case "veins_per_chunk":
                settings.setVeinsPerChunk(amount);
                return;
            case "minimum_height":
                settings.setMinimumHeight(amount);
                return;
            case "height_range":
                settings.setHeightRange(amount);
                return;
            case "height_subtract_value":
                settings.setHeightSubtractValue(amount);
                return;
            default:
                throw new IllegalArgumentException(method + " is not a valid method");
        }
    }

    public static OreSettings getOreSettings(@NonNull WorldOreConfig config, @NonNull Ore ore) {
        switch (ore) {
            case DIAMOND:
                return config.getDiamondSettings();
            case COAL:
                return config.getCoalSettings();
            case GOLD:
                return config.getGoldSettings();
            case IRON:
                return config.getIronSettings();
            case REDSTONE:
                return config.getRedstoneSettings();
            case GOLD_BADLANDS:
                return config.getBadlandsGoldSettings();
            default:
                throw new IllegalArgumentException(ore + " is not a valid ore");
        }
    }

    public static OreSettings getDefaultOreSettings(@NonNull Ore ore) {
        switch (ore) {
            case DIAMOND:
                return OreControl.getInstance().getSettings().getDefaultDiamondSettings();
            case COAL:
                return OreControl.getInstance().getSettings().getDefaultCoalSettings();
            case GOLD:
                return OreControl.getInstance().getSettings().getDefaultGoldSettings();
            case IRON:
                return OreControl.getInstance().getSettings().getDefaultIronSettings();
            case REDSTONE:
                return OreControl.getInstance().getSettings().getDefaultRedstoneSettings();
            case GOLD_BADLANDS:
                return OreControl.getInstance().getSettings().getDefaultBadlandsGoldSettings();
            default:
                throw new IllegalArgumentException(ore + " is not a valid ore");
        }
    }

    public static int getDefault(@NonNull Ore ore, @NonNull String method) {

        if (ore == Ore.LAPIS)
            switch (method.toLowerCase()) {
                case "vein_size":
                    return OreControl.getInstance().getSettings().getDefaultLapisSettings().getVeinSize();
                case "veins_per_chunk":
                    return OreControl.getInstance().getSettings().getDefaultLapisSettings().getVeinsPerChunk();
                case "height_range":
                    return OreControl.getInstance().getSettings().getDefaultLapisSettings().getHeightRange();
                case "height_center":
                    return OreControl.getInstance().getSettings().getDefaultLapisSettings().getHeightCenter();
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        if (ore == Ore.EMERALD)
            switch (method.toLowerCase()) {
                case "minimum_ores_per_chunk":
                    return OreControl.getInstance().getSettings().getDefaultEmeraldSettings().getMinimumOresPerChunk();
                case "ores_per_chunk_range":
                    return OreControl.getInstance().getSettings().getDefaultEmeraldSettings().getOresPerChunkRange();
                case "height_range":
                    return OreControl.getInstance().getSettings().getDefaultEmeraldSettings().getHeightRange();
                case "minimum_height":
                    return OreControl.getInstance().getSettings().getDefaultEmeraldSettings().getMinimumHeight();
                default:
                    throw new IllegalArgumentException(method + " is not a valid method");
            }

        OreSettings settings = getDefaultOreSettings(ore);

        switch (method.toLowerCase()) {
            case "vein_size":
                return settings.getVeinSize();
            case "veins_per_chunk":
                return settings.getVeinsPerChunk();
            case "minimum_height":
                return settings.getMinimumHeight();
            case "height_range":
                return settings.getHeightRange();
            case "height_subtract_value":
                return settings.getHeightSubtractValue();
            default:
                throw new IllegalArgumentException(method + " is not a valid method");
        }
    }

}
