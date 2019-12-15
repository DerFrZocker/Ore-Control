package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.config.ConfigGui;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.settings.WorldGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class WorldGui extends PageGui<String> {

    private static WorldGuiSettings worldGuiSettings;

    @NotNull
    private final OreControlValues oreControlValues;
    @Nullable
    private final CopyAction copyAction;
    private Map<String, WorldOreConfig> worldOreConfigs = new HashMap<>();

    public WorldGui(@NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible) {
        super(oreControlValues.getJavaPlugin());

        Validate.notNull(permissible, "Permissible can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.copyAction = null;

        final Permissions permissions = oreControlValues.getPermissions();

        addDecorations();
        init(getStrings(), String[]::new, worldGuiSettings, this::getItemStack, (configName, event) -> new WorldConfigGui(oreControlValues, event.getWhoClicked(), getWorldOreConfig(configName)).openSync(event.getWhoClicked()));

        if (permissions.getTemplateCreatePermission().hasPermission(permissible))
            addItem(worldGuiSettings.getCreateTemplateSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getCreateTemplateItemStack()), this::handleCreateTemplate);

        if (permissions.getConfigEditPermission().hasPermission(permissible))
            addItem(worldGuiSettings.getEditConfigSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getEditConfigItemStack()), event -> new ConfigGui(oreControlValues).openSync(event.getWhoClicked()));

        worldOreConfigs = null;
    }

    WorldGui(@NotNull final OreControlValues oreControlValues, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getJavaPlugin());
        Validate.notNull(copyAction, "CopyAction can not be null");

        checkSettings(oreControlValues.getJavaPlugin());

        this.oreControlValues = oreControlValues;
        this.copyAction = copyAction;

        addDecorations();
        init(getStrings(), String[]::new, worldGuiSettings, this::getItemStack, this::handleCopyAction);
    }

    private static void checkSettings(@NotNull final JavaPlugin javaPlugin) {
        if (worldGuiSettings == null)
            worldGuiSettings = new WorldGuiSettings(javaPlugin, "data/gui/world-gui.yml", true);
    }

    private ItemStack getItemStack(@NotNull final String value) {
        if (worldOreConfigs.containsKey(value) && worldOreConfigs.get(value).isTemplate())
            return MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getTemplateItemStack(), new MessageValue("template", value));
        else
            return MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getWorldItemStack(), new MessageValue("world", value));
    }

    private void handleCreateTemplate(@NotNull final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            try {
                Bukkit.getScheduler().callSyncMethod(getPlugin(), () ->
                        new AnvilGUI.Builder()
                                .plugin(getPlugin())
                                .onComplete((player, value) -> {
                                    final OreControlService service = oreControlValues.getService();

                                    if (Bukkit.getWorld(value) != null || service.getWorldOreConfig(value).isPresent())
                                        return AnvilGUI.Response.text(MessageUtil.replacePlaceHolder(getPlugin(), oreControlValues.getOreControlMessages().getWorldConfigAlreadyExistsMessage().getMessage(), new MessageValue("world-config", value)));

                                    service.createWorldOreConfigTemplate(value);

                                    new WorldGui(oreControlValues, player).openSync(event.getWhoClicked());

                                    return AnvilGUI.Response.text("");
                                })
                                .text(oreControlValues.getOreControlMessages().getGuiAnvilTitleMessage().getMessage())
                                .open((Player) event.getWhoClicked()))
                        .get();
            } catch (final InterruptedException | ExecutionException e) {
                throw new RuntimeException("Unexpected Error while create Template", e);
            }
        }
    }

    private String[] getStrings() {
        final Set<String> configsSet = new LinkedHashSet<>();

        Bukkit.getWorlds().stream().map(World::getName).forEach(configsSet::add);
        oreControlValues.getService().getAllWorldOreConfigs().forEach(value -> worldOreConfigs.put(value.getName(), value));

        worldOreConfigs.values().stream().filter(value -> !value.isTemplate()).map(WorldOreConfig::getName).forEach(configsSet::add);
        configsSet.addAll(worldOreConfigs.keySet());

        if (copyAction != null && copyAction.isFilterWorldOreConfig())
            configsSet.remove(copyAction.getWorldOreConfigSource().getName());

        return configsSet.toArray(new String[0]);
    }

    private void handleCopyAction(@NotNull final String configName, @NotNull final InventoryClickEvent event) {
        copyAction.setWorldOreConfigTarget(getWorldOreConfig(configName));

        copyAction.next(event.getWhoClicked(), this);
    }

    private WorldOreConfig getWorldOreConfig(@NotNull final String configName) {
        final OreControlService service = oreControlValues.getService();

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

}
