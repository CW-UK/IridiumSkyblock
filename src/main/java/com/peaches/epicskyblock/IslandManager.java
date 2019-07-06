package com.peaches.epicskyblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IslandManager {

    public HashMap<Integer, Island> islands = new HashMap<>();
    public HashMap<String, User> users = new HashMap<>();

    public Direction direction = Direction.UNDEFINED;
    public String worldName = "EpicSkyblock";
    public Location nextLocation;

    public int nextID = 1;

    public IslandManager() {
        makeWorld();
        nextLocation = new Location(getWorld(), 0, 0, 0);
    }

    private transient Schematic schematic;

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public Island createIsland(Player player) {
        Location pos1 = nextLocation.clone().subtract(EpicSkyblock.getConfiguration().size / 2, 0, EpicSkyblock.getConfiguration().size / 2);
        Location pos2 = nextLocation.clone().add(EpicSkyblock.getConfiguration().size / 2, 0, EpicSkyblock.getConfiguration().size / 2);
        Location center = nextLocation.clone().add(0, 100, 0);
        Location home = nextLocation.clone().add(0.5, 97, -1.5);
        Island island = new Island(player, pos1, pos2, center, home, nextID);
        islands.put(nextID, island);

        User.getUser(player.getName()).islandID = nextID;

        island.generateIsland();
        island.teleportHome(player);

        NMSUtils.sendTitle(player, "&e&lIsland Created", 20, 40, 20);

        //Preparing for next Island
        direction = direction.next();
        switch (direction) {
            case NORTH:
                nextLocation.add(EpicSkyblock.getConfiguration().distance, 0, 0);
            case EAST:
                nextLocation.add(0, 0, EpicSkyblock.getConfiguration().distance);
            case SOUTH:
                nextLocation.subtract(EpicSkyblock.getConfiguration().distance, 0, 0);
            case WEST:
                nextLocation.subtract(0, 0, EpicSkyblock.getConfiguration().distance);
        }
        EpicSkyblock.getInstance().saveConfigs();

        nextID++;

        return island;
    }

    private void makeWorld() {
        if (Bukkit.getWorld(worldName) == null) {
            WorldCreator wc = new WorldCreator(worldName);
            wc.generateStructures(false);
            wc.generator(new SkyblockGenerator());
            Bukkit.getServer().createWorld(wc);
        }
        new WorldCreator(worldName).generator(new SkyblockGenerator());
    }

    public Schematic getSchematic() throws IOException {
        File schematicFolder = new File(EpicSkyblock.getInstance().getDataFolder(), "schematics");
        if (!schematicFolder.exists()) {
            schematicFolder.mkdir();
        }
        File schematicFile = new File(schematicFolder, "island.schematic");

        if (!schematicFile.exists()) {
            if (EpicSkyblock.getInstance().getResource("schematics/island.schematic") != null) {
                EpicSkyblock.getInstance().saveResource("schematics/island.schematic", false);
            }
        }
        if (schematic == null) schematic = Schematic.loadSchematic(schematicFile);
        return schematic;
    }
}