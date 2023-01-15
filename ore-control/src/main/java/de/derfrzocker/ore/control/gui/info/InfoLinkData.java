package de.derfrzocker.ore.control.gui.info;

import de.derfrzocker.ore.control.gui.PlayerGuiData;
import de.derfrzocker.spigot.utils.message.MessageValue;

public record InfoLinkData(InfoLink infoLink, String[] data) {

    public static InfoLinkData of(InfoLink infoLink, String... data) {
        return new InfoLinkData(infoLink, data);
    }

    public String url(PlayerGuiData playerGuiData) {
        return infoLink().getUrl().apply(playerGuiData, data());
    }

    public MessageValue[] messageValues(PlayerGuiData playerGuiData) {
        return infoLink().getMessageValues().apply(playerGuiData, data());
    }
}
