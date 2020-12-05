package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Config;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {

        Config config = IridiumSkyblock.getConfiguration();
        if (!config.disableNaturalAnimalSpawns && !config.disableNaturalMobSpawns) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

        final Location location = event.getEntity().getLocation();
        final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        if (!islandManager.isIslandWorld(location)) return;

        if ((event.getEntity() instanceof Monster && config.disableNaturalMobSpawns) || (event.getEntity() instanceof Animals && config.disableNaturalAnimalSpawns)) {
            event.setCancelled(true);
            return;
        }

    }

}
