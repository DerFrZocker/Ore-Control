package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.OreControl;
import de.derfrzocker.ore.control.OreControlMessages;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.config.ConfigGui;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.gui.PageSettings;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import lombok.NonNull;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class WorldGui extends PageGui<String> {

    private Map<String, WorldOreConfig> worldOreConfigs = new HashMap<>();

    private final CopyAction copyAction;

    public WorldGui(final Permissible permissible) {
        super(OreControl.getInstance());
        this.copyAction = null;

        init(getStrings(), String[]::new, WorldGuiSettings.getInstance(), this::getItemStack, (configName, event) -> new WorldConfigGui(getWorldOreConfig(configName), event.getWhoClicked()).openSync(event.getWhoClicked()));

        if (Permissions.CREATE_TEMPLATE_PERMISSION.hasPermission(permissible))
            addItem(WorldGuiSettings.getInstance().getCreateTemplateSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), WorldGuiSettings.getInstance().getCreateTemplateItemStack()), this::handleCreateTemplate);

        if (Permissions.EDIT_CONFIG_PERMISSION.hasPermission(permissible))
            addItem(WorldGuiSettings.getInstance().getEditConfigSlot(), MessageUtil.replaceItemStack(OreControl.getInstance(), WorldGuiSettings.getInstance().getEditConfigItemStack()), event -> new ConfigGui().openSync(event.getWhoClicked()));

        worldOreConfigs = null;
    }

    WorldGui(final @NonNull CopyAction copyAction) {
        super(OreControl.getInstance());
        this.copyAction = copyAction;
        init(getStrings(), String[]::new, WorldGuiSettings.getInstance(), this::getItemStack, this::handleCopyAction);
    }

    private ItemStack getItemStack(final String value) {
        if (worldOreConfigs.containsKey(value) && worldOreConfigs.get(value).isTemplate())
            return MessageUtil.replaceItemStack(OreControl.getInstance(), WorldGuiSettings.getInstance().getTemplateItemStack(), new MessageValue("template", value));
        else
            return MessageUtil.replaceItemStack(OreControl.getInstance(), WorldGuiSettings.getInstance().getWorldItemStack(), new MessageValue("world", value));
    }


    private void handleCreateTemplate(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            try {
                Bukkit.getScheduler().callSyncMethod(OreControl.getInstance(), () -> new AnvilGUI(OreControl.getInstance(), (Player) event.getWhoClicked(), OreControlMessages.ANVIL_TITLE.getMessage(), (player, value) -> {
                    final OreControlService service = OreControl.getService();

                    if (Bukkit.getWorld(value) != null || service.getWorldOreConfig(value).isPresent())
                        return OreControlMessages.ANVIL_NAME_ALREADY_EXISTS.getMessage();

                    service.createWorldOreConfigTemplate(value);

                    new WorldGui(player).openSync(event.getWhoClicked());

                    return "";
                })).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Unexpected Error while create Template", e);
            }
        }
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
