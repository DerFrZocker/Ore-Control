package de.derfrzocker.ore.control.utils;

import java.util.LinkedHashSet;
import java.util.Set;

public interface ReloadAble {

    Set<ReloadAble> RELOAD_ABLES = new LinkedHashSet<>();

    void reload();

}
