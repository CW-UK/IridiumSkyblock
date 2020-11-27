package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.UUID;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        try {
            final Location toLocation = event.getTo();
            final Location fromLocation = event.getFrom();
            if (!IslandManager.isIslandWorld(toLocation)) return;
            final Island toIsland = IslandManager.getIslandViaLocation(toLocation);
            if (toIsland == null) return;

            final Player player = event.getPlayer();
            Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> toIsland.sendHomograms(player), 1);
            final User user = User.getUser(player);

            if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
                Island fromIsland = IslandManager.getIslandViaLocation(fromLocation);
                if (fromIsland == null || !fromIsland.isInIsland(toLocation)) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (user.islandID == toIsland.getId()) return;

            if ((toIsland.isVisit() && !toIsland.isBanned(user)) || user.bypassing) {
                if (!toIsland.isInIsland(fromLocation)) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(IridiumSkyblock.getInstance(), () -> toIsland.sendBorder(player), 1);
                    if (user.islandID != toIsland.getId()) {
                        player.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitingIsland.replace("%player%", User.getUser(toIsland.getOwner()).name).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        if (player.hasPermission("iridiumskyblock.silentvisit")) return;
                        for (String pl : toIsland.getMembers()) {
                            Player p = Bukkit.getPlayer(UUID.fromString(pl));
                            if (p != null && p.canSee(player)) {
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().visitedYourIsland.replace("%player%", player.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } catch (Exception e) {
            IridiumSkyblock.getInstance().sendErrorMessage(e);
        }
    }
}
