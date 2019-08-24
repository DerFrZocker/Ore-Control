package de.derfrzocker.ore.control.command;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.spigot.utils.CommandUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static de.derfrzocker.ore.control.OreControlMessages.*;

@RequiredArgsConstructor
public class CreateCommand implements TabExecutor {

    @NonNull
    private final Supplier<OreControlService> serviceSupplier;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(sender))
            return false;

        if (args.length != 1) {
            CREATE_NOT_ENOUGH_ARGS.sendMessage(sender);
            return true;
        }

        CommandUtil.runAsynchronously(sender, OreControl.getInstance(), () -> {
            final String configName = args[0];

            final OreControlService service = serviceSupplier.get();

            final World world = Bukkit.getWorld(configName);

            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

            if (optionalWorldOreConfig.isPresent() || world != null) {
                CREATE_NAME_ALREADY_EXIST.sendMessage(sender, new MessageValue("config_name", configName));
                return;
            }

            service.createWorldOreConfigTemplate(configName);

            CREATE_SUCCESS.sendMessage(sender, new MessageValue("config_name", configName));

        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
