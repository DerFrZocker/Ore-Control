package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("ConstantConditions")
@RunWith(PowerMockRunner.class) //TODO rework the test cases, add assertNotSame to test cases
public class DeserializeTest {

    @Test
    public void testOreSettings() {
        Map<String, Object> map = new HashMap<>();
        OreSettingsYamlImpl oreSettings;

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreSettingsYamlImpl.deserialize(null));
        assertThrows(NullPointerException.class, () -> OreSettingsYamlImpl.deserialize(map));

        ///////////////////////////////////////////////////

        map.put("ore", Ore.EMERALD.toString());

        oreSettings = OreSettingsYamlImpl.deserialize(map);

        assertEquals(oreSettings.getOre(), Ore.EMERALD);
        assertEquals(oreSettings.getSettings().size(), 0);

        ///////////////////////////////////////////////////

        map.put("random_string", "random_string");

        oreSettings = OreSettingsYamlImpl.deserialize(map);

        assertEquals(oreSettings.getOre(), Ore.EMERALD);
        assertEquals(oreSettings.getSettings().size(), 0);

        ///////////////////////////////////////////////////

        final int value = 10;

        map.put(Setting.VEIN_SIZE.toString(), value);

        oreSettings = OreSettingsYamlImpl.deserialize(map);

        assertEquals(oreSettings.getOre(), Ore.EMERALD);
        assertEquals(oreSettings.getSettings().size(), 1);
        assertTrue(oreSettings.getValue(Setting.VEIN_SIZE).isPresent());
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), value);

        ///////////////////////////////////////////////////

        final int value2 = 20;
        final int value3 = 30;

        map.put(Setting.VEINS_PER_CHUNK.toString(), value2);
        map.put(Setting.HEIGHT_RANGE.toString(), value3);

        oreSettings = OreSettingsYamlImpl.deserialize(map);

        assertEquals(oreSettings.getOre(), Ore.EMERALD);
        assertEquals(oreSettings.getSettings().size(), 3);
        assertTrue(oreSettings.getValue(Setting.VEIN_SIZE).isPresent());
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), value);
        assertTrue(oreSettings.getValue(Setting.VEINS_PER_CHUNK).isPresent());
        assertEquals((int) oreSettings.getValue(Setting.VEINS_PER_CHUNK).get(), value2);
        assertTrue(oreSettings.getValue(Setting.HEIGHT_RANGE).isPresent());
        assertEquals((int) oreSettings.getValue(Setting.HEIGHT_RANGE).get(), value3);
    }

    @Test
    public void testBiomeOreSettings() {
        Map<String, Object> map = new HashMap<>();
        BiomeOreSettingsYamlImpl biomeOreSettings;

        //NullPointers
        assertThrows(NullPointerException.class, () -> BiomeOreSettingsYamlImpl.deserialize(null));
        assertThrows(NullPointerException.class, () -> BiomeOreSettingsYamlImpl.deserialize(map));

        ///////////////////////////////////////////////////

        map.put("biome", Biome.BADLANDS.toString());

        biomeOreSettings = BiomeOreSettingsYamlImpl.deserialize(map);

        assertEquals(biomeOreSettings.getBiome(), Biome.BADLANDS);
        assertEquals(biomeOreSettings.getOreSettings().size(), 0);

        ///////////////////////////////////////////////////

        map.put("random_string", "random_string");

        biomeOreSettings = BiomeOreSettingsYamlImpl.deserialize(map);

        assertEquals(biomeOreSettings.getBiome(), Biome.BADLANDS);
        assertEquals(biomeOreSettings.getOreSettings().size(), 0);

        ///////////////////////////////////////////////////

        final OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        map.put(Ore.GOLD.toString(), oreSettings);

        biomeOreSettings = BiomeOreSettingsYamlImpl.deserialize(map);

        assertEquals(biomeOreSettings.getBiome(), Biome.BADLANDS);
        assertEquals(biomeOreSettings.getOreSettings().size(), 1);
        assertTrue(biomeOreSettings.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(biomeOreSettings.getOreSettings(Ore.GOLD).get(), oreSettings);

        ///////////////////////////////////////////////////

        final OreSettings oreSettings2 = new OreSettingsYamlImpl(Ore.DIAMOND);
        final OreSettings oreSettings3 = new OreSettingsYamlImpl(Ore.EMERALD);

        map.put(Ore.DIAMOND.toString(), oreSettings2);
        map.put(Ore.EMERALD.toString(), oreSettings3);

        biomeOreSettings = BiomeOreSettingsYamlImpl.deserialize(map);

        assertEquals(biomeOreSettings.getBiome(), Biome.BADLANDS);
        assertEquals(biomeOreSettings.getOreSettings().size(), 3);
        assertTrue(biomeOreSettings.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(biomeOreSettings.getOreSettings(Ore.GOLD).get(), oreSettings);
        assertTrue(biomeOreSettings.getOreSettings(Ore.DIAMOND).isPresent());
        assertEquals(biomeOreSettings.getOreSettings(Ore.DIAMOND).get(), oreSettings2);
        assertTrue(biomeOreSettings.getOreSettings(Ore.EMERALD).isPresent());
        assertEquals(biomeOreSettings.getOreSettings(Ore.EMERALD).get(), oreSettings3);
    }

    @Test
    public void testWorldOreConfig() {
        Map<String, Object> map = new HashMap<>();
        WorldOreConfigYamlImpl worldOreConfig;

        //NullPointers
        assertThrows(NullPointerException.class, () -> WorldOreConfigYamlImpl.deserialize(null));
        assertThrows(NullPointerException.class, () -> WorldOreConfigYamlImpl.deserialize(map));

        ///////////////////////////////////////////////////

        map.put("world", "dummy_world");

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 0);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 0);

        ///////////////////////////////////////////////////

        map.put("random_string", "random_string");

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 0);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 0);

        ///////////////////////////////////////////////////

        final OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        map.put(Ore.GOLD.toString(), oreSettings);

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 1);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 0);
        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.GOLD).get(), oreSettings);

        ///////////////////////////////////////////////////

        final OreSettings oreSettings2 = new OreSettingsYamlImpl(Ore.DIAMOND);
        final OreSettings oreSettings3 = new OreSettingsYamlImpl(Ore.EMERALD);

        map.put(Ore.DIAMOND.toString(), oreSettings2);
        map.put(Ore.EMERALD.toString(), oreSettings3);

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 3);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 0);
        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.GOLD).get(), oreSettings);
        assertTrue(worldOreConfig.getOreSettings(Ore.DIAMOND).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.DIAMOND).get(), oreSettings2);
        assertTrue(worldOreConfig.getOreSettings(Ore.EMERALD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.EMERALD).get(), oreSettings3);

        ///////////////////////////////////////////////////

        final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(Biome.BADLANDS);

        map.put(Biome.BADLANDS.toString(), biomeOreSettings);

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 3);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 1);
        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.GOLD).get(), oreSettings);
        assertTrue(worldOreConfig.getOreSettings(Ore.DIAMOND).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.DIAMOND).get(), oreSettings2);
        assertTrue(worldOreConfig.getOreSettings(Ore.EMERALD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.EMERALD).get(), oreSettings3);
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).isPresent());
        assertEquals(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get(), biomeOreSettings);

        ///////////////////////////////////////////////////

        final BiomeOreSettings biomeOreSettings2 = new BiomeOreSettingsYamlImpl(Biome.RIVER);
        final BiomeOreSettings biomeOreSettings3 = new BiomeOreSettingsYamlImpl(Biome.PLAINS);

        map.put(Biome.RIVER.toString(), biomeOreSettings2);
        map.put(Biome.PLAINS.toString(), biomeOreSettings3);

        worldOreConfig = WorldOreConfigYamlImpl.deserialize(map);

        assertEquals(worldOreConfig.getName(), "dummy_world");
        assertEquals(worldOreConfig.getOreSettings().size(), 3);
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), 3);
        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.GOLD).get(), oreSettings);
        assertTrue(worldOreConfig.getOreSettings(Ore.DIAMOND).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.DIAMOND).get(), oreSettings2);
        assertTrue(worldOreConfig.getOreSettings(Ore.EMERALD).isPresent());
        assertEquals(worldOreConfig.getOreSettings(Ore.EMERALD).get(), oreSettings3);
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).isPresent());
        assertEquals(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get(), biomeOreSettings);
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.RIVER).isPresent());
        assertEquals(worldOreConfig.getBiomeOreSettings(Biome.RIVER).get(), biomeOreSettings2);
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.PLAINS).isPresent());
        assertEquals(worldOreConfig.getBiomeOreSettings(Biome.PLAINS).get(), biomeOreSettings3);


    }

}
