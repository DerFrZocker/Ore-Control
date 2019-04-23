package de.derfrzocker.ore.control.impl;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.OreSettings;
import de.derfrzocker.ore.control.api.Setting;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class OreSettingsYamlImpl implements ConfigurationSerializable, OreSettings {

    private static final String ORE_KEY = "ore";
    private static final String STATUS_KEY = "status";

    @Getter
    private final Map<Setting, Integer> settings = new ConcurrentHashMap<>();

    @NonNull
    @Getter
    private final Ore ore;

    @Getter
    @Setter
    private boolean activated = true;

    public OreSettingsYamlImpl(final @NonNull Ore ore, final @NonNull Map<Setting, Integer> settings) {
        this.ore = ore;
        this.settings.putAll(settings);
    }

    @Override
    public Optional<Integer> getValue(final @NonNull Setting setting) {
        return Optional.ofNullable(settings.get(setting));
    }

    @Override
    public void setValue(final @NonNull Setting setting, final int value) {
        settings.put(setting, value);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put(ORE_KEY, getOre().toString());

        if (!activated)
            map.put(STATUS_KEY, false);

        getSettings().forEach((key, value) -> map.put(key.toString(), value));

        return map;
    }

    @Override
    public OreSettingsYamlImpl clone() {
        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(getOre(), getSettings());
        oreSettingsYaml.setActivated(isActivated());
        return oreSettingsYaml;
    }

    public static OreSettingsYamlImpl deserialize(final Map<String, Object> map) {
        final Map<Setting, Integer> settings = new LinkedHashMap<>();
        final OreControlService service = Bukkit.getServicesManager().load(OreControlService.class);

        map.entrySet().stream().filter(entry -> service.isSetting(entry.getKey())).
                forEach(entry -> settings.put(Setting.valueOf(entry.getKey().toUpperCase()), (Integer) entry.getValue()));

        final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(Ore.valueOf(((String) map.get(ORE_KEY)).toUpperCase()), settings);

        if (map.containsKey(STATUS_KEY))
            oreSettingsYaml.setActivated((boolean) map.get(STATUS_KEY));

        return oreSettingsYaml;
    }

}
