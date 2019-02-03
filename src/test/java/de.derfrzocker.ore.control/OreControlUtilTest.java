package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Biome;
import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.api.WorldOreConfig;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.utils.Config;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class OreControlUtilTest {

    private static Settings settings;

    private static OreControl oreControl;

    @BeforeClass
    public static void setUp() throws IOException {

        oreControl = mock(OreControl.class);

        OreControl.setInstance(oreControl);

        URL url = OreControl.getInstance().getClass().getClassLoader().getResource("data/settings.yml");

        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        settings = new Settings(new Config(connection.getInputStream()));

        when(oreControl.getSettings()).thenReturn(settings);
    }

    @Test
    public void testGetAmount() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, null, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, null));

        final int amount = 10;

        WorldOreConfigYamlImpl worldOreConfig = new WorldOreConfigYamlImpl("dummy_world");

        OreSettingsYamlImpl oreSettings = new OreSettingsYamlImpl(Ore.GOLD);
        oreSettings.setValue(Setting.VEIN_SIZE, amount);

        worldOreConfig.setOreSettings(oreSettings);

        // get a value from an existing OreSetting
        assertEquals(OreControlUtil.getAmount(Ore.GOLD, Setting.VEIN_SIZE, worldOreConfig), amount);

        // get a value from a none existing OreSettings -> should return the default vault from the settings
        assertEquals(OreControlUtil.getAmount(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, worldOreConfig), (int) settings.getDefaultSettings(Ore.GOLD_BADLANDS).getValue(Setting.VEIN_SIZE).get());

        assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getAmount(Ore.GOLD, Setting.VEINS_PER_CHUNK, worldOreConfig));
    }

    @Test
    public void testSetAmount() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), 0));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, 0));

        final int amount = 10;

        WorldOreConfigYamlImpl worldOreConfig = new WorldOreConfigYamlImpl("dummy_world");

        assertFalse(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());

        // set a new the value
        OreControlUtil.setAmount(Ore.GOLD, Setting.VEIN_SIZE, worldOreConfig, amount);

        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD).isPresent());
        assertEquals((int) worldOreConfig.getOreSettings(Ore.GOLD).get().getValue(Setting.VEIN_SIZE).get(), amount);

        ////////////////////////////////////////////////////////////////////////////

        OreSettingsYamlImpl oreSettings = new OreSettingsYamlImpl(Ore.GOLD_BADLANDS);
        oreSettings.setValue(Setting.VEIN_SIZE, amount);

        worldOreConfig.setOreSettings(oreSettings);

        final int newamount = 20;

        assertEquals((int) worldOreConfig.getOreSettings(Ore.GOLD_BADLANDS).get().getValue(Setting.VEIN_SIZE).get(), amount);
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), amount);

        // override an existing value
        OreControlUtil.setAmount(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, worldOreConfig, newamount);

        assertEquals((int) worldOreConfig.getOreSettings(Ore.GOLD_BADLANDS).get().getValue(Setting.VEIN_SIZE).get(), newamount);
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), newamount);
    }

    @Test
    public void testGetDefault() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.getDefault(null, Setting.VEIN_SIZE));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getDefault(Ore.GOLD_BADLANDS, null));

        Stream.of(Ore.values()).forEach(ore -> {
            List<Setting> list = Arrays.asList(ore.getSettings());
            Stream.of(Setting.values()).forEach(setting -> {
                // if the ore settings contains the setting it should return the vault from the settings.yml else it should throw an illegalArgumentException
                if (list.contains(setting))
                    assertEquals(OreControlUtil.getDefault(ore, setting), (int) settings.getDefaultSettings(ore).getValue(setting).get());
                else
                    assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getDefault(ore, setting));
            });
        });
    }

    @Test
    public void testGetBiomeAmount() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, mock(WorldOreConfig.class), null));

        final int amount = 10;

        WorldOreConfigYamlImpl worldOreConfig = new WorldOreConfigYamlImpl("dummy_world");

        BiomeOreSettingsYamlImpl biomeOreSettings = new BiomeOreSettingsYamlImpl(Biome.BADLANDS);

        OreSettingsYamlImpl oreSettings = new OreSettingsYamlImpl(Ore.GOLD);
        oreSettings.setValue(Setting.VEIN_SIZE, amount);

        biomeOreSettings.setOreSettings(oreSettings);

        worldOreConfig.setBiomeOreSettings(biomeOreSettings);

        // get a value from an existing BiomeOreSetting
        assertEquals(OreControlUtil.getAmount(Ore.GOLD, Setting.VEIN_SIZE, worldOreConfig, Biome.BADLANDS), amount);

        // get a value from a none existing BiomeOreSettings -> should return the default vault from the settings
        assertEquals(OreControlUtil.getAmount(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, worldOreConfig, Biome.BADLANDS_PLATEAU), (int) settings.getDefaultSettings(Ore.GOLD_BADLANDS).getValue(Setting.VEIN_SIZE).get());
    }

    @Test
    public void testSetBiomeAmount() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, null));

        final int amount = 10;

        WorldOreConfigYamlImpl worldOreConfig = new WorldOreConfigYamlImpl("dummy_world");

        assertFalse(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).isPresent());

        // set a new the value
        OreControlUtil.setAmount(Ore.GOLD, Setting.VEIN_SIZE, worldOreConfig, amount, Biome.BADLANDS);

        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).isPresent());
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD).isPresent());
        assertEquals((int) worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD).get().getValue(Setting.VEIN_SIZE).get(), amount);

        ////////////////////////////////////////////////////////////////////////////

        OreSettingsYamlImpl oreSettings = new OreSettingsYamlImpl(Ore.GOLD_BADLANDS);
        oreSettings.setValue(Setting.VEIN_SIZE, amount);

        BiomeOreSettingsYamlImpl biomeOreSettings = new BiomeOreSettingsYamlImpl(Biome.BADLANDS);

        biomeOreSettings.setOreSettings(oreSettings);

        worldOreConfig.setBiomeOreSettings(biomeOreSettings);

        final int newamount = 20;

        assertEquals((int) worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD_BADLANDS).get().getValue(Setting.VEIN_SIZE).get(), amount);
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), amount);

        // override an existing value
        OreControlUtil.setAmount(Ore.GOLD_BADLANDS, Setting.VEIN_SIZE, worldOreConfig, newamount, Biome.BADLANDS);

        assertEquals((int) worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD_BADLANDS).get().getValue(Setting.VEIN_SIZE).get(), newamount);
        assertEquals((int) oreSettings.getValue(Setting.VEIN_SIZE).get(), newamount);
    }

    @Test
    public void testIsBiome() {
        Stream.of(Biome.values()).map(Enum::toString).forEach(value -> assertTrue(OreControlUtil.isBiome(value)));
        Stream.of(Biome.values()).map(Enum::toString).map(String::toLowerCase).forEach(value -> assertTrue(OreControlUtil.isBiome(value)));

        assertFalse(OreControlUtil.isBiome("random_string"));
        assertFalse(OreControlUtil.isBiome("another_random_string"));
    }

    @Test
    public void testIsOre() {
        Stream.of(Ore.values()).map(Enum::toString).forEach(value -> assertTrue(OreControlUtil.isOre(value)));
        Stream.of(Ore.values()).map(Enum::toString).map(String::toLowerCase).forEach(value -> assertTrue(OreControlUtil.isOre(value)));

        assertFalse(OreControlUtil.isOre("random_string"));
        assertFalse(OreControlUtil.isOre("another_random_string"));
    }

    @Test
    public void testIsSetting() {
        Stream.of(Setting.values()).map(Enum::toString).forEach(value -> assertTrue(OreControlUtil.isSetting(value)));
        Stream.of(Setting.values()).map(Enum::toString).map(String::toLowerCase).forEach(value -> assertTrue(OreControlUtil.isSetting(value)));

        assertFalse(OreControlUtil.isSetting("random_string"));
        assertFalse(OreControlUtil.isSetting("another_random_string"));
    }

}
