package de.derfrzocker.ore.control.api;

public interface WorldOreConfig {

    String getWorld();

    OreSettings getDiamondSettings();

    OreSettings getRedstoneSettings();

    OreSettings getCoalSettings();

    OreSettings getGoldSettings();

    OreSettings getBadlandsGoldSettings();

    OreSettings getIronSettings();

    LapisSettings getLapisSettings();

    EmeraldSettings getEmeraldSettings();

    void setDiamondSettings(OreSettings oreSettings);

    void setRedstoneSettings(OreSettings oreSettings);

    void setCoalSettings(OreSettings oreSettings);

    void setGoldSettings(OreSettings oreSetting);

    void setBadlandsGoldSettings(OreSettings oreSetting);

    void setIronSettings(OreSettings oreSettings);

    void setLapisSettings(LapisSettings lapisSettings);

    void setEmeraldSettings(EmeraldSettings emeraldSettings);

}
