package de.derfrzocker.ore.control.utils;

import com.google.common.collect.Lists;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Supplier;

public class Messages implements ReloadAble {

    private YamlConfiguration yaml;
    private final Supplier<Language> languageSupplier;
    private final JavaPlugin plugin;

    public Messages(JavaPlugin plugin, Supplier<Language> supplier) {
        this.languageSupplier = supplier;
        this.plugin = plugin;
        reload();
        RELOAD_ABLES.add(this);
    }

    public void sendMessage(@NonNull MessageKey key, @NonNull CommandSender target, @NonNull MessageValue... messageValues) {
        if (yaml == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    public void broadcastMessage(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        if (yaml == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    public void broadcastMessage(@NonNull MessageKey key, @NonNull String permission, @NonNull MessageValue... messageValues) {
        if (yaml == null)
            throw new IllegalStateException(new NullPointerException("file can't be null"));

        List<String> stringList = getStringList(key);


        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(value, messageValues), permission));
    }

    private List<String> getStringList(MessageKey key) {
        List<String> stringList;

        if (yaml.isList(key.getKey()))
            stringList = yaml.getStringList(key.getKey());
        else if (!yaml.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(yaml.getString(key.getKey()));

        return stringList;
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, languageSupplier.get().getFileLocation());
    }
}
