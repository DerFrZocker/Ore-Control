package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.ReloadAble;
import de.derfrzocker.spigot.utils.command.CommandUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements TabExecutor {

    @NotNull
    private final OreControlValues oreControlValues;

    public ReloadCommand(@NotNull final OreControlValues oreControlValues) {
        Validate.notNull(oreControlValues, "OreControlValues can't be null");

        this.oreControlValues = oreControlValues;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        CommandUtil.runAsynchronously(sender, oreControlValues.getJavaPlugin(), () -> {
            oreControlValues.getOreControlMessages().getCommandReloadBeginMessage().sendMessage(sender);

            ReloadAble.RELOAD_ABLES.forEach(ReloadAble::reload);

            oreControlValues.getOreControlMessages().getCommandReloadEndMessage().sendMessage(sender);
        });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return new ArrayList<>();
    }

}
