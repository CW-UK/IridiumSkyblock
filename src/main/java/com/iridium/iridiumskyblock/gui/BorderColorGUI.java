package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class BorderColorGUI extends GUI implements Listener {

    public BorderColorGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().borderColorGUISize, IridiumSkyblock.getInventories().borderColorGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (IridiumSkyblock.border.RedEnabled)
            setItem(IridiumSkyblock.getInventories().red.slot, Utils.makeItem(IridiumSkyblock.getInventories().red));
        if (IridiumSkyblock.border.BlueEnabled)
            setItem(IridiumSkyblock.getInventories().blue.slot, Utils.makeItem(IridiumSkyblock.getInventories().blue));
        if (IridiumSkyblock.border.GreenEnabled)
            setItem(IridiumSkyblock.getInventories().green.slot, Utils.makeItem(IridiumSkyblock.getInventories().green));
        if (IridiumSkyblock.border.OffEnabled)
            setItem(IridiumSkyblock.getInventories().off.slot, Utils.makeItem(IridiumSkyblock.getInventories().off));
        if (IridiumSkyblock.getInventories().backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getCurrentItem() != null) {
                if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                    e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
                }
                if (IridiumSkyblock.border.BlueEnabled && e.getSlot() == IridiumSkyblock.getInventories().blue.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Blue);
                if (IridiumSkyblock.border.RedEnabled && e.getSlot() == IridiumSkyblock.getInventories().red.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Red);
                if (IridiumSkyblock.border.GreenEnabled && e.getSlot() == IridiumSkyblock.getInventories().green.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Green);
                if (IridiumSkyblock.border.OffEnabled && e.getSlot() == IridiumSkyblock.getInventories().off.slot)
                    IslandManager.getIslandViaId(islandID).setBorderColor(Color.Off);
            }
        }
    }
}
