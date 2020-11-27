package com.iridium.iridiumskyblock.managers;

import com.iridium.iridiumskyblock.Direction;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import org.bukkit.Location;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LegacyIslandManager {
    //Used solely for converting old .json to SQL

    private Map<Integer, Island> islands;
    private Map<List<Integer>, Set<Integer>> islandCache;
    private Map<String, User> users;

    int length;
    int current;

    public Direction direction;
    public Location nextLocation;

    public int nextID;


    public void moveToSQL() {
        if (users != null) {
            for (String uuid : users.keySet()) {
                UserManager.cache.put(UUID.fromString(uuid), users.get(uuid));
            }
            users = null;
        }
        if (islandCache != null) {
            for (List<Integer> coords : islandCache.keySet()) {
                for (int id : islandCache.get(coords)) {
                    ClaimManager.addClaim(coords.get(0), coords.get(1), id);
                }
            }
            islandCache = null;
        }
    }
}
