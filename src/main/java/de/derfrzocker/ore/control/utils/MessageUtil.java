package de.derfrzocker.ore.control.utils;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class MessageUtil {

    public static String replacePlaceHolder(@NonNull String string, @NonNull MessageValue... messageValues) {

        string = ChatColor.translateAlternateColorCodes('&', string);

        for (MessageValue value : messageValues)
            string = string.replace("%" + value.getKey() + "%", value.getValue());

        return string;
    }

    @SuppressWarnings("WeakerAccess")
    public static List<String> replaceList(@NonNull List<String> strings, @NonNull MessageValue... messageValues) {
        List<String> list = new LinkedList<>();

        strings.forEach(value -> list.add(replacePlaceHolder(value, messageValues)));

        return list;
    }

    public static ItemStack replaceItemStack(@NonNull ItemStack itemStack, @NonNull MessageValue... messageValues) {
        itemStack = itemStack.clone();

        if (!itemStack.hasItemMeta())
            return itemStack;

        itemStack.setItemMeta(replaceItemMeta(itemStack.getItemMeta(), messageValues));

        return itemStack;
    }

    @SuppressWarnings("WeakerAccess")
    public static ItemMeta replaceItemMeta(@NonNull ItemMeta meta, @NonNull MessageValue... messageValues) {
        meta = meta.clone();

        if (meta.hasDisplayName())
            meta.setDisplayName(replacePlaceHolder(meta.getDisplayName(), messageValues));

        if (meta.hasLore())
            meta.setLore(replaceList(meta.getLore(), messageValues));

        return meta;
    }


}
