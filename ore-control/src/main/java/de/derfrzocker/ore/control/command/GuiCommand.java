package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.gui.WorldGui;
import de.derfrzocker.ore.control.utils.OreControlValues;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuiCommand implements TabExecutor {

    @NotNull
    private final OreControlValues oreControlValues;

    public GuiCommand(@NotNull final OreControlValues oreControlValues) {
        Validate.notNull(oreControlValues, "OreControlValues can't be null");

        this.oreControlValues = oreControlValues;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!(sender instanceof Player)) {
            oreControlValues.getOreControlMessages().getCommandPlayerOnlyMessage().sendMessage(sender);
            return true;
        }

        new WorldGui(oreControlValues, sender).openSync((Player) sender);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return new ArrayList<>();
    }

}
