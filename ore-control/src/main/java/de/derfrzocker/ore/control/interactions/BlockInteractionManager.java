package de.derfrzocker.ore.control.interactions;

import de.derfrzocker.spigot.utils.language.LanguageManager;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BlockInteractionManager implements Listener {

    private final Map<Player, BlockInteractionListener> pendingInteractions = new ConcurrentHashMap<>();
    private final LanguageManager languageManager;

    public BlockInteractionManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public void createBasicBlockDataInteraction(Player player, Consumer<BlockData> acceptConsumer, Runnable cancel) {
        // TODO: 5/7/23 Send Message
        pendingInteractions.put(player, new BlockInteractionListener() {
            @Override
            public boolean onBlockShiftLeftClick(Player player, BlockState blockState) {
                cancel.run();
                return true;
            }

            @Override
            public boolean onBlockLeftClick(Player player, BlockState blockState) {
                acceptConsumer.accept(blockState.getBlockData());
                return true;
            }

            @Override
            public boolean onAirShiftLeftClick(Player player) {
                cancel.run();
                return true;
            }

            @Override
            public boolean onAirLeftClick(Player player) {
                acceptConsumer.accept(Material.AIR.createBlockData());
                return true;
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(@NotNull final PlayerInteractEvent event) {
       Player player = event.getPlayer();
       BlockInteractionListener interactionListener = pendingInteractions.get(player);

       if (interactionListener == null) {
           return;
       }

       event.setCancelled(true);

       boolean remove = false;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                remove = interactionListener.onBlockShiftLeftClick(player, event.getClickedBlock().getState());
            } else {
                remove = interactionListener.onBlockLeftClick(player, event.getClickedBlock().getState());
            }
        } else if (event.getAction() == Action.LEFT_CLICK_AIR) {
            if (player.isSneaking()) {
               remove =  interactionListener.onAirShiftLeftClick(player);
            } else {
                remove = interactionListener.onAirLeftClick(player);
            }
        }

        if (remove) {
            pendingInteractions.remove(player);
        }
    }
}
