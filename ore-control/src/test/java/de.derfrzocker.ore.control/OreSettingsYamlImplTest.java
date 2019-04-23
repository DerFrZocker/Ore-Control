package de.derfrzocker.ore.control;

import de.derfrzocker.ore.control.api.Ore;
import de.derfrzocker.ore.control.api.Setting;
import de.derfrzocker.ore.control.impl.OreSettingsYamlImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class OreSettingsYamlImplTest {

    @Test
    public void When_OreSettingsIsInitialedWithANullValue_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new OreSettingsYamlImpl(null));
        assertThrows(NullPointerException.class, () -> new OreSettingsYamlImpl(null, new HashMap<>()));
        assertThrows(NullPointerException.class, () -> new OreSettingsYamlImpl(Ore.EMERALD, null));
    }

    //Test OreSettingsYamlImpl#getOre() begin

    @Test
    public void GetOre_When_OreSettingsIsInitialedWithAnOre_Expect_ReturnTheOreThatWasInitialed() { //Todo rename the method, currently i found no better name
        final List<OreSettingsYamlImpl> oreSettingsYamls = new LinkedList<>();

        for (Ore ore : Ore.values())
            oreSettingsYamls.add(new OreSettingsYamlImpl(ore));

        final Iterator<OreSettingsYamlImpl> oreSettingsIterator = oreSettingsYamls.iterator();

        for (Ore ore : Ore.values())
            assertSame(ore, oreSettingsIterator.next().getOre());
    }

    @Test
    public void GetOre_When_OreSettingsIsInitialedWithAnOreAndMap_Expect_ReturnTheOreThatWasInitialed() { //Todo rename the method
        for (Ore ore : Ore.values())
            assertSame(ore, new OreSettingsYamlImpl(ore).getOre());

        for (Ore ore : Ore.values())
            assertSame(ore, new OreSettingsYamlImpl(ore, new HashMap<>()).getOre());
    }

    //Test OreSettingsYamlImpl#getOre() end

    //Test OreSettingsYamlImpl#getValue(Setting) begin

    @Test
    public void GetValue_When_SettingIsNull_Expect_ThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new OreSettingsYamlImpl(Ore.EMERALD).getValue(null));
    }

    @Test
    public void GetValue_When_SettingIsNotPresent_Expect_ReturnEmptyOptional() {
        for (Ore ore : Ore.values()) {
            final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(ore);
            for (Setting setting : Setting.values())
                assertFalse(oreSettingsYaml.getValue(setting).isPresent());
        }

        for (Ore ore : Ore.values()) {
            final OreSettingsYamlImpl oreSettingsYaml = new OreSettingsYamlImpl(ore, new HashMap<>());
            for (Setting setting : Setting.values())
                assertFalse(oreSettingsYaml.getValue(setting).isPresent());
        }
    }

    //Test OreSettingsYamlImpl#getValue(Setting) end

}
