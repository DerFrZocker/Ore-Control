package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.EmeraldSettings;
import de.derfrzocker.ore.control.api.LapisSettings;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class WorldOreConfigYamlImpl extends WorldOreConfigImpl implements ConfigurationSerializable {

    public WorldOreConfigYamlImpl(String world, OreSettings diamondSettings, OreSettings redstoneSettings, OreSettings coalSettings, OreSettings goldSettings, OreSettings badlandsGoldSettings, OreSettings ironSettings, LapisSettings lapisSettings, EmeraldSettings emeraldSettings) {
        super(world, diamondSettings, redstoneSettings, coalSettings, goldSettings, badlandsGoldSettings, ironSettings, lapisSettings, emeraldSettings);
    }

    public WorldOreConfigYamlImpl(WorldOreConfig config) {
        super(config.getWorld(),
                config.getDiamondSettings() instanceof ConfigurationSerializable ? config.getDiamondSettings() : new OreSettingsYamlImpl(config.getDiamondSettings()),
                config.getRedstoneSettings() instanceof ConfigurationSerializable ? config.getRedstoneSettings() : new OreSettingsYamlImpl(config.getRedstoneSettings()),
                config.getCoalSettings() instanceof ConfigurationSerializable ? config.getCoalSettings() : new OreSettingsYamlImpl(config.getCoalSettings()),
                config.getGoldSettings() instanceof ConfigurationSerializable ? config.getGoldSettings() : new OreSettingsYamlImpl(config.getGoldSettings()),
                config.getBadlandsGoldSettings() instanceof ConfigurationSerializable ? config.getBadlandsGoldSettings() : new OreSettingsYamlImpl(config.getBadlandsGoldSettings()),
                config.getIronSettings() instanceof ConfigurationSerializable ? config.getIronSettings() : new OreSettingsYamlImpl(config.getIronSettings()),
                config.getLapisSettings() instanceof ConfigurationSerializable ? config.getLapisSettings() : new LapisSettingsYamlImpl(config.getLapisSettings()),
                config.getEmeraldSettings() instanceof ConfigurationSerializable ? config.getEmeraldSettings() : new EmeraldSettingsYamlImpl(config.getEmeraldSettings()));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("world", getWorld());
        map.put("diamond", getDiamondSettings());
        map.put("redstone", getRedstoneSettings());
        map.put("coal", getCoalSettings());
        map.put("gold_normal", getGoldSettings());
        map.put("iron", getIronSettings());
        map.put("lapis", getLapisSettings());
        map.put("gold_badlands", getBadlandsGoldSettings());
        map.put("emerald", getEmeraldSettings());

        return map;
    }

    public static WorldOreConfigYamlImpl deserialize(Map<String, Object> map) {
        return new WorldOreConfigYamlImpl(builder().
                world((String) map.get("world")).
                diamondSettings((OreSettings) map.getOrDefault("diamond", OreControl.getInstance().getSettings().getDefaultDiamondSettings())).
                redstoneSettings((OreSettings) map.getOrDefault("redstone", OreControl.getInstance().getSettings().getDefaultRedstoneSettings())).
                coalSettings((OreSettings) map.getOrDefault("coal", OreControl.getInstance().getSettings().getDefaultDiamondSettings())).
                goldSettings((OreSettings) map.getOrDefault("gold_normal", OreControl.getInstance().getSettings().getDefaultDiamondSettings())).
                ironSettings((OreSettings) map.getOrDefault("iron", OreControl.getInstance().getSettings().getDefaultDiamondSettings())).
                lapisSettings((LapisSettings) map.getOrDefault("lapis", OreControl.getInstance().getSettings().getDefaultLapisSettings())).
                emeraldSettings((EmeraldSettings) map.getOrDefault("emerald", OreControl.getInstance().getSettings().getDefaultEmeraldSettings())).
                badlandsGoldSettings((OreSettings) map.getOrDefault("gold_badlands", OreControl.getInstance().getSettings().getDefaultBadlandsGoldSettings())).
                build());
    }

}
