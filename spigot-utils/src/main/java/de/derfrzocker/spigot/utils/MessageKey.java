package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
@Getter
public class MessageKey {

    @NonNull
    @Getter
    private final Messages messages;

    @NonNull
    private final String key;

    public void sendMessage(@NonNull CommandSender target, @NonNull MessageValue... messageValues) {
        messages.sendMessage(this, target, messageValues);
    }

    public void broadcastMessage(@NonNull MessageValue... messageValues) {
        messages.broadcastMessage(this, messageValues);
    }

    public void broadcastMessage(@NonNull String permission, @NonNull MessageValue... messageValues) {
        messages.broadcastMessage(this, permission, messageValues);
    }

    public String getMessage() {
        return messages.getMessages(this);
    }

}
