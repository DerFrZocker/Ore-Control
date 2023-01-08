package de.derfrzocker.ore.control.gui.info;

import de.derfrzocker.ore.control.OreControl;
import org.bukkit.NamespacedKey;

public final class InfoUtil {

    public static String fromKey(String base, NamespacedKey key) {
        return OreControl.BASE_WIKI_URL + base + ":-" + key.getNamespace().replace("_", "-") + ":-" + key.getKey().replace("_", "-");
    }

}
