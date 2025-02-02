package de.derfrzocker.feature.common.config.resolved;

import de.derfrzocker.feature.api.PlacedFeature;
import de.derfrzocker.feature.api.config.ConfigLayerService;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParserId;
import de.derfrzocker.feature.api.config.resolved.ResolvedWorldConfig;
import de.derfrzocker.feature.api.config.resolved.ResolvedWorldConfigService;
import de.derfrzocker.feature.api.config.world.WorldConfig;
import de.derfrzocker.feature.api.config.world.WorldConfigService;
import de.derfrzocker.feature.api.config.world.WorldId;
import de.derfrzocker.feature.api.world.WorldData;
import de.derfrzocker.feature.api.world.WorldDataService;
import de.derfrzocker.ore.control.api.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResolvedWorldConfigServiceImpl implements ResolvedWorldConfigService {

    private final Map<ResolvedGenericConfigParserId, ResolvedGenericConfigParser<?>> parsers = new HashMap<>();
    private final Map<WorldId, ResolvedWorldConfig> cache = new HashMap<>();
    private final WorldConfigService worldConfigService;
    private final WorldDataService worldDataService;
    private final ConfigLayerService layerService;
    private ResolvedWorldConfig defaultConfig;

    public ResolvedWorldConfigServiceImpl(WorldConfigService worldConfigService, WorldDataService worldDataService, ConfigLayerService layerService) {
        this.worldConfigService = worldConfigService;
        this.worldDataService = worldDataService;
        this.layerService = layerService;
    }

    @Override
    public Optional<ResolvedWorldConfig> getById(WorldId id) {
        return this.cache.computeIfAbsent(id, _id -> {

        });
    }

    @Override
    public <T extends ResolvedGenericConfig> void register(ResolvedGenericConfigParserId id,
                                                           ResolvedGenericConfigParser<T> parser) {

        this.parsers.put(id, parser);
    }

    private ResolvedWorldConfig load(WorldId id) {
        Optional<WorldConfig> worldConfig = this.worldConfigService.getById(id);

        if (worldConfig.isEmpty()) {
            if (this.defaultConfig == null) {
                this.defaultConfig = null; // TODO 2025-01-19: FIX THIS
            }
            return this.defaultConfig;
        }

        Optional<WorldData> worldData = this.worldDataService.getById(id);

        if (worldData.isEmpty()) {
            if (this.defaultConfig == null) {
                this.defaultConfig = null; // TODO 2025-02-02: FIX THIS
            }
            // TODO 2025-02-02: This should not happen log this
            return this.defaultConfig;
        }

        for (Biome biome : worldData.get().possibleBiomes()) {
            for (PlacedFeature placedFeature : biome.getPlacedFeatures()) {
            }
        }

        return load();
    }
}
