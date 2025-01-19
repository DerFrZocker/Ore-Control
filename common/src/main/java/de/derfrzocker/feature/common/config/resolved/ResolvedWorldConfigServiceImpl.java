package de.derfrzocker.feature.common.config.resolved;

import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParser;
import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfigParserId;
import de.derfrzocker.feature.api.config.resolved.ResolvedWorldConfig;
import de.derfrzocker.feature.api.config.resolved.ResolvedWorldConfigService;
import de.derfrzocker.ore.control.api.OreControlManager;
import java.util.Optional;
import org.bukkit.World;

public class ResolvedWorldConfigServiceImpl implements ResolvedWorldConfigService {

    private final OreControlManager manager;

    public ResolvedWorldConfigServiceImpl(OreControlManager manager) {
        this.manager = manager;
    }

    @Override
    public Optional<ResolvedWorldConfig> getByWorld(World world) {
        return Optional.empty();
    }

    @Override
    public <T extends ResolvedGenericConfig> void register(ResolvedGenericConfigParserId id,
                                                           ResolvedGenericConfigParser<T> parser) {

    }
}
