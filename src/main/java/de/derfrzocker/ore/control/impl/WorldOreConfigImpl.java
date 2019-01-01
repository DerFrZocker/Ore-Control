package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class WorldOreConfigImpl implements WorldOreConfig {

    @Getter
    private final Map<Ore, OreSettings> oreSettings = new HashMap<>();

    @Getter
    @NonNull
    private final String world;

    public WorldOreConfigImpl(String world, Map<Ore, OreSettings> map) {
        this.world = world;
        this.oreSettings.putAll(map);
    }

    @Override
    public OreSettings getOreSettings(@NonNull Ore ore) {
        return oreSettings.computeIfAbsent(ore, OreControl.getInstance().getSettings()::getDefaultSettings);
    }

    @Override
    public void setOreSettings(OreSettings oreSettings) {
        this.oreSettings.put(oreSettings.getOre(), oreSettings);
    }
}
