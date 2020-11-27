package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

public class StructureGrowListener implements Listener {

    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent event) {
        Island island = IslandManager.getIslandViaLocation(event.getLocation());
        if (island == null) return;
        for (BlockState blockState : event.getBlocks()) {
            if (!island.isInIsland(blockState.getLocation())) {
                blockState.setType(Material.AIR);
            }
        }
    }

}
