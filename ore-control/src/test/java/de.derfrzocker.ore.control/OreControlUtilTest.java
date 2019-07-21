package de.derfrzocker.ore.control;

import com.google.common.collect.Sets;
import de.derfrzocker.ore.control.api.*;
import de.derfrzocker.ore.control.api.dao.WorldOreConfigDao;
import de.derfrzocker.ore.control.impl.BiomeOreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import de.derfrzocker.ore.control.impl.WorldOreConfigYamlImpl;
import de.derfrzocker.ore.control.utils.OreControlUtil;
import de.derfrzocker.ore.control.utils.TestUtil;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Version;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.ServicesManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
@RunWith(PowerMockRunner.class)
public class OreControlUtilTest {

    private static Settings settings;

    private final Random random = new Random(2315112346878245789L);

    @BeforeClass
    public static void setUp() throws IOException {
        final OreControl oreControl = mock(OreControl.class);

        OreControl.setInstance(oreControl);

        final Server server;

        // mock Server
        {
            server = mock(Server.class);
            when(server.getLogger()).thenReturn(mock(Logger.class));
            when(server.getName()).thenReturn("spigot");
            Bukkit.setServer(server);
        }

        // mock OreControlService
        {
            final OreControlService oreControlService = new OreControlServiceImpl(mock(NMSReplacer.class), mock(WorldOreConfigDao.class));

            final ServicesManager servicesManager = mock(ServicesManager.class);

            when(servicesManager.load(OreControlService.class)).thenReturn(oreControlService);

            when(server.getServicesManager()).thenReturn(servicesManager);

        }

        final URL url = OreControl.getInstance().getClass().getClassLoader().getResource("data/settings.yml");

        if (url == null)
            throw new NullPointerException();

        final URLConnection connection = url.openConnection();
        connection.setUseCaches(false);

        settings = new Settings(new Config(connection.getInputStream()));

        when(oreControl.getSettings()).thenReturn(settings);

        Version.setCurrentVersion(Version.v1_14_R1);
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
                assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, Biome.PLAINS), (int) iterator.next());

    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingDontExistsButNormalOreSettingAndNormalOreSettingsDontHaveTheSetting_Expect_ReturnDefaultOreSettingValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values())
            worldOreConfig.setOreSettings(new OreSettingsYamlImpl(ore));

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                for (Setting setting : ore.getSettings())
                    assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).get());

    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingAndNormalOreSettingExistButNormalOreSettingsDontHaveTheSetting_Expect_ReturnDefaultOreSettingValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));

        for (Ore ore : Ore.values())
            worldOreConfig.setOreSettings(new OreSettingsYamlImpl(ore));

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                for (Setting setting : ore.getSettings())
                    assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).get());

    }

    @Test
    public void GetBiomeAmount_When_BiomeOreSettingAndOreSettingsExistButTheOreSettingsDontHaveTheSettingButNormalOreSettingExistAndHaveIt_Expect_ReturnNormalOreSettingValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres()) {
                biomeOreSettings.setOreSettings(new OreSettingsYamlImpl(ore));
            }
        }

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            worldOreConfig.setOreSettings(oreSettings);
            for (Setting setting : ore.getSettings())
                oreSettings.setValue(setting, random.nextInt());

        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                for (Setting setting : ore.getSettings())
                    assertEquals(OreControlUtil.getAmount(ore, setting, worldOreConfig, biome), (int) worldOreConfig.getOreSettings(ore).get().getValue(setting).get());

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

    @Test
    public void SetBiomeAmount_When_TheOreSettingsTheBiomeOreSettingsExistButTheValueNot_Expect_SetANewValueToTheSameOreSettingInTheSameBiomeSetting() {
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
    public void SetBiomeAmount_When_BiomeDontHaveTheOre_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final Set<Ore> ores = Sets.newHashSet(Ore.values());

            ores.removeAll(Sets.newHashSet(biome.getOres()));

            for (Ore ore : ores)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.setAmount(ore, ore.getSettings()[0], worldOreConfig, random.nextInt(), biome));
        }
    }

    @Test
    public void SetBiomeAmount_When_OreDontHaveTheSetting_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            for (Ore ore : biome.getOres()) {
                final Set<Setting> settings = Sets.newHashSet(Setting.values());

                settings.removeAll(Sets.newHashSet(ore.getSettings()));

                for (Setting setting : settings)
                    assertThrows(IllegalArgumentException.class, () -> OreControlUtil.setAmount(ore, setting, worldOreConfig, random.nextInt(), biome));
            }
        }
    }

    //Test OreControlUtil#setAmount(Ore, Setting, WorldOreConfig, int, Biome) end

    //Test OreControlUtil#getDefault(Ore, Setting) begin

    @Test
    public void GetDefault_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.getDefault(null, Setting.VEIN_SIZE));
        assertThrows(NullPointerException.class, () -> OreControlUtil.getDefault(Ore.GOLD_BADLANDS, null));
    }

    @Test
    public void GetDefault_When_OreHaveTheSetting_Expect_ReturnDefaultValue() {
        for (Ore ore : Ore.values())
            for (Setting setting : ore.getSettings())
                assertEquals(OreControlUtil.getDefault(ore, setting), (int) OreControl.getInstance().getSettings().getDefaultSettings(ore).getValue(setting).get());
    }

    @Test
    public void GetDefault_When_OreDontHaveTheSetting_Expect_ThrowIllegalArgumentException() {
        for (Ore ore : Ore.values()) {
            final Set<Setting> settings = Sets.newHashSet(Setting.values());

            settings.removeAll(Sets.newHashSet(ore.getSettings()));

            for (Setting setting : settings)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.getDefault(ore, setting));
        }
    }

    //Test OreControlUtil#getDefault(Ore, Setting) end

    //Test OreControlUtil#isUnSafe(Setting, int) begin

    @Test
    public void IsUnSafe_When_SettingIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isUnSafe(null, 0));
    }

    @Test
    public void IsUnsafe_When_ValueIsHigherOrSameThenMinimumValue_Expect_ReturnFalse() {
        for (int i = 0; i < 10000; i++)
            for (Setting setting : Setting.values()) {
                assertFalse(OreControlUtil.isUnSafe(setting, setting.getMinimumValue() + random.nextInt(100000)));
            }
    }

    @Test
    public void IsUnsafe_When_ValueIsLowerThenMinimumValue_Expect_ReturnTrue() {
        for (int i = 0; i < 10000; i++)
            for (Setting setting : Setting.values())
                assertTrue(OreControlUtil.isUnSafe(setting, setting.getMinimumValue() - random.nextInt(100000)));
    }

    //Test OreControlUtil#isUnSafe(Setting, int) end

    //Test OreControlUtil#isOre(String) begin

    @Test
    public void IsOre_When_StringIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isOre(null));
    }

    @Test
    public void IsOre_When_OreIsUpperCaseAndRight_Expect_ReturnTrue() {
        for (Ore ore : Ore.values())
            assertTrue(OreControlUtil.isOre(ore.toString().toUpperCase()));
    }

    @Test
    public void IsOre_When_OreIsLowerCaseAndRight_Expect_ReturnTrue() {
        for (Ore ore : Ore.values())
            assertTrue(OreControlUtil.isOre(ore.toString().toLowerCase()));
    }

    @Test
    public void IsOre_When_OreIsMixedCaseAndRight_Expect_ReturnTrue() {
        for (Ore ore : Ore.values())
            assertTrue(OreControlUtil.isOre(TestUtil.toMixedCase(ore.toString())));
    }

    @Test
    public void IsOre_When_InputStringIsWrong_Expect_ReturnFalse() {
        for (int i = 0; i < 10000; i++)
            assertFalse(OreControlUtil.isOre(TestUtil.generateRandomString(i)));
    }

    //Test OreControlUtil#isOre(String) end

    //Test OreControlUtil#isBiome(String) begin

    @Test
    public void IsBiome_When_StringIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isBiome(null));
    }

    @Test
    public void IsBiome_When_OreIsUpperCaseAndRight_Expect_ReturnTrue() {
        for (Biome biome : Biome.values())
            assertTrue(OreControlUtil.isBiome(biome.toString().toUpperCase()));
    }

    @Test
    public void IsBiome_When_OreIsLowerCaseAndRight_Expect_ReturnTrue() {
        for (Biome biome : Biome.values())
            assertTrue(OreControlUtil.isBiome(biome.toString().toLowerCase()));
    }

    @Test
    public void IsBiome_When_OreIsMixedCaseAndRight_Expect_ReturnTrue() {
        for (Biome biome : Biome.values())
            assertTrue(OreControlUtil.isBiome(TestUtil.toMixedCase(biome.toString())));
    }

    @Test
    public void IsBiome_When_InputStringIsWrong_Expect_ReturnFalse() {
        for (int i = 0; i < 10000; i++)
            assertFalse(OreControlUtil.isBiome(TestUtil.generateRandomString(i)));
    }

    //Test OreControlUtil#isBiome(String) end

    //Test OreControlUtil#isSetting(String) begin

    @Test
    public void IsSetting_When_StringIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isSetting(null));
    }

    @Test
    public void IsSetting_When_OreIsUpperCaseAndRight_Expect_ReturnTrue() {
        for (Setting setting : Setting.values())
            assertTrue(OreControlUtil.isSetting(setting.toString().toUpperCase()));
    }

    @Test
    public void IsSetting_When_OreIsLowerCaseAndRight_Expect_ReturnTrue() {
        for (Setting setting : Setting.values())
            assertTrue(OreControlUtil.isSetting(setting.toString().toLowerCase()));
    }

    @Test
    public void IsSetting_When_OreIsMixedCaseAndRight_Expect_ReturnTrue() {
        for (Setting setting : Setting.values())
            assertTrue(OreControlUtil.isSetting(TestUtil.toMixedCase(setting.toString())));
    }

    @Test
    public void IsSetting_When_InputStringIsWrong_Expect_ReturnFalse() {
        for (int i = 0; i < 10000; i++)
            assertFalse(OreControlUtil.isSetting(TestUtil.generateRandomString(i)));
    }

    //Test OreControlUtil#isSetting(String) end

    //Test OreControlUtil#isActivated(Ore, WorldOreConfig) begin

    @Test
    public void IsActivated_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(null, mock(WorldOreConfig.class)));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, null));
    }

    @Test
    public void IsActivated_When_WorldOreConfigDontHaveOreSetting_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values())
            assertTrue(OreControlUtil.isActivated(ore, worldOreConfig));
    }

    @Test
    public void IsActivated_When_WorldOreConfigHaveOreSettingAndActivatedIsTrueInOreSettings_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(true);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Ore ore : Ore.values())
            assertTrue(OreControlUtil.isActivated(ore, worldOreConfig));
    }

    @Test
    public void IsActivated_When_WorldOreConfigHaveOreSettingAndActivatedIsFalseInOreSettings_Expect_ReturnFalse() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(false);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Ore ore : Ore.values())
            assertFalse(OreControlUtil.isActivated(ore, worldOreConfig));
    }

    @Test
    public void IsActivated_When_WorldOreConfigHaveOreSettingAndActivatedIsMixedInOreSettings_Expect_ReturnTheRightBoolean() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        boolean status = true;

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(status);
            booleanList.add(status);
            status = !status;
            worldOreConfig.setOreSettings(oreSettings);
        }

        final Iterator<Boolean> booleanIterator = booleanList.iterator();
        status = true;

        for (Ore ore : Ore.values()) {
            assertEquals(OreControlUtil.isActivated(ore, worldOreConfig), booleanIterator.next());
            assertEquals(OreControlUtil.isActivated(ore, worldOreConfig), status);
            status = !status;
        }
    }

    //Test OreControlUtil#isActivated(Ore, WorldOreConfig) end

    //Test OreControlUtil#isActivated(Ore, WorldOreConfig, Biome) begin

    @Test
    public void IsBiomeActivated_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(null, mock(WorldOreConfig.class), Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, null, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.isActivated(Ore.GOLD, mock(WorldOreConfig.class), null));
    }

    @Test
    public void IsBiomeActivated_When_WorldOreConfigDontHaveBiomeOreSettingsOrNormalOreSettings_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertTrue(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_BiomeOreSettingsExistsButNoOreSettings_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertTrue(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_BiomeOreSettingsAndOreSettingsExistsAndIsActivated_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettings.setActivated(true);
                biomeOreSettings.setOreSettings(oreSettings);
            }
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertTrue(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_BiomeOreSettingsAndOreSettingsExistsAndIsNotActivated_Expect_ReturnFalse() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettings.setActivated(false);
                biomeOreSettings.setOreSettings(oreSettings);
            }
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertFalse(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_BiomeOreSettingsAndOreSettingsExistsAndIsMixedActivated_Expect_ReturnTheRightBoolean() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        boolean status = true;

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettings.setActivated(status);
                booleanList.add(status);
                status = !status;
                biomeOreSettings.setOreSettings(oreSettings);
            }
        }

        final Iterator<Boolean> booleanIterator = booleanList.iterator();
        status = true;

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                assertEquals(OreControlUtil.isActivated(ore, worldOreConfig, biome), booleanIterator.next());
                assertEquals(OreControlUtil.isActivated(ore, worldOreConfig, biome), status);
                status = !status;
            }

    }

    @Test
    public void IsBiomeActivated_When_OnlyNormalOreSettingsExistsAndIsActivated_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(true);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertTrue(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_OnlyNormalOreSettingsExistsAndIsNotActivated_Expect_ReturnFalse() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(false);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertFalse(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_OnlyNormalOreSettingsExistsAndIsMixedActivated_Expect_ReturnTheRightValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final Map<Ore, Boolean> booleanMap = new HashMap<>();
        boolean status = true;

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(status);
            booleanMap.put(ore, status);
            status = !status;
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertEquals(OreControlUtil.isActivated(ore, worldOreConfig, biome), booleanMap.get(ore));
    }

    @Test
    public void IsBiomeActivated_When_OnlyBiomeOreSettingsAndNormalOreSettingsExistsAndIsActivated_Expect_ReturnTrue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(true);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertTrue(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_OnlyBiomeOreSettingsAndNormalOreSettingsExistsAndIsNotActivated_Expect_ReturnFalse() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(false);
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertFalse(OreControlUtil.isActivated(ore, worldOreConfig, biome));
    }

    @Test
    public void IsBiomeActivated_When_OnlyBiomeOreSettingsAndNormalOreSettingsExistsAndIsMixedActivated_Expect_ReturnTheRightValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final Map<Ore, Boolean> booleanMap = new HashMap<>();
        boolean status = true;

        for (Biome biome : Biome.values())
            worldOreConfig.setBiomeOreSettings(new BiomeOreSettingsYamlImpl(biome));

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(status);
            booleanMap.put(ore, status);
            status = !status;
            worldOreConfig.setOreSettings(oreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres())
                assertEquals(OreControlUtil.isActivated(ore, worldOreConfig, biome), booleanMap.get(ore));
    }

    @Test
    public void IsBiomeActivated_When_BiomeDontHaveTheOre_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final Set<Ore> ores = Sets.newHashSet(Ore.values());

            ores.removeAll(Sets.newHashSet(biome.getOres()));

            for (Ore ore : ores)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.isActivated(ore, worldOreConfig, biome));
        }
    }

    //Test OreControlUtil#isActivated(Ore, WorldOreConfig, Biome) end

    //Test OreControlUtil#setActivated(Ore, WorldOreConfig, boolean) begin

    @Test
    public void SetActivated_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(null, mock(WorldOreConfig.class), true));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, null, true));
    }

    @Test
    public void SetActivated_When_OreSettingDontExists_Expect_CreateNewOneAndSetValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        boolean status = true;

        for (Ore ore : Ore.values()) {
            OreControlUtil.setActivated(ore, worldOreConfig, status);
            booleanList.add(status);
            status = !status;
        }
        final Iterator<Boolean> booleanIterator = booleanList.iterator();

        for (Ore ore : Ore.values()) {
            final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);
            assertTrue(oreSettings.isPresent());

            assertEquals(oreSettings.get().isActivated(), booleanIterator.next());
        }
    }

    @Test
    public void SetActivated_When_OreSettingExists_Expect_OverrideExistingValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();
        boolean status = true;

        for (Ore ore : Ore.values()) {
            final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
            oreSettings.setActivated(status);
            status = !status;
            oreSettingsList.add(oreSettings);
            worldOreConfig.setOreSettings(oreSettings);
        }

        status = false;

        for (Ore ore : Ore.values()) {
            OreControlUtil.setActivated(ore, worldOreConfig, status);
            booleanList.add(status);
            status = !status;
        }

        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();
        final Iterator<Boolean> booleanIterator = booleanList.iterator();

        for (Ore ore : Ore.values()) {
            final Optional<OreSettings> oreSettings = worldOreConfig.getOreSettings(ore);
            assertTrue(oreSettings.isPresent());

            assertSame(oreSettings.get(), oreSettingsIterator.next());

            assertEquals(oreSettings.get().isActivated(), booleanIterator.next());
        }
    }

    //Test OreControlUtil#setActivated(Ore, WorldOreConfig, boolean) end

    //Test OreControlUtil#setActivated(Ore, WorldOreConfig, boolean, Biome) begin

    @Test
    public void SetBiomeActivated_When_OneArgumentIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(null, mock(WorldOreConfig.class), true, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, null, true, Biome.BADLANDS));
        assertThrows(NullPointerException.class, () -> OreControlUtil.setActivated(Ore.GOLD, mock(WorldOreConfig.class), true, null));
    }

    @Test
    public void SetBiomeActivated_When_BiomeOreSettingDontExists_Expect_CreateNewOneAndSetValue() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        boolean status = true;

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                OreControlUtil.setActivated(ore, worldOreConfig, status, biome);
                booleanList.add(status);
                status = !status;
            }

        final Iterator<Boolean> booleanIterator = booleanList.iterator();

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
                assertTrue(biomeOreSettings.isPresent());

                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());

                assertEquals(oreSettings.get().isActivated(), booleanIterator.next());
            }
    }

    @Test
    public void SetBiomeActivated_When_OnlyBiomeOreSettingExists_Expect_CreateNewOreSettingsInSameBiomeOreSettings() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();
        boolean status = true;

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            biomeOreSettingsList.add(biomeOreSettings);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
        }

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                OreControlUtil.setActivated(ore, worldOreConfig, status, biome);
                booleanList.add(status);
                status = !status;
            }

        final Iterator<BiomeOreSettings> biomeOreSettingsIterator = biomeOreSettingsList.iterator();
        final Iterator<Boolean> booleanIterator = booleanList.iterator();

        for (Biome biome : Biome.values()) {
            final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
            assertTrue(biomeOreSettings.isPresent());
            assertSame(biomeOreSettings.get(), biomeOreSettingsIterator.next());

            for (Ore ore : biome.getOres()) {
                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());

                assertEquals(oreSettings.get().isActivated(), booleanIterator.next());
            }
        }
    }

    @Test
    public void SetBiomeActivated_When_BiomeAndOreSettingsExists_Expect_OverrideCurrentStatusInSameObject() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);
        final List<Boolean> booleanList = new LinkedList<>();
        final List<BiomeOreSettings> biomeOreSettingsList = new LinkedList<>();
        final List<OreSettings> oreSettingsList = new LinkedList<>();
        boolean status = true;

        for (Biome biome : Biome.values()) {
            final BiomeOreSettings biomeOreSettings = new BiomeOreSettingsYamlImpl(biome);
            biomeOreSettingsList.add(biomeOreSettings);
            worldOreConfig.setBiomeOreSettings(biomeOreSettings);
            for (Ore ore : biome.getOres()) {
                final OreSettings oreSettings = new OreSettingsYamlImpl(ore);
                oreSettings.setActivated(status);
                status = !status;
                oreSettingsList.add(oreSettings);
                biomeOreSettings.setOreSettings(oreSettings);
            }
        }

        status = false;

        for (Biome biome : Biome.values())
            for (Ore ore : biome.getOres()) {
                OreControlUtil.setActivated(ore, worldOreConfig, status, biome);
                booleanList.add(status);
                status = !status;
            }

        final Iterator<BiomeOreSettings> biomeOreSettingsIterator = biomeOreSettingsList.iterator();
        final Iterator<OreSettings> oreSettingsIterator = oreSettingsList.iterator();
        final Iterator<Boolean> booleanIterator = booleanList.iterator();

        for (Biome biome : Biome.values()) {
            final Optional<BiomeOreSettings> biomeOreSettings = worldOreConfig.getBiomeOreSettings(biome);
            assertTrue(biomeOreSettings.isPresent());
            assertSame(biomeOreSettings.get(), biomeOreSettingsIterator.next());

            for (Ore ore : biome.getOres()) {
                final Optional<OreSettings> oreSettings = biomeOreSettings.get().getOreSettings(ore);
                assertTrue(oreSettings.isPresent());
                assertSame(oreSettings.get(), oreSettingsIterator.next());

                assertEquals(oreSettings.get().isActivated(), booleanIterator.next());
            }
        }
    }

    @Test
    public void SetBiomeActivated_When_BiomeDontHaveTheOre_Expect_ThrowIllegalArgumentException() {
        final WorldOreConfig worldOreConfig = new WorldOreConfigYamlImpl("dummy", false);

        for (Biome biome : Biome.values()) {
            final Set<Ore> ores = Sets.newHashSet(Ore.values());

            ores.removeAll(Sets.newHashSet(biome.getOres()));

            for (Ore ore : ores)
                assertThrows(IllegalArgumentException.class, () -> OreControlUtil.setActivated(ore, worldOreConfig, true, biome));
        }
    }

    //Test OreControlUtil#setActivated(Ore, WorldOreConfig, boolean, Biome) end

}
