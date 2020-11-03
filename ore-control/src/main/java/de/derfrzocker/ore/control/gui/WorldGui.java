/*
 * MIT License
 *
 * Copyright (c) 2019 Marvin (DerFrZocker)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.derfrzocker.ore.control.gui;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.config.ConfigGui;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.WorldGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.spigot.utils.Version;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class WorldGui extends PageGui<String> {

    private final static Set<Dimension> DIMENSIONS = Collections.unmodifiableSet(Sets.newHashSet(Dimension.OVERWORLD, Dimension.NETHER));
    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @Nullable
    private final CopyAction copyAction;
    private Map<String, WorldOreConfig> worldOreConfigs = new HashMap<>();

    public WorldGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible) {
        super(oreControlValues.getPlugin(), guiSettings.getWorldGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.copyAction = null;

        final WorldGuiSettings worldGuiSettings = guiSettings.getWorldGuiSettings();

        final Permissions permissions = oreControlValues.getPermissions();

        addDecorations();
        init(getStrings(), String[]::new, this::getItemStack, (configName, event) -> new WorldConfigGui(guiSettings, oreControlValues, event.getWhoClicked(), getWorldOreConfig(configName), getDimension(configName)).openSync(event.getWhoClicked()));

        if (permissions.getTemplateCreatePermission().hasPermission(permissible)) {
            addItem(worldGuiSettings.getCreateTemplateSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getCreateTemplateItemStack()), this::handleCreateTemplate);
        }

        if (permissions.getConfigEditPermission().hasPermission(permissible)) {
            addItem(worldGuiSettings.getEditConfigSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getEditConfigItemStack()), event -> new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked()));
        }

        worldOreConfigs = null;
    }

    WorldGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getPlugin(), guiSettings.getWorldGuiSettings());

        Validate.notNull(copyAction, "CopyAction cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.copyAction = copyAction;

        addDecorations();
        init(getStrings(), String[]::new, this::getItemStack, this::handleCopyAction);
    }

    private ItemStack getItemStack(@NotNull final String value) {
        if (worldOreConfigs.containsKey(value) && worldOreConfigs.get(value).isTemplate()) {
            return MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getWorldGuiSettings().getTemplateItemStack(), new MessageValue("template", value));
        } else {
            return MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getWorldGuiSettings().getWorldItemStack(), new MessageValue("world", value));
        }
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

                                    new WorldGui(this.guiSettings, this.oreControlValues, player).openSync(event.getWhoClicked());

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

        Bukkit.getWorlds().stream().filter(world -> DIMENSIONS.contains(oreControlValues.getService().getNMSService().getNMSUtil().getDimension(world))).map(World::getName).forEach(configsSet::add);
        oreControlValues.getService().getAllWorldOreConfigs().forEach(value -> worldOreConfigs.put(value.getName(), value));

        worldOreConfigs.values().stream().filter(value -> !value.isTemplate()).map(WorldOreConfig::getName).forEach(configsSet::add);
        configsSet.addAll(worldOreConfigs.keySet());

        if (copyAction != null && copyAction.isFilterWorldOreConfig()) {
            configsSet.remove(copyAction.getWorldOreConfigSource().getName());
        }

        return configsSet.toArray(new String[0]);
    }

    private void handleCopyAction(@NotNull final String configName, @NotNull final InventoryClickEvent event) {
        copyAction.setWorldOreConfigTarget(getWorldOreConfig(configName));

        copyAction.next(event.getWhoClicked(), this);
    }

    @NotNull
    private WorldOreConfig getWorldOreConfig(@NotNull final String configName) {
        final OreControlService service = oreControlValues.getService();

        final World world = Bukkit.getWorld(configName);

        final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(configName);

        final WorldOreConfig worldOreConfig;

        if (!optionalWorldOreConfig.isPresent()) {
            if (world != null) {
                worldOreConfig = service.createWorldOreConfig(world);
            } else {
                worldOreConfig = service.createWorldOreConfigTemplate(configName);
            }
        } else {
            worldOreConfig = optionalWorldOreConfig.get();
        }

        return worldOreConfig;
    }

    @Nullable
    private Dimension getDimension(String configName) {
        final World world = Bukkit.getWorld(configName);

        if (world == null) {
            return null;
        }

        return oreControlValues.getService().getNMSService().getNMSUtil().getDimension(world);
    }

}
