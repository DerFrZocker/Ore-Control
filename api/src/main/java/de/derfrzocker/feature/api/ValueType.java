package de.derfrzocker.feature.api;

import de.derfrzocker.feature.api.util.Parser;
import org.bukkit.Keyed;

public interface ValueType<V extends Value<V, T, O>, T extends ValueType<V, T, O>, O> extends Keyed {

    Parser<V> getParser();

    V createNewValue();
}
