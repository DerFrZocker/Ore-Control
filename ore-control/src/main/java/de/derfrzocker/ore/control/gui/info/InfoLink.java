package de.derfrzocker.ore.control.gui.info;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.message.MessageValue;

import java.util.function.BiFunction;

public enum InfoLink {

    GENERATOR_INFO((playerGuiData, data) -> InfoUtil.fromKey("Generator", playerGuiData.getFeature().generator().getKey()),
            (playerGuiData, data) -> new MessageValue[]{new MessageValue("generator-namespace", playerGuiData.getFeature().generator().getKey().getNamespace()),
                    new MessageValue("generator-key", playerGuiData.getFeature().generator().getKey().getKey())}),
    PLACEMENT_MODIFIER_INFO((playerGuiData, data) -> InfoUtil.fromKey("Placement-Modifier", playerGuiData.getPlacementModifier().getKey()),
            (playerGuiData, data) -> new MessageValue[]{new MessageValue("placement-modifier-namespace", playerGuiData.getPlacementModifier().getKey().getNamespace()),
                    new MessageValue("placement-modifier-key", playerGuiData.getPlacementModifier().getKey().getKey())}),
    INVENTORY_GUI_SCREENS_EXPLAINED((playerGuiData, data) -> OreControl.BASE_WIKI_URL + "Inventory-Gui-Screens-Explained#" + data[0],
            (playerGuiData, data) -> new MessageValue[]{new MessageValue("screen-name-key", data[1])}),
    COMMON_QUESTIONS((playerGuiData, data) -> OreControl.BASE_WIKI_URL + "Common-Questions#" + data[0],
            (playerGuiData, data) -> new MessageValue[]{new MessageValue("common-question-key", data[0])});

    private final BiFunction<PlayerGuiData, String[], String> url;
    private final BiFunction<PlayerGuiData, String[], MessageValue[]> messageValues;

    InfoLink(BiFunction<PlayerGuiData, String[], String> url, BiFunction<PlayerGuiData, String[], MessageValue[]> messageValues) {
        this.url = url;
        this.messageValues = messageValues;
    }

    public BiFunction<PlayerGuiData, String[], String> getUrl() {
        return url;
    }

    public BiFunction<PlayerGuiData, String[], MessageValue[]> getMessageValues() {
        return messageValues;
    }
}
