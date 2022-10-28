package de.derfrzocker.feature.api.util;

public interface SaveAble {

    /**
     * Returns true if this object has unsaved changes applied.
     * Otherwise, it will return false.
     *
     * @return true if dirty otherwise false.
     */
    boolean isDirty();

    /**
     * Sets the state of the object to not dirty.
     */
    void saved();
}
