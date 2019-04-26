package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.config.ConfigGui;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.gui.PageSettings;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.*;

public class WorldGui extends PageGui<String> {

    private Map<String, WorldOreConfig> worldOreConfigs = new HashMap<>();

    private final CopyAction copyAction;

    public WorldGui(final Permissible permissible) {
        this.copyAction = null;

        init(getStrings(), String[]::new, WorldGuiSettings.getInstance(), this::getItemStack, (configName, event) -> openSync(event.getWhoClicked(), new WorldConfigGui(getWorldOreConfig(configName), event.getWhoClicked()).getInventory()));

        if (Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(permissible) && !OreControl.is_1_14)
            addItem(WorldGuiSettings.getInstance().getCreateTemplateSlot(), MessageUtil.replaceItemStack(WorldGuiSettings.getInstance().getCreateTemplateItemStack()), this::handleCreateTemplate);

        if (Permissions.EDIT_CONFIG_PERMISSION.hasPermission(permissible))
            addItem(WorldGuiSettings.getInstance().getEditConfigSlot(), MessageUtil.replaceItemStack(WorldGuiSettings.getInstance().getEditConfigItemStack()), event -> openSync(event.getWhoClicked(), new ConfigGui().getInventory()));

        worldOreConfigs = null;
    }

    WorldGui(final @NonNull CopyAction copyAction) {
        this.copyAction = copyAction;
        init(getStrings(), String[]::new, WorldGuiSettings.getInstance(), this::getItemStack, this::handleCopyAction);
    }

    private ItemStack getItemStack(final String value) {
        if (worldOreConfigs.containsKey(value) && worldOreConfigs.get(value).isTemplate())
            return MessageUtil.replaceItemStack(WorldGuiSettings.getInstance().getTemplateItemStack(), new MessageValue("template", value));
        else
            return MessageUtil.replaceItemStack(WorldGuiSettings.getInstance().getWorldItemStack(), new MessageValue("world", value));
    }


    private void handleCreateTemplate(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player)
            new AnvilGUI(OreControl.getInstance(), (Player) event.getWhoClicked(), OreControlMessages.ANVIL_TITLE.getMessage(), (player, value) -> {
                final OreControlService service = OreControl.getService();

                if (Bukkit.getWorld(value) != null || service.getWorldOreConfig(value).isPresent())
                    return OreControlMessages.ANVIL_NAME_ALREADY_EXISTS.getMessage();

                service.createWorldOreConfigTemplate(value);

                openSync(player, new WorldGui(player).getInventory());

                return "";
            });
    }

    private String[] getStrings() {
        final Set<String> configsSet = new LinkedHashSet<>();

        Bukkit.getWorlds().stream().map(World::getName).forEach(configsSet::add);
        OreControl.getService().getAllWorldOreConfigs().forEach(value -> worldOreConfigs.put(value.getName(), value));

        worldOreConfigs.values().stream().filter(value -> !value.isTemplate()).map(WorldOreConfig::getName).forEach(configsSet::add);
        configsSet.addAll(worldOreConfigs.keySet());

        if (copyAction != null && copyAction.isFilterWorldOreConfig())
            configsSet.remove(copyAction.getWorldOreConfigSource().getName());

        return configsSet.toArray(new String[0]);
    }

    private void handleCopyAction(final String configName, final InventoryClickEvent event) {
        copyAction.setWorldOreConfigTarget(getWorldOreConfig(configName));

        copyAction.next(event.getWhoClicked(), this);
    }

    private WorldOreConfig getWorldOreConfig(final String configName) {
        final OreControlService service = OreControl.getService();

        final World world = Bukkit.getWorld(configName);

        final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

        final WorldOreConfig worldOreConfig;

        if (!optionalWorldOreConfig.isPresent())
            if (world != null)
                worldOreConfig = service.createWorldOreConfig(world);
            else
                worldOreConfig = service.createWorldOreConfigTemplate(configName);
        else worldOreConfig = optionalWorldOreConfig.get();

        return worldOreConfig;
    }

    private static final class WorldGuiSettings extends PageSettings {
        private static WorldGuiSettings instance = null;

        private static WorldGuiSettings getInstance() {
            if (instance == null)
                instance = new WorldGuiSettings();

            return instance;
        }

        private WorldGuiSettings() {
            super(OreControl.getInstance(), "data/world_gui.yml");
        }

        private ItemStack getWorldItemStack() {
            return getYaml().getItemStack("world_item_stack").clone();
        }

        private ItemStack getTemplateItemStack() {
            return getYaml().getItemStack("template.item_stack").clone();
        }

        private ItemStack getCreateTemplateItemStack() {
            return getYaml().getItemStack("template.create.item_stack").clone();
        }

        private int getCreateTemplateSlot() {
            return getYaml().getInt("template.create.slot");
        }

        private int getEditConfigSlot() {
            return getYaml().getInt("config.edit.slot");
        }

        private ItemStack getEditConfigItemStack() {
            return getYaml().getItemStack("config.edit.item_stack").clone();
        }

    }

}
