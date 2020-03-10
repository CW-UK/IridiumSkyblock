package com.iridium.iridiumskyblock.api;

import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Role;
import com.iridium.iridiumskyblock.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandDemoteEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private Island island;
    private User target;
    private User demoter;
    private Role role;
    private boolean cancel;

    public IslandDemoteEvent(Island island, User target, User demoter, Role role) {
        this.island = island;
        this.target = target;
        this.demoter = demoter;
        this.role = role;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public User getTarget() {
        return target;
    }

    public User getDemoter() {
        return demoter;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Island getIsland() {
        return island;
    }
}
