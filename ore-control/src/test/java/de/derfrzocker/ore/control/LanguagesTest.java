package de.derfrzocker.ore.control;

import org.bukkit.configuration.file.YamlConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LanguagesTest {

    private final static File LANG_DIRECTORY = new File(Thread.currentThread().getContextClassLoader().getResource("lang").getFile());

    private final static String MAIN_LANGUAGE = "en";
    private final static Map<String, Map<String, MappingNode>> LANGUAGES = new HashMap<>();

    @BeforeAll
    public static void loadLangFiles() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        for (File directory : LANG_DIRECTORY.listFiles()) {
            if (!directory.isDirectory()) {
                continue;
            }

            Map<String, MappingNode> map = new HashMap<>();
            for (File file : directory.listFiles()) {
                map.put(file.getName(), (MappingNode) yaml.compose(new FileReader(file)));
            }

            LANGUAGES.put(directory.getName(), map);
        }
    }

    @Test
    public void testLanguagePresent() {
        assertEquals(2, LANGUAGES.size());

        assertTrue(LANGUAGES.containsKey("en"), "No English language found");
        assertTrue(LANGUAGES.containsKey("de"), "No German language found");
    }

    @Test
    public void testAllFilesPresent() {
        Map<String, MappingNode> enNodes = LANGUAGES.get(MAIN_LANGUAGE);

        for (Map.Entry<String, Map<String, MappingNode>> toTest : LANGUAGES.entrySet()) {
            for (String shouldHave : enNodes.keySet()) {
                assertTrue(toTest.getValue().containsKey(shouldHave), "Language " + toTest.getKey() + " does not have language file " + shouldHave);
            }

            assertEquals(enNodes.size(), toTest.getValue().size(), "Unexpected different in file amount for " + toTest.getKey());
        }
    }

    @Test
    public void testAllKeysPresent() {
        Map<String, MappingNode> enNodes = LANGUAGES.get(MAIN_LANGUAGE);

        for (Map.Entry<String, Map<String, MappingNode>> toTest : LANGUAGES.entrySet()) {
            for (Map.Entry<String, MappingNode> referenceEntry : enNodes.entrySet()) {
                MappingNode reference = referenceEntry.getValue();
                MappingNode testNode = toTest.getValue().get(referenceEntry.getKey());

                testMappingNode(toTest.getKey(), referenceEntry.getKey(), reference, testNode);
            }
        }
    }

    private final YamlConstructor constructor = new YamlConstructor();

    private void testMappingNode(String languageName, String fileName, MappingNode reference, MappingNode testNode) {
        constructor.flattenMapping(reference);
        constructor.flattenMapping(testNode);

        for (NodeTuple nodeTuple : reference.getValue()) {
            Node key = nodeTuple.getKeyNode();
            String keyString = String.valueOf(constructor.construct(key));
            Node value = nodeTuple.getValueNode();

            while (value instanceof AnchorNode) {
                value = ((AnchorNode) value).getRealNode();
            }

            NodeTuple testTuple = searchTuple(keyString, testNode);

            assertNotNull(testTuple, "Language " + languageName + " does not have key " + keyString + " in file " + fileName);

            Node testValue = testTuple.getValueNode();
            while (testValue instanceof AnchorNode) {
                testValue = ((AnchorNode) testValue).getRealNode();
            }

            if (value instanceof MappingNode valueMappingNode) {
                assertTrue(testValue instanceof MappingNode, "Language " + languageName + " should have a MappingNode at " + keyString + " in file " + fileName + " but has " + testValue);
                testMappingNode(languageName, fileName, valueMappingNode, (MappingNode) testValue);
            }
        }
    }

    private NodeTuple searchTuple(String searchKey, MappingNode testNode) {
        for (NodeTuple nodeTuple : testNode.getValue()) {
            Node key = nodeTuple.getKeyNode();
            String keyString = String.valueOf(constructor.construct(key));

            if (keyString.equals(searchKey)) {
                return nodeTuple;
            }
        }

        return null;
    }
}