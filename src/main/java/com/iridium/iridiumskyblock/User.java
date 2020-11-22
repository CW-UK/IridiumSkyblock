package com.iridium.iridiumskyblock;

import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class User {

    public String player;
    public String name;
    public int islandID;
    public Role role;
    public Set<Integer> invites;
    public Island.Warp warp;
    public boolean bypassing;
    public boolean islandChat;
    public boolean spyingIslandsChat;
    public boolean flying;
    public transient boolean teleportingHome;
    public transient boolean tookInterestMessage;
    public Date lastCreate;
    private transient List<Object> holograms;

    public User(OfflinePlayer p) {
        invites = new HashSet<>();
        this.player = p.getUniqueId().toString();
        this.name = p.getName();
        this.islandID = 0;
        bypassing = false;
        islandChat = false;
        spyingIslandsChat = false;
        flying = false;
        IridiumSkyblock.getIslandManager().users.put(this.player, this);
    }

    public Island getIsland() {
        return IridiumSkyblock.getIslandManager().islands.getOrDefault(islandID, null);
    }

    public Role getRole() {
        if (role == null) {
            if (getIsland() != null) {
                if (getIsland().getOwner().equals(player)) {
                    role = Role.Owner;
                } else {
                    role = Role.Member;
                }
            } else {
                role = Role.Visitor;
            }
        }
        return role;
    }

    public boolean isOnCooldown() {
        return IridiumSkyblock.getConfiguration().createCooldown && this.lastCreate != null && !this.bypassing && new Date().before(this.lastCreate);
    }

    public String getCooldownTimeMessage() {
        long time = (this.lastCreate.getTime() - System.currentTimeMillis()) / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(time);
        int hours = (int) Math.floor(TimeUnit.SECONDS.toHours(time - day * 86400));
        int minute = (int) Math.floor((time - day * 86400 - hours * 3600) / 60.00);
        int second = (int) Math.floor((time - day * 86400 - hours * 3600) % 60.00);
        return IridiumSkyblock.getMessages().createCooldown.replace("%days%", day + "").replace("%hours%", hours + "").replace("%minutes%", minute + "").replace("%seconds%", second + "").replace("%prefix%", IridiumSkyblock.getConfiguration().prefix);
    }

    public static User getUser(String p) {
        if (IridiumSkyblock.getIslandManager().users == null)
            IridiumSkyblock.getIslandManager().users = new HashMap<>();
        return IridiumSkyblock.getIslandManager().users.get(p);
    }

    public static User getUser(OfflinePlayer p) {
        if (p == null) return null;
        if (IridiumSkyblock.getIslandManager().users == null)
            IridiumSkyblock.getIslandManager().users = new HashMap<>();
        return IridiumSkyblock.getIslandManager().users.containsKey(p.getUniqueId().toString()) ? IridiumSkyblock.getIslandManager().users.get(p.getUniqueId().toString()) : new User(p);
    }

    public List<Object> getHolograms() {
        if (holograms == null) holograms = new ArrayList<>();
        return holograms;
    }

    public void addHologram(Object object) {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.add(object);
    }

    public void removeHologram(Object object) {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.remove(object);
    }

    public void clearHolograms() {
        if (holograms == null) holograms = new ArrayList<>();
        holograms.clear();
    }
}
