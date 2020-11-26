package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;
import java.util.function.Supplier;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        final Entity damagee = event.getEntity();
        if (!(damagee instanceof Player)) return;
        final Player player = (Player) damagee;
        final User user = User.getUser(player);
        final Location damageeLocation = damagee.getLocation();
        final IslandManager islandManager = IridiumSkyblock.getIslandManager();
        final Island island = islandManager.getIslandViaLocation(damageeLocation);
        if (island == null) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) return;

        //The user is visiting this island, so disable damage
        if (user.islandID != island.getId() && IridiumSkyblock.getConfiguration().disablePvPOnIslands) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        try {
            final Entity damagee = event.getEntity();
            final Location damageeLocation = damagee.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(damageeLocation);
            if (island == null) return;

            final Entity damager = event.getDamager();

            if (damager instanceof Egg && damagee instanceof ItemFrame) {
                Player player = (Player) ((Egg) damager).getShooter();
                User user = User.getUser(player);
                if (player != null && !island.getMembers().contains(player.getUniqueId().toString()) && !island.isCoop(user.getIsland())) {
                    event.setCancelled(true);
                }
            }
            // Using suppliers to defer work if unnecessary
            // This includes seemingly innocuous downcast operations
            final Supplier<Player> damageePlayerSupplier = () -> (Player) damagee;
            final Supplier<User> damageeUserSupplier = () -> User.getUser(damageePlayerSupplier.get());
            final Supplier<Island> damageeIslandSupplier = () -> damageeUserSupplier.get().getIsland();
            final Supplier<Arrow> arrowSupplier = () -> (Arrow) damager;
            final Supplier<ProjectileSource> projectileSourceSupplier = () -> arrowSupplier.get().getShooter();
            final Supplier<Player> shooterSupplier = () -> (Player) projectileSourceSupplier.get();
            final Supplier<User> shootingUserSupplier = () -> User.getUser(Objects.requireNonNull(shooterSupplier.get()));
            final Supplier<Player> damagingPlayerSupplier = () -> (Player) damager;
            final Supplier<User> damagingUserSupplier = () -> User.getUser(damagingPlayerSupplier.get());

            // Deals with two players pvping in IridiumSkyblock world
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
                    && damagee instanceof Player
                    && damager instanceof Player) {
                event.setCancelled(true);
                return;
            }

            // Deals with A player getting damaged by a bow fired from a player in IridiumSkyblock world
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
                    && damagee instanceof Player
                    && damager instanceof Arrow
                    && projectileSourceSupplier.get() instanceof Player) {
                event.setCancelled(true);
                return;
            }

            // Deals with a player attacking animals with bows that are not from their island
            if (damager instanceof Arrow
                    && !(damagee instanceof Player)
                    && projectileSourceSupplier.get() instanceof Player
                    && !island.getPermissions(shootingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            // Deals with a player attacking animals that are not from their island
            if (damager instanceof Player
                    && !(damagee instanceof Player)
                    && !island.getPermissions(damagingUserSupplier.get()).killMobs) {
                event.setCancelled(true);
                return;
            }

            //Deals with a mob attacking a player that doesn't belong to the island (/is home traps?)
            if (IridiumSkyblock.getConfiguration().disablePvPOnIslands
                    && damagee instanceof Player
                    && !(damager instanceof Player)) {
                if (damageeIslandSupplier.get() != null) {
                    if (!damageeIslandSupplier.get().isInIsland(damager.getLocation())) {
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    event.setCancelled(true);
                    return;
                }
            }

            // Deals with two allies pvping
            if (IridiumSkyblock.getConfiguration().disablePvPBetweenIslandMembers
                    && damagee instanceof Player
                    && damager instanceof Player
                    && damageeIslandSupplier.get() != null
                    && damageeIslandSupplier.get().equals(damagingUserSupplier.get().getIsland())) {
                event.setCancelled(true);
                return;
            }

            // Deals with two allies pvping with bows
            if (IridiumSkyblock.getConfiguration().disablePvPBetweenIslandMembers
                    && damagee instanceof Player
                    && damager instanceof Arrow
                    && projectileSourceSupplier.get() instanceof Player
                    && damageeIslandSupplier.get() != null
                    && damageeIslandSupplier.get().equals(damagingUserSupplier.get().getIsland())) {
                event.setCancelled(true);
                return;
            }
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler
    public void onVehicleDamage(VehicleDamageEvent event) {
        try {
            final Vehicle vehicle = event.getVehicle();
            final Location location = vehicle.getLocation();
            final IslandManager islandManager = IridiumSkyblock.getIslandManager();
            final Island island = islandManager.getIslandViaLocation(location);
            if (island == null) return;

            final Entity attacker = event.getAttacker();
            if (!(attacker instanceof Player)) return;

            final Player attackerPlayer = (Player) attacker;
            final User attackerUser = User.getUser(attackerPlayer);

            if (!island.getPermissions(attackerUser).killMobs)
                event.setCancelled(true);
        } catch (Exception ex) {
            IridiumSkyblock.getInstance().sendErrorMessage(ex);
        }
    }

    @EventHandler
    public void onHangingByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Egg && event.getEntity() instanceof ItemFrame) {
            Entity entity = event.getEntity();
            Location location = entity.getLocation();
            IslandManager islandManager = IridiumSkyblock.getIslandManager();
            if (!islandManager.isIslandWorld(location)) return;
            Island island = islandManager.getIslandViaLocation(location);
            Player player = (Player) ((Egg) event.getRemover()).getShooter();
            User user = User.getUser(player);
            if (player != null && island != null && !island.getMembers().contains(player.getUniqueId().toString()) && !island.isCoop(user.getIsland())) {
                event.setCancelled(true);
            }
        }
    }
}
