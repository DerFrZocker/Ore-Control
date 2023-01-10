package de.derfrzocker.ore.control.gui.info;

import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.message.MessageValue;

import java.util.function.Function;

public enum InfoLink {

    GENERATOR_INFO(playerGuiData -> InfoUtil.fromKey("Generator", playerGuiData.getFeature().generator().getKey()),
            playerGuiData -> new MessageValue[]{new MessageValue("generator-namespace", playerGuiData.getFeature().generator().getKey().getNamespace()),
                    new MessageValue("generator-key", playerGuiData.getFeature().generator().getKey().getKey())}),
    PLACEMENT_MODIFIER_INFO(playerGuiData -> InfoUtil.fromKey("Placement-Modifier", playerGuiData.getPlacementModifier().getKey()),
            playerGuiData -> new MessageValue[]{new MessageValue("placement-modifier-namespace", playerGuiData.getPlacementModifier().getKey().getNamespace()),
                    new MessageValue("placement-modifier-key", playerGuiData.getPlacementModifier().getKey().getKey())})
    ;
    private final Function<PlayerGuiData, String> url;
    private final Function<PlayerGuiData, MessageValue[]> messageValues;

    InfoLink(Function<PlayerGuiData, String> url, Function<PlayerGuiData, MessageValue[]> messageValues) {
        this.url = url;
        this.messageValues = messageValues;
    }

    public Function<PlayerGuiData, String> getUrl() {
        return url;
    }

    public Function<PlayerGuiData, MessageValue[]> getMessageValues() {
        return messageValues;
    }
}
