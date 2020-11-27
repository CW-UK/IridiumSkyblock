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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitGUI extends GUI implements Listener {

    public Map<Integer, Integer> islands = new HashMap<>();

    private final int page;

    public VisitGUI(int page) {
        super(IridiumSkyblock.getInventories().visitGUISize, IridiumSkyblock.getInventories().visitGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
        this.page = page;
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        List<Island> top = Utils.getIslands();
        int slot = 0;
        int i = 45 * (page - 1);
        while (slot < 45) {
            if (top.size() > i && i >= 0) {
                Island island = top.get(i);
                if (island.isVisit()) {
                    User owner = User.getUser(island.getOwner());
                    ItemStack head = Utils.makeItem(IridiumSkyblock.getInventories().visitisland, Arrays.asList(new Utils.Placeholder("player", owner.name), new Utils.Placeholder("name", island.getName()), new Utils.Placeholder("rank", Utils.getIslandRank(island) + ""), new Utils.Placeholder("votes", NumberFormat.getInstance().format(island.getVotes())), new Utils.Placeholder("value", island.getFormattedValue())));
                    islands.put(slot, island.getId());
                    setItem(slot, head);
                    slot++;
                } else {
                    setItem(slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().background));
                }
                i++;
            } else {
                setItem(slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().background));
                slot++;
            }
        }
        setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
        setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (islands.containsKey(e.getSlot())) {
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                User u = User.getUser((OfflinePlayer) e.getWhoClicked());
                if (island.isVisit() || u.bypassing) {
                    if (e.getClick() == ClickType.RIGHT) {
                        if (island.hasVoted(u)) {
                            island.removeVote(u);
                        } else {
                            island.addVote(u);
                        }
                    } else {
                        e.getWhoClicked().closeInventory();
                        island.teleportHome((Player) e.getWhoClicked());
                    }
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            } else if (e.getSlot() == getInventory().getSize() - 7) {
                if (IridiumSkyblock.visitGUI.containsKey(page - 1))
                    e.getWhoClicked().openInventory(IridiumSkyblock.visitGUI.get(page - 1).getInventory());
            } else if (e.getSlot() == getInventory().getSize() - 3) {
                if (IridiumSkyblock.visitGUI.containsKey(page + 1))
                    e.getWhoClicked().openInventory(IridiumSkyblock.visitGUI.get(page + 1).getInventory());
            }
        }
    }
}