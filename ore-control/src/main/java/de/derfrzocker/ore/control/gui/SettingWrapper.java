package de.derfrzocker.ore.control.gui;

import de.derfrzocker.feature.api.ConfigurationAble;
import de.derfrzocker.feature.api.Setting;

public class SettingWrapper {

    private final Setting setting;
    private final ConfigurationAble settingOwner;

    public SettingWrapper(Setting setting, ConfigurationAble settingOwner) {
        this.setting = setting;
        this.settingOwner = settingOwner;
    }

    public Setting getSetting() {
        return setting;
    }

    public ConfigurationAble getSettingOwner() {
        return settingOwner;
    }
}
