/*
 * MIT License
 *
 * Copyright (c) 2019 - 2021 Marvin (DerFrZocker)
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
 *
 */

package de.derfrzocker.ore.control.gui;

import de.derfrzocker.ore.control.Permissions;
import de.derfrzocker.ore.control.api.ConfigType;
import de.derfrzocker.ore.control.api.Dimension;
import de.derfrzocker.ore.control.api.OreControlService;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.gui.config.ConfigGui;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class WorldGui extends PageGui<WorldGui.WorldConfigData> {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @Nullable
    private final CopyAction copyAction;

    public WorldGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible) {
        super(oreControlValues.getPlugin(), guiSettings.getWorldGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.copyAction = null;

        final WorldGuiSettings worldGuiSettings = guiSettings.getWorldGuiSettings();

        final Permissions permissions = oreControlValues.getPermissions();

        addDecorations();
        init(getWorldConfigDatas(), WorldConfigData[]::new, this::getItemStack, (configName, event) -> new WorldConfigGui(guiSettings, oreControlValues, event.getWhoClicked(), getWorldOreConfig(configName), getDimension(configName.configType)).openSync(event.getWhoClicked()));

        if (permissions.getTemplateCreatePermission().hasPermission(permissible)) {
            addItem(worldGuiSettings.getCreateTemplateSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getCreateTemplateItemStack()), this::handleCreateTemplate);
        }

        if (permissions.getConfigEditPermission().hasPermission(permissible)) {
            addItem(worldGuiSettings.getEditConfigSlot(), MessageUtil.replaceItemStack(getPlugin(), worldGuiSettings.getEditConfigItemStack()), event -> new ConfigGui(guiSettings, oreControlValues).openSync(event.getWhoClicked()));
        }
    }

    public WorldGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getPlugin(), guiSettings.getWorldGuiSettings());

        Validate.notNull(copyAction, "CopyAction cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.copyAction = copyAction;

        addDecorations();
        init(getWorldConfigDatas(), WorldConfigData[]::new, this::getItemStack, this::handleCopyAction);

        addItem(guiSettings.getWorldGuiSettings().getAbortSlot(), MessageUtil.replaceItemStack(getPlugin(), guiSettings.getWorldGuiSettings().getAbortItemStack()), (event) -> copyAction.abort(event.getWhoClicked()));
    }

    private ItemStack getItemStack(@NotNull final WorldConfigData worldConfigData) {
        String name = "UNKNOWN";

        if (worldConfigData.worldOreConfig != null) {
            name = worldConfigData.worldOreConfig.getName();
        } else if (worldConfigData.world != null) {
            name = worldConfigData.world.getName();
        }

        switch (worldConfigData.configType) {
            case OVERWORLD:
            case NETHER:
            case UNKNOWN:
                return MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getWorldGuiSettings().getWorldItemStack(),
                        new MessageValue("world", name),
                        new MessageValue("config-type", worldConfigData.configType),
                        new MessageValue("reset-copy", copyAction == null ? "" : "reset-copy."));
            case TEMPLATE:
                return MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getWorldGuiSettings().getTemplateItemStack(),
                        new MessageValue("template", name),
                        new MessageValue("config-type", worldConfigData.configType),
                        new MessageValue("reset-copy", copyAction == null ? "" : "reset-copy."));
            case GLOBAL:
                return MessageUtil.replaceItemStack(getPlugin(), this.guiSettings.getWorldGuiSettings().getGlobalItemStack(),
                        new MessageValue("config-type", worldConfigData.configType),
                        new MessageValue("reset-copy", copyAction == null ? "" : "reset-copy."));
        }

        throw new RuntimeException("No ConfigType found!");
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

    private WorldConfigData[] getWorldConfigDatas() {
        Set<WorldConfigData> worldConfigDatas = new LinkedHashSet<>();
        OreControlService service = oreControlValues.getService();
        final Set<WorldOreConfig> worldOreConfigs = service.getAllWorldOreConfigs();

        for (World world : Bukkit.getWorlds()) {
            Dimension dimension = service.getNMSService().getNMSUtil().getDimension(world);
            ConfigType configType;

            if (dimension == Dimension.OVERWORLD) {
                configType = ConfigType.OVERWORLD;
            } else if (dimension == Dimension.NETHER) {
                configType = ConfigType.NETHER;
            } else {
                continue;
            }

            WorldOreConfig worldOreConfig = service.getWorldOreConfig(world).orElse(null);
            if (worldOreConfig != null) {
                worldOreConfigs.remove(worldOreConfig);
                if (worldOreConfig.getConfigType() != configType) {
                    worldOreConfig.setConfigType(configType);
                    service.saveWorldOreConfig(worldOreConfig);
                }
            }

            worldConfigDatas.add(new WorldConfigData(worldOreConfig, world, configType));
        }

        Set<WorldConfigData> overworld = new LinkedHashSet<>();
        Set<WorldConfigData> nether = new LinkedHashSet<>();
        Set<WorldConfigData> unknown = new LinkedHashSet<>();
        Set<WorldConfigData> template = new LinkedHashSet<>();
        Set<WorldConfigData> global = new LinkedHashSet<>();

        for (WorldOreConfig worldOreConfig : worldOreConfigs) {
            switch (worldOreConfig.getConfigType()) {
                case OVERWORLD:
                    overworld.add(new WorldConfigData(worldOreConfig, null, ConfigType.OVERWORLD));
                    break;
                case NETHER:
                    nether.add(new WorldConfigData(worldOreConfig, null, ConfigType.NETHER));
                    break;
                case TEMPLATE:
                    template.add(new WorldConfigData(worldOreConfig, null, ConfigType.TEMPLATE));
                    break;
                case GLOBAL:
                    global.add(new WorldConfigData(worldOreConfig, null, ConfigType.GLOBAL));
                    break;
                case UNKNOWN:
                    unknown.add(new WorldConfigData(worldOreConfig, null, ConfigType.UNKNOWN));
                    break;
            }
        }
        worldConfigDatas.addAll(overworld);
        worldConfigDatas.addAll(nether);
        worldConfigDatas.addAll(unknown);
        worldConfigDatas.addAll(global);
        worldConfigDatas.addAll(template);


        if (copyAction != null && copyAction.isFilterWorldOreConfig()) {
            WorldConfigData worldConfigData = null;

            for (WorldConfigData worldConfigData1 : worldConfigDatas) {
                WorldOreConfig current = worldConfigData1.worldOreConfig;
                WorldOreConfig source = copyAction.getWorldOreConfigSource();
                if (current != null && source.getName().equals(current.getName()) && current.getConfigType() == source.getConfigType()) {
                    worldConfigData = worldConfigData1;
                }
            }

            if (worldConfigData != null) {
                worldConfigDatas.remove(worldConfigData);
            }
        }

        return worldConfigDatas.toArray(new WorldConfigData[0]);
    }

    private void handleCopyAction(@NotNull final WorldConfigData worldConfigData, @NotNull final InventoryClickEvent event) {
        copyAction.setWorldOreConfigTarget(getWorldOreConfig(worldConfigData));

        copyAction.next(event.getWhoClicked(), () -> new WorldGui(guiSettings, oreControlValues, copyAction));
    }

    @NotNull
    private WorldOreConfig getWorldOreConfig(@NotNull final WorldConfigData worldConfigData) {
        if (worldConfigData.worldOreConfig != null) {
            return worldConfigData.worldOreConfig;
        }

        if (worldConfigData.world != null) {
            World world = worldConfigData.world;
            OreControlService service = oreControlValues.getService();
            final Optional<WorldOreConfig> optionalWorldOreConfig = service.getWorldOreConfig(world.getName());

            return optionalWorldOreConfig.orElseGet(() -> service.createWorldOreConfig(world));
        }

        throw new RuntimeException("No WorldOreConfig found!");
    }

    @Nullable
    private Dimension getDimension(ConfigType configType) {
        switch (configType) {
            case OVERWORLD:
                return Dimension.OVERWORLD;
            case NETHER:
                return Dimension.NETHER;
            case TEMPLATE:
            case GLOBAL:
            case UNKNOWN:
                return null;
        }

        return null;
    }

    protected final class WorldConfigData {
        @Nullable
        private final WorldOreConfig worldOreConfig;
        @Nullable
        private final World world;
        @NotNull
        private final ConfigType configType;

        private WorldConfigData(@Nullable WorldOreConfig worldOreConfig, @Nullable World world, @NotNull ConfigType configType) {
            this.worldOreConfig = worldOreConfig;
            this.world = world;
            this.configType = configType;
        }
    }

}
