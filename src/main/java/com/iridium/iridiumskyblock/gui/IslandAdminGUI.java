package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Inventories;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IslandAdminGUI extends GUI implements Listener {

    public IslandAdminGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().islandMenuGUISize, IridiumSkyblock.getInventories().islandMenuGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            for (Inventories.Item item : IridiumSkyblock.getInventories().menu.keySet()) {
                setItem(item.slot, Utils.makeItemHidden(item, getIsland()));
            }
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            for (Inventories.Item item : IridiumSkyblock.getInventories().menu.keySet()) {
                if (item.slot == e.getSlot()) {
                    p.closeInventory();
                    Bukkit.getServer().dispatchCommand(e.getWhoClicked(), IridiumSkyblock.getInventories().menu.get(item).replace("is", "is admin " + islandID));
                    return;
                }
            }
        }
    }
}
