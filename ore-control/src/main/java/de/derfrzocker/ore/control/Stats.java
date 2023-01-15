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
import de.derfrzocker.spigot.utils.language.Language;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

    private final static int PLUGIN_ID = 4244;

    private final Map<String, Integer> biomeNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> featureNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> placementModifierNamespace = new LinkedHashMap<>();
    private final Map<String, Integer> featureGeneratorNamespace = new LinkedHashMap<>();

    private final Map<String, Integer> languages = new LinkedHashMap<>();
    private AtomicInteger usefulLinksClickCount = new AtomicInteger(0);
    private final File languageFile;

    public Stats(JavaPlugin plugin, OreControlRegistries registries) {
        Metrics metrics = new Metrics(plugin, PLUGIN_ID);
        metrics.addCustomChart(new AdvancedPie("biome_namespace", () -> biomeNamespace));
        metrics.addCustomChart(new AdvancedPie("feature_namespace", () -> featureNamespace));
        metrics.addCustomChart(new AdvancedPie("placement_modifier_namespace", () -> placementModifierNamespace));
        metrics.addCustomChart(new AdvancedPie("feature_generator_namespace", () -> featureGeneratorNamespace));
        metrics.addCustomChart(new AdvancedPie("language", () -> languages));
        metrics.addCustomChart(new SingleLineChart("useful_links_click_count", () -> usefulLinksClickCount.getAndSet(0)));

        registries.getBiomeRegistry().getValues().keySet().forEach(key -> biomeNamespace.put(key.getNamespace(), 1));
        registries.getFeatureRegistry().getValues().keySet().forEach(key -> featureNamespace.put(key.getNamespace(), 1));
        registries.getPlacementModifierRegistry().getValues().keySet().forEach(key -> placementModifierNamespace.put(key.getNamespace(), 1));
        registries.getFeatureGeneratorRegistry().getValues().keySet().forEach(key -> featureGeneratorNamespace.put(key.getNamespace(), 1));

        languageFile = new File(plugin.getDataFolder(), "/data/.language_stats.yml");
        languageFile.getParentFile().mkdirs();

        if (!languageFile.exists()) {
            try {
                languageFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileConfiguration languageConfig = new YamlConfiguration();
        try {
            languageConfig.load(languageFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Cannot load configuration from file " + languageFile, e);
        }

        languageConfig.getStringList("languages").forEach(language -> languages.put(language, 1));

        saveLanguageFile();
    }

    public void addLanguage(Language language) {
        if (languages.put(language.getName(), 1) == null) {
            saveLanguageFile();
        }
    }

    private void saveLanguageFile() {
        YamlConfiguration languageConfig = new YamlConfiguration();
        languageConfig.options().setHeader(List.of("Stats, for which language is used. Please do not modify. Thanks!", "See also: https://bstats.org/plugin/bukkit/Ore-Control/4244"));
        languageConfig.set("languages", new ArrayList<>(languages.keySet()));
        try {
            languageConfig.save(languageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void incrementUsefulLinksClicksCount() {
        usefulLinksClickCount.incrementAndGet();
    }
}
