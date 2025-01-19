package de.derfrzocker.feature.api.config.resolved;

import java.util.Optional;
import org.bukkit.World;

public interface ResolvedWorldConfigService {

    Optional<ResolvedWorldConfig> getByWorld(World world);

    <T extends ResolvedGenericConfig> void register(ResolvedGenericConfigParserId id,
                                                    ResolvedGenericConfigParser<T> parser);
}
