package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import lombok.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@EqualsAndHashCode
public class OreSettingsYamlImpl implements ConfigurationSerializable, OreSettings {

    private static final String ORE_KEY = "ore";

    private static final String STATUS_KEY = "status";

    @Getter
    private final Map<Setting, Integer> settings = new HashMap<>();

    @NonNull
    @Getter
    private final Ore ore;

    @Getter
    @Setter
    private boolean activated = true;

    public OreSettingsYamlImpl(Ore ore, Map<Setting, Integer> map) {
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
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(ORE_KEY, getOre().toString());

        getSettings().forEach((key, value) -> map.put(key.toString(), value));

        return map;
    }

    @Override
    public OreSettingsYamlImpl clone() {
        return new OreSettingsYamlImpl(getOre(), getSettings());
    }

    public static OreSettingsYamlImpl deserialize(Map<String, Object> map) {
        Map<Setting, Integer> settings = new HashMap<>();

        map.entrySet().stream().filter(entry -> OreControlUtil.isSetting(entry.getKey())).
                forEach(entry -> settings.put(Setting.valueOf(entry.getKey().toUpperCase()), (Integer) entry.getValue()));

        OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(Ore.valueOf(((String) map.get(ORE_KEY)).toUpperCase()), settings);

        if (map.containsKey(STATUS_KEY))
            oreSettingsYaml.setActivated((Boolean) map.get(STATUS_KEY));

        return oreSettingsYaml;
    }

}
