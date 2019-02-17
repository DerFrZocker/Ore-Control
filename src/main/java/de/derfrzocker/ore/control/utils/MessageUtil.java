package de.derfrzocker.ore.control.utils;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtil {

    public static String replacePlaceHolder(@NonNull String string, @NonNull MessageValue... messageValues) {

        string = replaceTranslation(string, messageValues);

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

    // %%translation:[example.string]%
    public static String replaceTranslation(@NonNull String string, @NonNull MessageValue... messageValues) {
        if (Messages.getInstance() == null)
            return string;

        if (!string.contains("%%translation:["))
            return string;

        Pattern pattern = Pattern.compile("%%translation:(.*?)]%");
        Matcher matcher = pattern.matcher(string);

        StringBuilder stringBuilder = new StringBuilder(string);

        while (matcher.find()) {
            String key = stringBuilder.substring(matcher.start() + 15, matcher.end() - 2);

            key = replacePlaceHolder(key, messageValues);

            stringBuilder.replace(matcher.start(), matcher.end(), new MessageKey(Messages.getInstance(), key).getMessage());

            matcher = pattern.matcher(stringBuilder.toString());
        }

        return stringBuilder.toString();
    }


}
