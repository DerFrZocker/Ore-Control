package de.derfrzocker.feature.common.feature.generator.configuration;

import de.derfrzocker.feature.api.config.resolved.ResolvedGenericConfig;

public final class ResolvedEmptyConfig implements ResolvedGenericConfig {

    private static final ResolvedEmptyConfig INSTANCE = new ResolvedEmptyConfig();

    public static ResolvedEmptyConfig getInstance() {
        return INSTANCE;
    }
}
