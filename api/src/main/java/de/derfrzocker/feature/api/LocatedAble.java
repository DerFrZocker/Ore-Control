package de.derfrzocker.feature.api;

import org.jetbrains.annotations.NotNull;

public interface LocatedAble {

    /**
     * The value location were the value is from / is being saved.
     * The value is only present during runtime and is not saved to disk.
     * Therefor it should be set once the value is loaded / set.
     * When the value is saved to a different value location, then the value
     * should be updated accordingly.
     *
     * @return from were the value is, if not location is set {@link ValueLocation#UNKNOWN} is returned
     * @see #setValueLocation(ValueLocation)
     */
    @NotNull
    ValueLocation getValueLocation();

    /**
     * The value location were the value is from / is being saved.
     * The value is only present during runtime and is not saved to disk.
     * Therefor it should be set once the value is loaded / set.
     * When the value is saved to a different value location, then the value
     * should be updated accordingly.
     * <br>
     * When setting a value location it gets passed to all child values.
     *
     * @param valueLocation to set
     * @see #getValueLocation()
     */
    void setValueLocation(@NotNull ValueLocation valueLocation);
}
