package de.derfrzocker.feature.api.config.resolved;

import de.derfrzocker.feature.api.config.world.WorldId;

import java.util.Optional;

public interface ResolvedWorldConfigService {

    Optional<ResolvedWorldConfig> getById(WorldId id);

    <T extends ResolvedGenericConfig> void register(ResolvedGenericConfigParserId id,
                                                    ResolvedGenericConfigParser<T> parser);
}
