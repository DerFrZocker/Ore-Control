package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class OreSettingsImpl implements OreSettings {

    private final Map<Setting, Integer> settings = new HashMap<>();

    @NonNull
    private final Ore ore;


    public OreSettingsImpl(Ore ore, Map<Setting, Integer> map) {
        this.ore = ore;
        this.settings.putAll(map);
    }

    @Override
    public Optional<Integer> getValue(@NonNull Setting setting) {
        return Optional.ofNullable(settings.get(setting));
    }

    @Override
    public void setValue(@NonNull Setting setting, int value) {
        settings.put(setting, value);
    }

    @Override
    public OreSettingsImpl clone() {
        return new OreSettingsImpl(getOre(), getSettings());
    }

}
