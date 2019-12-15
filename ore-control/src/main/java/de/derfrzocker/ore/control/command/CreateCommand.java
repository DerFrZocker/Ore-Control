package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.command.CommandUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateCommand implements TabExecutor {

    @NotNull
    private final OreControlValues oreControlValues;

    public CreateCommand(@NotNull final OreControlValues oreControlValues) {
        Validate.notNull(oreControlValues, "OreControlValues can't be null");

        this.oreControlValues = oreControlValues;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (args.length != 1) {
            oreControlValues.getOreControlMessages().getCommandCreateNotEnoughArgsMessage().sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, oreControlValues.getJavaPlugin(), () -> {
            final String configName = args[0];

            final OreControlService service = oreControlValues.getService();

            final World world = Bukkit.getWorld(configName);

            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

            if (optionalWorldOreConfig.isPresent() || world != null) {
                oreControlValues.getOreControlMessages().getWorldConfigAlreadyExistsMessage().sendMessage(sender, new MessageValue("world-config", configName));
                return;
            }

            service.createWorldOreConfigTemplate(configName);

            oreControlValues.getOreControlMessages().getCommandCreateSuccessMessage().sendMessage(sender, new MessageValue("world-config", configName));

        });

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        return new ArrayList<>();
    }

}
