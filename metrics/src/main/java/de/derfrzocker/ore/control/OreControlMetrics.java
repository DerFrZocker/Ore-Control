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

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.*;
import org.apache.commons.lang.Validate;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class OreControlMetrics {

    private final static int B_STATS_PLUGIN_ID = 4244;

    public OreControlMetrics(@NotNull final Plugin plugin, @NotNull final Supplier<OreControlService> serviceSupplier) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(serviceSupplier, "Service supplier cannot be null");

        final Metrics metrics = new Metrics(plugin, B_STATS_PLUGIN_ID);

        // Config values
        metrics.addCustomChart(new Metrics.SimplePie("used_language", this::getLanguage));
        metrics.addCustomChart(new Metrics.SimplePie("use_safe_mode", this::getUseSafeMode));
        metrics.addCustomChart(new Metrics.SimplePie("use_translate_tab_compilation", this::getUseTranslateTabCompilation));
        metrics.addCustomChart(new Metrics.SimplePie("use_verify_copy_action", this::getUseVerifyCopyAction));
        metrics.addCustomChart(new Metrics.SimplePie("use_verify_reset_action", this::getUseVerifyResetAction));

        metrics.addCustomChart(new Metrics.SingleLineChart("created_templates", () -> Math.toIntExact(serviceSupplier.get().getAllWorldOreConfigs().stream().filter(WorldOreConfig::isTemplate).count())));
        metrics.addCustomChart(new Metrics.SingleLineChart("used_in_worlds", () -> Math.toIntExact(serviceSupplier.get().getAllWorldOreConfigs().stream().filter(worldOreConfig -> !worldOreConfig.isTemplate()).count())));

        metrics.addCustomChart(new Metrics.DrilldownPie("changed_ores", () -> {
            final Map<String, Map<String, Integer>> result = new LinkedHashMap<>();

            for (final WorldOreConfig worldOreConfig : serviceSupplier.get().getAllWorldOreConfigs()) {
                for (final OreSettings oreSettings : worldOreConfig.getOreSettings().values()) {
                    final Map<String, Integer> settingsMap = result.computeIfAbsent(oreSettings.getOre().toString(), ore -> new LinkedHashMap<>());

                    if (oreSettings.isActivated()) {
                        settingsMap.put("ACTIVATED", settingsMap.getOrDefault("ACTIVATED", 0) + 1);
                    } else {
                        settingsMap.put("DEACTIVATED", settingsMap.getOrDefault("DEACTIVATED", 0) + 1);
                    }

                    for (final Setting setting : oreSettings.getSettings().keySet()) {
                        settingsMap.put(setting.toString(), settingsMap.getOrDefault(setting.toString(), 0) + 1);
                    }
                }
            }

            return result;
        }));

        metrics.addCustomChart(new Metrics.DrilldownPie("changed_ores_in_biomes", () -> {
            final Map<String, Map<String, Integer>> result = new LinkedHashMap<>();

            for (final WorldOreConfig worldOreConfig : serviceSupplier.get().getAllWorldOreConfigs()) {
                for (final BiomeOreSettings biomeOreSettings : worldOreConfig.getBiomeOreSettings().values()) {
                    for (final OreSettings oreSettings : biomeOreSettings.getOreSettings().values()) {
                        final Map<String, Integer> settingsMap = result.computeIfAbsent(oreSettings.getOre().toString(), ore -> new LinkedHashMap<>());

                        if (oreSettings.isActivated()) {
                            settingsMap.put("ACTIVATED", settingsMap.getOrDefault("ACTIVATED", 0) + 1);
                        } else {
                            settingsMap.put("DEACTIVATED", settingsMap.getOrDefault("DEACTIVATED", 0) + 1);
                        }

                        for (final Setting setting : oreSettings.getSettings().keySet()) {
                            settingsMap.put(setting.toString(), settingsMap.getOrDefault(setting.toString(), 0) + 1);
                        }
                    }
                }
            }

            return result;
        }));

        metrics.addCustomChart(new Metrics.DrilldownPie("changed_biomes", () -> {
            final Map<String, Map<String, Integer>> result = new LinkedHashMap<>();

            for (final WorldOreConfig worldOreConfig : serviceSupplier.get().getAllWorldOreConfigs()) {
                for (final BiomeOreSettings biomeOreSettings : worldOreConfig.getBiomeOreSettings().values()) {
                    final Map<String, Integer> oreSettingsMap = result.computeIfAbsent(biomeOreSettings.getBiome().toString(), biome -> new LinkedHashMap<>());
                    for (final Ore ore : biomeOreSettings.getOreSettings().keySet()) {
                        oreSettingsMap.put(ore.toString(), oreSettingsMap.getOrDefault(ore.toString(), 0) + 1);
                    }
                }
            }
            return result;
        }));

        if (!metrics.isEnabled()) {
            plugin.getLogger().warning("meh");
        }

    }

    protected abstract String getLanguage();

    protected abstract String getUseSafeMode();

    protected abstract String getUseTranslateTabCompilation();

    protected abstract String getUseVerifyCopyAction();

    protected abstract String getUseVerifyResetAction();

}
