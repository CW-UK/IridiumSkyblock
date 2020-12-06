package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.configs.Config;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Animals;

public class CreatureSpawnListener implements Listener {

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        try {
            final Config config = IridiumSkyblock.getConfiguration();
            if (!config.disableNaturalAnimalSpawns && !config.disableNaturalMonsterSpawns) return;

            final Entity entity = event.getEntity();
            final Location location = entity.getLocation();
            if (!IridiumSkyblock.getIslandManager().isIslandWorld(location) || event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

            if ((entity instanceof Monster && config.disableNaturalMonsterSpawns) || (entity instanceof Animals && config.disableNaturalAnimalSpawns)) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }

}
