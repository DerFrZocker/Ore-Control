package de.derfrzocker.spigot.utils;

import java.util.LinkedHashSet;
import java.util.Set;

public interface ReloadAble {

    Set<ReloadAble> RELOAD_ABLES = new LinkedHashSet<>();

    void reload();

}
