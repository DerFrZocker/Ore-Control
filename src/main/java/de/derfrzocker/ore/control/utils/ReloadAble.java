package de.derfrzocker.ore.control.utils;

import java.util.HashSet;
import java.util.Set;

public interface ReloadAble {

    Set<ReloadAble> RELOAD_ABLES = new HashSet<>();

    void reload();

}
