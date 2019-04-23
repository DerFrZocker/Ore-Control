package de.derfrzocker.spigot.utils;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandUtil {

    public static void runAsynchronously(final @NonNull CommandSender commandSender, final @NonNull Plugin plugin, final @NonNull Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                commandSender.sendMessage("§4Error while execute command, see console for more information.");
                commandSender.sendMessage("§4Please report the error to the Developer.");
                e.printStackTrace();
            }
        });
    }

}
