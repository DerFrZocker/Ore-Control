package de.derfrzocker.ore.control.interactions;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public interface BlockInteractionListener {

    boolean onBlockShiftLeftClick(Player player, BlockState blockState);

    boolean onBlockLeftClick(Player player, BlockState blockState);

    boolean onAirShiftLeftClick(Player player);

    boolean onAirLeftClick(Player player);
}
