package de.derfrzocker.ore.control.gui.info;

import de.derfrzocker.ore.control.gui.PlayerGuiData;

import java.util.function.Function;

public enum InfoLink {

    GENERATOR_INFO(playerGuiData -> InfoUtil.fromKey("Generator", playerGuiData.getFeature().generator().getKey())),
    PLACEMENT_MODIFIER_INFO(playerGuiData -> InfoUtil.fromKey("Placement-Modifier", playerGuiData.getPlacementModifier().getKey()))
    ;
    private final Function<PlayerGuiData, String> url;

    InfoLink(Function<PlayerGuiData, String> url) {
        this.url = url;
    }

    public Function<PlayerGuiData, String> getUrl() {
        return url;
    }
}
