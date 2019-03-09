package de.derfrzocker.ore.control;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
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
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@RunWith(PowerMockRunner.class) //TODO rework the test cases
public class OreControlUtilTest {

    private static Settings settings;

    private final Random random = new Random(2315112346878245789L);

    @BeforeClass
    public static void setUp() throws IOException {
        final OreControl oreControl = mock(OreControl.class);

        OreControl.setInstance(oreControl);

        final URL url = OreControl.getInstance().getClass().getClassLoader().getResource("data/settings.yml");

        if (url == null)
            throw new NullPointerException();

        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        settings = new Settings(new Config(connection.getInputStream()));

        when(oreControl.getSettings()).thenReturn(settings);
    }

    //Test OreControlUtil#getAmount(Ore, Setting, WorldOreConfig) begin

    @Test
    public void GetAmount_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, null, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, null));
    }

    @Test
    public void GetAmount_When_SettingValueDontExist_Expect_ReturnDefaultOne() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values())
            for (Setting setting : ore.getSettings())
                assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig), (int) settings.getDefaultSettings(ore).getValue(setting).get());
    }

    @Test
    public void GetAmount_When_SettingValueExist_Expect_ReturnTheExistingOne() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();

        // Go through all Ores and Settings and add a random number to the Setting
        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            for (Setting setting : ore.getSettings()) {
                final int amount = random.nextInt();
                list.add(amount);
                oreSettings.setValue(setting, amount);
            }
            worldOreConfig.setOreSettings(oreSettings);
        }

        final Iterator<Integer> iterator = list.iterator();

        // Go through all Ores and Settings again and check if the return value the right one
        for (Ore ore : Ore.values())
            for (Setting setting : ore.getSettings())
                assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig), (int) iterator.next());
    }

    @Test
    public void GetAmount_When_OreDontHaveTheSetting_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final Set<Setting> settings = Sets.newHashSet(Setting.values());

            settings.removeAll(Sets.newHashSet(ore.getSettings()));

            for (Setting setting : settings)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getAmount(ore, setting, worldOreConfig));
        }
    }

    @Test
    public void GetAmount_When_OreSettingsExistsButTheSettingNot_Expect_ReturnDefaultValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values())
            worldOreConfig.setOreSettings(new OreSettingsYamlImpl(ore));

        for (Ore ore : Ore.values())
            assertEquals(OreControlUtil.getAmount(ore, ore.getSettings()[1], worldOreConfig), (int) settings.getDefaultSettings(ore).getValue(ore.getSettings()[1]).get());

    }

    //Test OreControlUtil#getAmount(Ore, Setting, WorldOreConfig) end

    //Test OreControlUtil#getAmount(Ore, Setting, WorldOreConfig, Biome) begin

    @Test
    public void GetBiomeAmount_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getAmount(Ore.EMERALD, Setting.VEIN_SIZE, mock(WorldOreConfig.class), null));
    }

    @Test
    public void GetBiomeAmount_When_SettingValueDontExist_Expect_ReturnDefaultOne() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                for (Setting setting : ore.getSettings())
                    assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) settings.getDefaultSettings(ore).getValue(setting).get());

    }

    @Test
    public void GetBiomeAmount_When_SettingValueExist_Expect_ReturnTheExistingOne() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();

        // Go through all Biomes, Ores and Settings and add a random number to the Setting
        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSetting = new BiomeOreSettingsYamlImpl(biome);
            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                for (Setting setting : ore.getSettings()) {
                    final int amount = random.nextInt();
                    list.add(amount);
                    oreSettings.setValue(setting, amount);
                }
                biomeOreSetting.setOreSettings(oreSettings);
            }
            worldOreConfig.setBiomeOreSettings(biomeOreSetting);
        }

        final Iterator<Integer> iterator = list.iterator();

        // Go through all Biomes, Ores and Settings again and check if the return value the right one
        for (Biome biome : Biome.values()) {
            for (Ore ore : biome.getOres())
                for (Setting setting : ore.getSettings())
                    assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) iterator.next());
        }
    }

    @Test
    public void GetBiomeAmount_When_OreDontHaveTheSetting_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final Set<Setting> settings = Sets.newHashSet(Setting.values());

                settings.removeAll(Sets.newHashSet(ore.getSettings()));

                for (Setting setting : settings)
                    assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getAmount(ore, setting, worldOreConfig, biome));
            }
    }

    @Test
    public void GetBiomeAmount_When_BiomeDontHaveTheOre_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final Set<Ore> ores = Sets.newHashSet(Ore.values());

            ores.removeAll(Sets.newHashSet(biome.getOres()));

            for (Ore ore : ores)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getAmount(ore, ore.getSettings()[0], worldOreConfig, biome));
        }

    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingExistButNoOreSettingsNot_Expect_ReturnDefaultValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));


        for (Biome biome : Biome.values()) {
            final Ore ore = biome.getOres()[0];
            final Setting setting = ore.getSettings()[0];
            assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) settings.getDefaultSettings(ore).getValue(setting).get());
        }
    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingAndOreSettingsExistButTheOreSettingsDontHaveTheSetting_Expect_ReturnDefaultValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres())
                biomeOreSettings.setOreSettings(new OreSettingsYamlImpl(ore));
        }

        for (Biome biome : Biome.values()) {
            final Ore ore = biome.getOres()[0];
            final Setting setting = ore.getSettings()[0];
            assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) settings.getDefaultSettings(ore).getValue(setting).get());
        }
    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingDontExistsButNormalOreSetting_Expect_ReturnNormalOreSettingValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> integers = new LinkedList<>();

        for (Ore ore : Biome.PLAINS.getOres()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            worldOreConfig.setOreSettings(oreSettings);
            for (Setting setting : ore.getSettings()) {
                final int value = random.nextInt();
                integers.add(value);
                oreSettings.setValue(setting, value);
            }
        }

        final Iterator<Integer> iterator = integers.iterator();

        for (Ore ore : Biome.PLAINS.getOres())
            for (Setting setting : ore.getSettings())
                assertSame(OreControlUtil.getAmount(ore, setting, worldOreConfig, Biome.PLAINS), iterator.next());

    }

    //Test OreControlUtil#getAmount(Ore, Setting, WorldOreConfig, Biome) end

    //Test OreControlUtil#setAmount(Ore, Setting, WorldOreConfig, int) begin

    @Test
    public void SetAmount_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), 0));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, 0));
    }

    @Test
    public void SetAmount_When_TheOreSettingDontExist_Expect_CreateNewOreSettingAndSetValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();

        assertTrue(worldOreConfig.getOreSettings().isEmpty());

        for (Ore ore : Ore.values()) {
            final int value = random.nextInt();
            OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, value);
            list.add(value);
        }

        assertFalse(worldOreConfig.getOreSettings().isEmpty());
        assertEquals(worldOreConfig.getOreSettings().size(), Ore.values().length);

        final Iterator<Integer> iterator = list.iterator();

        for (Ore ore : Ore.values()) {
            final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);
            assertTrue(oreSettings.isPresent());

            final Optional<Integer> integer = oreSettings.get().getValue(ore.getSettings()[0]);
            assertTrue(integer.isPresent());

            assertEquals(integer.get(), iterator.next());
        }
    }

    @Test
    public void SetAmount_When_OreSettingsExistsButValueNot_Expect_AddValueToTheOreSettings() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            worldOreConfig.setOreSettings(oreSettings);
            oreSettingsList.add(oreSettings);
        }

        for (Ore ore : Ore.values())
            for (Setting setting : ore.getSettings()) {
                final int amount = random.nextInt();
                list.add(amount);
                OreControlUtil.setAmount(ore, setting, worldOreConfig, amount);
            }


        final Iterator<Integer> iterator = list.iterator();
        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();

        for (Ore ore : Ore.values()) {
            final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);
            assertTrue(oreSettings.isPresent());
            assertSame(oreSettings.get(), oreSettingsIterator.next());

            for (Setting setting : ore.getSettings()) {
                final Optional<Integer> integer = oreSettings.get().getValue(setting);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
        }
    }

    @Test
    public void SetAmount_When_OreSettingAndValueExists_Expect_ReplaceOldOneWithNewOne() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            for (Setting setting : ore.getSettings())
                oreSettings.setValue(setting, random.nextInt());
            worldOreConfig.setOreSettings(oreSettings);
            oreSettingsList.add(oreSettings);
        }

        for (Ore ore : Ore.values())
            for (Setting setting : ore.getSettings()) {
                final int amount = random.nextInt();
                list.add(amount);
                OreControlUtil.setAmount(ore, setting, worldOreConfig, amount);
            }


        final Iterator<Integer> iterator = list.iterator();
        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();

        for (Ore ore : Ore.values()) {
            final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);
            assertTrue(oreSettings.isPresent());
            assertSame(oreSettings.get(), oreSettingsIterator.next());

            for (Setting setting : ore.getSettings()) {
                final Optional<Integer> integer = oreSettings.get().getValue(setting);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
        }
    }

    @Test
    public void SetAmount_When_OreDontHaveTheSetting_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final Set<Setting> settings = Sets.newHashSet(Setting.values());

            settings.removeAll(Sets.newHashSet(ore.getSettings()));

            for (Setting setting : settings)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.setAmount(ore, setting, worldOreConfig, 314));
        }

    }

    //Test OreControlUtil#setAmount(Ore, Setting, WorldOreConfig, int) end

    //Test OreControlUtil#setAmount(Ore, Setting, WorldOreConfig, int, Biome) begin

    @Test
    public void SetBiomeAmount_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, null));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, 0, Biome.BADLANDS));
    }

    @Test
    public void SetBiomeAmount_When_TheOreSettingAndBiomeOreSettingDontExist_Expect_CreateNewSettingsAndSetValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();

        assertTrue(worldOreConfig.getBiomeOreSettings().isEmpty());

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final int value = random.nextInt();
                OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, value, biome);
                list.add(value);
            }

        assertFalse(worldOreConfig.getBiomeOreSettings().isEmpty());
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), Biome.values().length);

        final Iterator<Integer> iterator = list.iterator();

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
                assertTrue(biomeOreSettings.isPresent());

                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());

                final Optional<Integer> integer = oreSettings.get().getValue(ore.getSettings()[0]);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
    }

    @Test
    public void SetBiomeAmount_When_TheOreSettingDontExistButTheBiomeOreSetting_Expect_CreateNewOreSettingInTheExistingBiomeOreSettingAndSetValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();
        final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();

        assertTrue(worldOreConfig.getBiomeOreSettings().isEmpty());

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            biomeOreSettingsList.add(biomeOreSettings);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
        }

        assertFalse(worldOreConfig.getBiomeOreSettings().isEmpty());
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), Biome.values().length);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final int amount = random.nextInt();
                list.add(amount);
                OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, amount, biome);
            }

        final Iterator<Integer> iterator = list.iterator();
        final Iterator<BiomeOreSettings> biomeOreSettingsIterator = biomeOreSettingsList.iterator();

        for (Biome biome : Biome.values()) {
            final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
            assertTrue(biomeOreSettings.isPresent());
            assertSame(biomeOreSettings.get(), biomeOreSettingsIterator.next());

            for (Ore ore : biome.getOres()) {
                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());

                final Optional<Integer> integer = oreSettings.get().getValue(ore.getSettings()[0]);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
        }
    }

    @Test
    public void SetBiomeAmount_When_TheOreSettingsAndTheBiomeOreSettingsExists_Expect_SetValueToTheSameOreSettingInTheSameBiomeSetting() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();
        final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();

        assertTrue(worldOreConfig.getBiomeOreSettings().isEmpty());

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            biomeOreSettingsList.add(biomeOreSettings);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);

            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettingsList.add(oreSettings);
                biomeOreSettings.setOreSettings(oreSettings);
            }
        }

        assertFalse(worldOreConfig.getBiomeOreSettings().isEmpty());
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), Biome.values().length);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final int amount = random.nextInt();
                OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, amount, biome);
                list.add(amount);
            }

        final Iterator<Integer> iterator = list.iterator();
        final Iterator<BiomeOreSettings> biomeOreSettingsIterator = biomeOreSettingsList.iterator();
        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();

        for (Biome biome : Biome.values()) {
            final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
            assertTrue(biomeOreSettings.isPresent());
            assertSame(biomeOreSettings.get(), biomeOreSettingsIterator.next());

            for (Ore ore : biome.getOres()) {
                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());
                assertSame(oreSettings.get(), oreSettingsIterator.next());

                final Optional<Integer> integer = oreSettings.get().getValue(ore.getSettings()[0]);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
        }
    }

    @Test
    public void SetBiomeAmount_When_TheOreSettingsTheBiomeOreSettingsAndTheValueExists_Expect_ReplaceValueToTheSameOreSettingInTheSameBiomeSetting() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Integer> list = new LinkedList<>();
        final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();

        assertTrue(worldOreConfig.getBiomeOreSettings().isEmpty());

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            biomeOreSettingsList.add(biomeOreSettings);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);

            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettingsList.add(oreSettings);
                biomeOreSettings.setOreSettings(oreSettings);
                oreSettings.setValue(ore.getSettings()[0], random.nextInt());
            }
        }

        assertFalse(worldOreConfig.getBiomeOreSettings().isEmpty());
        assertEquals(worldOreConfig.getBiomeOreSettings().size(), Biome.values().length);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final int amount = random.nextInt();
                OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, amount, biome);
                list.add(amount);
            }

        final Iterator<Integer> iterator = list.iterator();
        final Iterator<BiomeOreSettings> biomeOreSettingsIterator = biomeOreSettingsList.iterator();
        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();

        for (Biome biome : Biome.values()) {
            final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
            assertTrue(biomeOreSettings.isPresent());
            assertSame(biomeOreSettings.get(), biomeOreSettingsIterator.next());

            for (Ore ore : biome.getOres()) {
                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());
                assertSame(oreSettings.get(), oreSettingsIterator.next());

                final Optional<Integer> integer = oreSettings.get().getValue(ore.getSettings()[0]);
                assertTrue(integer.isPresent());

                assertEquals(integer.get(), iterator.next());
            }
        }
    }


    //Test OreControlUtil#setAmount(Ore, Setting, WorldOreConfig, int, Biome) end

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
    public void testSetBiomeAmount() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(null, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, null, mock(WorldOreConfig.class), 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, null, 0, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setAmount(Ore.EMERALD, Setting.VEIN_SIZE, mock(WorldOreConfig.class), 0, null));

        final int amount = 10;

        WorldOreConfigYamlImpl worldOreConfig = new WorldOreConfigYamlImpl("dummy_world", false);

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

    @Test
    public void testIsActivated() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(null, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, null));

        WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy_world", false);

        assertTrue(OreControlUtil.isActivated(Ore.GOLD_BADLANDS, worldOreConfig));

        OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        worldOreConfig.setOreSettings(oreSettings);

        assertTrue(OreControlUtil.isActivated(Ore.GOLD, worldOreConfig));

        oreSettings.setActivated(false);

        assertFalse(OreControlUtil.isActivated(Ore.GOLD, worldOreConfig));
    }

    @Test
    public void testIsBiomeActivated() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(null, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, null, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, mock(WorldOreConfig.class), null));

        WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy_world", false);

        assertTrue(OreControlUtil.isActivated(Ore.GOLD_BADLANDS, worldOreConfig, Biome.BADLANDS));

        BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(Biome.BADLANDS);

        OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        biomeOreSettings.setOreSettings(oreSettings);
        worldOreConfig.setBiomeOreSettings(biomeOreSettings);

        assertTrue(OreControlUtil.isActivated(Ore.GOLD, worldOreConfig, Biome.BADLANDS));

        oreSettings.setActivated(false);

        assertFalse(OreControlUtil.isActivated(Ore.GOLD, worldOreConfig, Biome.BADLANDS));
    }

    @Test
    public void testSetActivated() {

        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(null, mock(WorldOreConfig.class), false));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, null, false));

        WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy_world", false);

        OreControlUtil.setActivated(Ore.GOLD_BADLANDS, worldOreConfig, false);

        assertTrue(worldOreConfig.getOreSettings(Ore.GOLD_BADLANDS).isPresent());
        assertFalse(worldOreConfig.getOreSettings(Ore.GOLD_BADLANDS).get().isActivated());

        OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        worldOreConfig.setOreSettings(oreSettings);

        assertTrue(oreSettings.isActivated());

        OreControlUtil.setActivated(Ore.GOLD, worldOreConfig, false);

        assertFalse(oreSettings.isActivated());
    }

    @Test
    public void testSetBiomeActivated() {
        //NullPointers
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(null, mock(WorldOreConfig.class), false, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, null, false, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, mock(WorldOreConfig.class), false, null));

        WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy_world", false);

        OreControlUtil.setActivated(Ore.GOLD_BADLANDS, worldOreConfig, false, Biome.BADLANDS);

        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).isPresent());
        assertTrue(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD_BADLANDS).isPresent());
        assertFalse(worldOreConfig.getBiomeOreSettings(Biome.BADLANDS).get().getOreSettings(Ore.GOLD_BADLANDS).get().isActivated());

        BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(Biome.BADLANDS);

        OreSettings oreSettings = new OreSettingsYamlImpl(Ore.GOLD);

        biomeOreSettings.setOreSettings(oreSettings);
        worldOreConfig.setBiomeOreSettings(biomeOreSettings);

        assertTrue(oreSettings.isActivated());

        OreControlUtil.setActivated(Ore.GOLD, worldOreConfig, false, Biome.BADLANDS);

        assertFalse(oreSettings.isActivated());
    }

}
