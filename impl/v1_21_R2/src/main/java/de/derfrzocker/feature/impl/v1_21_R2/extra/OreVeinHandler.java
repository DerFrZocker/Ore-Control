package de.derfrzocker.feature.impl.v1_21_R2.extra;

import de.derfrzocker.feature.api.ExtraValues;
import de.derfrzocker.ore.control.api.OreControlManager;
import de.derfrzocker.ore.control.api.config.ConfigInfo;
import de.derfrzocker.ore.control.api.config.ConfigManager;
import de.derfrzocker.ore.control.impl.v1_21_R2.NMSReflectionNames;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class OreVeinHandler implements Listener {

    private final static Field WORLD_GEN_CONTEXT_FIELD;
    private final static Field NOISE_GENERATOR_SETTINGS_FIELD;

    static {
        try {
            WORLD_GEN_CONTEXT_FIELD = ChunkMap.class.getDeclaredField(NMSReflectionNames.CHUNK_MAP_WORLD_GEN_CONTEXT);
            WORLD_GEN_CONTEXT_FIELD.setAccessible(true);
            NOISE_GENERATOR_SETTINGS_FIELD = NoiseBasedChunkGenerator.class.getDeclaredField(NMSReflectionNames.NOISE_GENERATOR_SETTINGS);
            NOISE_GENERATOR_SETTINGS_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<World> hookedWorlds = new HashSet<>();
    private final ConfigManager configManager;

    public OreVeinHandler(OreControlManager oreControlManager) {
        this.configManager = oreControlManager.getConfigManager();
        oreControlManager.addValueChangeListener(this::update);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWorldInit(WorldInitEvent event) {
        if (!(((CraftWorld) event.getWorld()).getHandle().getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noiseChunkGenerator)) {
            return;
        }

        NoiseGeneratorSettings newSettings = getNewValue(event.getWorld(), noiseChunkGenerator.settings.value());
        NoiseBasedChunkGenerator newGenerator = new NoiseBasedChunkGenerator(noiseChunkGenerator.getBiomeSource(), Holder.direct(newSettings));

        try {
            WorldGenContext worldGenContext = (WorldGenContext) WORLD_GEN_CONTEXT_FIELD.get(((CraftWorld) event.getWorld()).getHandle().getChunkSource().chunkMap);
            WORLD_GEN_CONTEXT_FIELD.set(((CraftWorld) event.getWorld()).getHandle().getChunkSource().chunkMap, new WorldGenContext(worldGenContext.level(), newGenerator, worldGenContext.structureManager(), worldGenContext.lightEngine(), worldGenContext.mainThreadExecutor(), worldGenContext.unsavedListener()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        hookedWorlds.add(event.getWorld());
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        // Cleanup
        hookedWorlds.remove(event.getWorld());
    }

    private NoiseGeneratorSettings getNewValue(World world, NoiseGeneratorSettings old) {
        ConfigInfo configInfo = configManager.getOrCreateConfigInfo(world.getName());
        Optional<ExtraValues> optionalExtraValues = configManager.getGenerationExtraValues(configInfo);

        boolean newValue = optionalExtraValues.flatMap(ExtraValues::shouldGeneratedBigOreVeins).orElse(old.oreVeinsEnabled());

        return new NoiseGeneratorSettings(old.noiseSettings(), old.defaultBlock(), old.defaultFluid(),
                old.noiseRouter(), old.surfaceRule(), old.spawnTarget(), old.seaLevel(), old.disableMobGeneration(), old.aquifersEnabled(), newValue, old.useLegacyRandomSource());
    }

    private void update() {
        for (World world : hookedWorlds) {
            if (!(((CraftWorld) world).getHandle().getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noiseChunkGenerator)) {
                return;
            }

            NoiseGeneratorSettings newSettings = getNewValue(world, noiseChunkGenerator.settings.value());

            try {
                NOISE_GENERATOR_SETTINGS_FIELD.set(noiseChunkGenerator, Holder.direct(newSettings));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
