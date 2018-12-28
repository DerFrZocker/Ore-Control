package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class Messages {

    @Getter
    @Setter
    private volatile YamlConfiguration file;

    public void sendMessage(@NonNull MessageKey key, @NonNull CommandSender target, @NonNull MessageValue... messageValues) {
        if (file == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    public void broadcastMessage(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        if (file == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    public void broadcastMessage(@NonNull MessageKey key, @NonNull String permission, @NonNull MessageValue... messageValues) {
        if (file == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(value, messageValues), permission));
    }

    private List<String> getStringList(MessageKey key) {
        List<String> stringList;

        if (file.isList(key.getKey()))
            stringList = file.getStringList(key.getKey());
        else if (!file.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(file.getString(key.getKey()));

        return stringList;
    }

}
