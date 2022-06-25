/*
 * MIT License
 *
 * Copyright (c) 2019 - 2022 Marvin (DerFrZocker)
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

package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.OreControlRegistries;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class Stats {

    private final static int PLUGIN_ID = 4244;

    private final Map<String, Integer> biomeNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> featureNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> placementModifierNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> featureGeneratorNamespace = new LinkedHashMap<>();

    public Stats(JavaPlugin plugin, OreControlRegistries registries) {
        Metrics metrics = new Metrics(plugin, PLUGIN_ID);
        metrics.addCustomChart(new AdvancedPie("biome_namespace", () -> biomeNamespace));
        metrics.addCustomChart(new AdvancedPie("feature_namespace", () -> featureNamespace));
        metrics.addCustomChart(new AdvancedPie("placement_modifier_namespace", () -> placementModifierNamespace));
        metrics.addCustomChart(new AdvancedPie("feature_generator_namespace", () -> featureGeneratorNamespace));

        registries.getBiomeRegistry().getValues().keySet().forEach(key -> biomeNamespace.put(key.getNamespace(), 1));
        registries.getFeatureRegistry().getValues().keySet().forEach(key -> featureNamespace.put(key.getNamespace(), 1));
        registries.getPlacementModifierRegistry().getValues().keySet().forEach(key -> placementModifierNamespace.put(key.getNamespace(), 1));
        registries.getFeatureGeneratorRegistry().getValues().keySet().forEach(key -> featureGeneratorNamespace.put(key.getNamespace(), 1));
    }
}
