/*
 * MIT License
 *
 * Copyright (c) 2019 - 2020 Marvin (DerFrZocker)
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
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.gui.copy.CopyAction;
import de.derfrzocker.ore.control.gui.copy.CopyOresAction;
import de.derfrzocker.ore.control.gui.settings.GuiSettings;
import de.derfrzocker.ore.control.gui.settings.OreGuiSettings;
import de.derfrzocker.ore.control.utils.OreControlValues;
import de.derfrzocker.ore.control.utils.ResetUtil;
import de.derfrzocker.spigot.utils.gui.PageGui;
import de.derfrzocker.spigot.utils.gui.VerifyGui;
import de.derfrzocker.spigot.utils.message.MessageKey;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class OreGui extends PageGui<Ore> {

    @NotNull
    private final GuiSettings guiSettings;
    @NotNull
    private final OreControlValues oreControlValues;
    @NotNull
    private final WorldOreConfig worldOreConfig;
    @Nullable
    private final Biome biome;
    @Nullable
    private final BiomeGroupGui.BiomeGroup biomeGroup;
    @Nullable
    private final Dimension dimension;
    @Nullable
    private final CopyAction copyAction;

    OreGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension, @Nullable final Biome biome) {
        super(oreControlValues.getPlugin(), guiSettings.getOreGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.dimension = dimension;
        this.copyAction = null;

        addDecorations();

        final OreGuiSettings oreGuiSettings = guiSettings.getOreGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();
        final Permissions permissions = oreControlValues.getPermissions();
        final Set<Ore> ores = new LinkedHashSet<>();

        for (final Ore ore : biome == null ? Ore.values() : biome.getOres()) {
            if (dimension != null && ore.getDimension() != dimension) {
                continue;
            }

            if (oreControlValues.getVersion().isOlderThan(ore.getSince())) {
                continue;
            }

            ores.add(ore);
        }

        init(ores.toArray(new Ore[0]), Ore[]::new, this::getOreItemStack, (ore, event) -> {
            new OreSettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome, ore).openSync(event.getWhoClicked());
        });

        addItem(oreGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getBackItemStack()),
                event -> (biome == null ? new WorldConfigGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension) : new BiomeGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension)).openSync(event.getWhoClicked()));

        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, biome == null ? oreGuiSettings.getInfoItemStack() : oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));

        if (permissions.getValueResetPermission().hasPermission(permissible)) {
            addItem(oreGuiSettings.getResetValueSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getResetValueItemStack()), this::handleResetValues);
        }

        if (permissions.getValueCopyPermission().hasPermission(permissible)) {
            addItem(oreGuiSettings.getCopyValueSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getCopyValueItemStack()),
                    event -> new WorldGui(guiSettings, oreControlValues, new CopyOresAction(guiSettings, oreControlValues,
                            () -> new OreGui(guiSettings, oreControlValues, permissible, worldOreConfig, dimension, biome), worldOreConfig, biome, biome == null ? Ore.values() : biome.getOres())).
                            openSync(event.getWhoClicked()));
        }

    }

    public OreGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Biome biome, @NotNull final CopyAction copyAction) {
        super(oreControlValues.getPlugin(), guiSettings.getOreGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(copyAction, "CopyAction cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = biome;
        this.biomeGroup = null;
        this.dimension = null;
        this.copyAction = copyAction;

        addDecorations();

        final OreGuiSettings oreGuiSettings = guiSettings.getOreGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();
        final Set<Ore> ores = new LinkedHashSet<>();

        for (final Ore ore : biome == null ? Ore.values() : biome.getOres()) {
            if (oreControlValues.getVersion().isOlderThan(ore.getSince())) {
                continue;
            }

            if (biome == null) {
                if (copyAction.shouldSet(ore)) {
                    ores.add(ore);
                }
            } else if (copyAction.shouldSet(ore, biome)) {
                ores.add(ore);
            }
        }

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        init(oresArray, Ore[]::new, this::getOreItemStack, (ore, event) -> {
            copyAction.setOreTarget(ore);

            copyAction.next(event.getWhoClicked(), () -> new OreGui(guiSettings, oreControlValues, permissible, worldOreConfig, biome, copyAction));
        });

        addItem(oreGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getBackItemStack()), event -> copyAction.back(event.getWhoClicked()));
        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, biome == null ? oreGuiSettings.getInfoItemStack() : oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
        addItem(oreGuiSettings.getAbortSlot(), MessageUtil.replaceItemStack(getPlugin(), oreGuiSettings.getAbortItemStack()), (event) -> copyAction.abort(event.getWhoClicked()));
    }

    OreGui(@NotNull final GuiSettings guiSettings, @NotNull final OreControlValues oreControlValues, @NotNull final Permissible permissible, @NotNull final WorldOreConfig worldOreConfig, @Nullable final Dimension dimension, @NotNull final BiomeGroupGui.BiomeGroup biomeGroup) {
        super(oreControlValues.getPlugin(), guiSettings.getOreGuiSettings());

        Validate.notNull(permissible, "Permissible cannot be null");
        Validate.notNull(worldOreConfig, "WorldOreConfig cannot be null");
        Validate.notNull(biomeGroup, "BiomeGroup cannot be null");

        this.guiSettings = guiSettings;
        this.oreControlValues = oreControlValues;
        this.worldOreConfig = worldOreConfig;
        this.biome = null;
        this.biomeGroup = biomeGroup;
        this.dimension = dimension;
        this.copyAction = null;

        addDecorations();

        final OreGuiSettings oreGuiSettings = guiSettings.getOreGuiSettings();
        final Plugin plugin = oreControlValues.getPlugin();
        final Set<Ore> ores = new LinkedHashSet<>();

        biomeGroup.getBiomes().stream().map(Biome::getOres).flatMap(Stream::of).distinct().filter(ore -> dimension == null || ore.getDimension() == dimension).filter(ore -> !oreControlValues.getVersion().isOlderThan(ore.getSince())).forEach(ores::add);

        final Ore[] oresArray = ores.toArray(new Ore[0]);

        init(oresArray, Ore[]::new, this::getOreItemStack, (ore, event) -> {
            new OreSettingsGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup, ore).openSync(event.getWhoClicked());
        });


        addItem(oreGuiSettings.getInfoSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getInfoBiomeItemStack(), getMessagesValues()));
        addItem(oreGuiSettings.getBackSlot(), MessageUtil.replaceItemStack(plugin, oreGuiSettings.getBackItemStack()),
                event -> new BiomeGroupGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension).openSync(event.getWhoClicked()));
    }

    private MessageValue[] getMessagesValues() {
        return new MessageValue[]{new MessageValue("world", worldOreConfig.getName()),
                new MessageValue("biome", biome == null ? biomeGroup == null ? "" : biomeGroup.getName() : biome.toString())};
    }

    private void handleResetValues(@NotNull final InventoryClickEvent event) {
        if (oreControlValues.getConfigValues().verifyResetAction()) {
            VerifyGui verifyGui = new VerifyGui(getPlugin(), clickEvent -> {
                if (biome != null) {
                    for (Ore ore : biome.getOres()) {
                        ResetUtil.reset(worldOreConfig, ore, biome);
                    }
                } else {
                    for (Ore ore : Ore.values()) {
                        ResetUtil.reset(worldOreConfig, ore);
                    }
                }

                oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
                oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());

                if (biomeGroup == null) {
                    new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome).openSync(event.getWhoClicked());
                } else {
                    new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup).openSync(event.getWhoClicked());
                }
            }, clickEvent1 -> openSync(event.getWhoClicked()));

            verifyGui.addDecorations();

            verifyGui.openSync(event.getWhoClicked());
            return;
        }
        if (biome != null) {
            for (final Ore ore : biome.getOres()) {
                ResetUtil.reset(worldOreConfig, ore, biome);
            }
        } else {
            for (final Ore ore : Ore.values()) {
                ResetUtil.reset(worldOreConfig, ore);
            }
        }

        oreControlValues.getService().saveWorldOreConfig(worldOreConfig);
        oreControlValues.getOreControlMessages().getGuiResetSuccessMessage().sendMessage(event.getWhoClicked());

        if (biomeGroup == null) {
            new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biome).openSync(event.getWhoClicked());
        } else {
            new OreGui(guiSettings, oreControlValues, event.getWhoClicked(), worldOreConfig, dimension, biomeGroup).openSync(event.getWhoClicked());
        }
    }

    private ItemStack getOreItemStack(@NotNull final Ore ore) {
        OreControlService service = oreControlValues.getService();
        ItemStack itemStack = guiSettings.getOreGuiSettings().getDefaultOreItemStack();

        itemStack.setType(ore.getMaterial());

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {
            if (itemMeta.hasLore()) {
                List<String> oldLore = itemMeta.getLore();
                List<String> newLore = new ArrayList<>();
                for (String line : oldLore) {
                    if (line.contains("%%settings%")) {

                        for (Setting setting : ore.getSettings()) {
                            String settingName = new MessageKey(getPlugin(), "setting." + setting.toString()).getMessage();
                            String value;
                            if (biome == null) {
                                if (biomeGroup == null) {
                                    value = String.valueOf(service.getValue(worldOreConfig, ore, setting));
                                } else {
                                    value = "N/A";
                                }
                            } else {
                                value = String.valueOf(service.getValue(worldOreConfig, biome, ore, setting));
                            }
                            newLore.add(line.
                                    replace("%%settings%", guiSettings.getOreGuiSettings().getDefaultOreItemStackSettingsFormat()).
                                    replace("%setting%", settingName).
                                    replace("%value%", value)
                            );
                        }
                    } else {
                        newLore.add(line);
                    }

                }
                itemMeta.setLore(newLore);
            }
            itemStack.setItemMeta(itemMeta);
        }

        String status;
        if (biome == null) {
            status = String.valueOf(service.isActivated(worldOreConfig, ore));
        } else {
            status = String.valueOf(service.isActivated(worldOreConfig, biome, ore));
        }

        itemStack = MessageUtil.replaceItemStack(getPlugin(), itemStack,
                new MessageValue("ore", ore.toString()),
                new MessageValue("status", status),
                new MessageValue("reset-copy", copyAction == null ? "" : "reset-copy."));

        return itemStack;
    }

}
