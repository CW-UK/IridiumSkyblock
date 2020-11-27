package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoopGUI extends GUI implements Listener {

    public Map<Integer, Integer> islands = new HashMap<>();

    public CoopGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().coopGUISize, IridiumSkyblock.getInventories().coopGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        islands.clear();
        Island island = getIsland();
        if (island != null) {
            int i = 0;
            for (int id : island.getCoop()) {
                Island is = IslandManager.getIslandViaId(id);
                if (is != null) {
                    islands.put(i, id);
                    User user = User.getUser(is.getOwner());
                    ItemStack head = Utils.makeItem(IridiumSkyblock.getInventories().islandcoop, Arrays.asList(new Utils.Placeholder("player", user.name), new Utils.Placeholder("name", is.getName()), new Utils.Placeholder("rank", Utils.getIslandRank(is) + ""), new Utils.Placeholder("value", is.getFormattedValue())));
                    setItem(i, head);
                    i++;
                } else {
                    island.removeCoop(id);
                }
            }
            if (IridiumSkyblock.getInventories().backButtons) setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
            }
            if (islands.containsKey(e.getSlot())) {
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                User u = User.getUser((OfflinePlayer) e.getWhoClicked());
                if (e.getClick().equals(ClickType.RIGHT)) {
                    if (u.bypassing || u.getIsland().getPermissions(u.getRole()).coop) {
                        getIsland().removeCoop(island);
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().noPermission.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    if (island.isVisit() || u.bypassing) {
                        island.teleportHome((Player) e.getWhoClicked());
                    } else {
                        e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                }
            }
        }
    }
}
