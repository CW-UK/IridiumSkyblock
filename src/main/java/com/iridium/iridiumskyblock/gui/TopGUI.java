package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandDataManager;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TopGUI extends GUI implements Listener {

    public Map<Integer, Integer> islands = new HashMap<>();

    public TopGUI() {
        super(IridiumSkyblock.getInventories().topGUISize, IridiumSkyblock.getInventories().topGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        ItemStack filler = Utils.makeItem(IridiumSkyblock.getInventories().topfiller);
        for (int i : IridiumSkyblock.getConfiguration().islandTopSlots.keySet()) {
            CompletableFuture<List<Integer>> completableFuture = IslandDataManager.getIslands(IslandDataManager.IslandSortType.VALUE, i - 1, i, false);
            completableFuture.thenRun(() -> {
                try {
                    List<Integer> islandid = completableFuture.get();
                    if (islandid.size() > 0) {
                        Island island = IslandManager.getIslandViaId(islandid.get(0));
                        if (island == null) return;
                        User owner = User.getUser(island.getOwner());
                        ArrayList<Utils.Placeholder> placeholders = new ArrayList<>(Arrays.asList(new Utils.Placeholder("player", owner.name), new Utils.Placeholder("votes", island.getVotes() + ""), new Utils.Placeholder("name", island.getName()), new Utils.Placeholder("rank", i + ""), new Utils.Placeholder("level", Utils.NumberFormatter.format(island.getValue() / IridiumSkyblock.getConfiguration().valuePerLevel)), new Utils.Placeholder("value", island.getFormattedValue()), new Utils.Placeholder("members", island.getMembers().size() + "")));
                        for (XMaterial item : IridiumSkyblock.getBlockValues().blockvalue.keySet()) {
                            placeholders.add(new Utils.Placeholder(item.name() + "_amount", "" + island.valuableBlocks.getOrDefault(item.name(), 0)));
                        }
                        for (String item : IridiumSkyblock.getBlockValues().spawnervalue.keySet()) {
                            placeholders.add(new Utils.Placeholder(item + "_amount", "" + island.spawners.getOrDefault(item, 0)));
                        }
                        placeholders.add(new Utils.Placeholder("ISLANDBANK_value", IridiumSkyblock.getConfiguration().islandMoneyPerValue != 0 ? Utils.NumberFormatter.format(island.money / IridiumSkyblock.getConfiguration().islandMoneyPerValue) : "0"));
                        ItemStack head = Utils.makeItem(IridiumSkyblock.getInventories().topisland, placeholders);
                        islands.put(IridiumSkyblock.getConfiguration().islandTopSlots.get(i), island.getId());
                        setItem(IridiumSkyblock.getConfiguration().islandTopSlots.get(i), head);
                    } else {
                        setItem(IridiumSkyblock.getConfiguration().islandTopSlots.get(i), filler);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
        if (IridiumSkyblock.getInventories().backButtons)
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(User.getUser((Player) e.getWhoClicked()).getIsland().getIslandMenuGUI().getInventory());
            }
            if (islands.containsKey(e.getSlot())) {
                e.getWhoClicked().closeInventory();
                Island island = IslandManager.getIslandViaId(islands.get(e.getSlot()));
                if (island.isVisit() || User.getUser((OfflinePlayer) e.getWhoClicked()).bypassing) {
                    island.teleportHome((Player) e.getWhoClicked());
                } else {
                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().playersIslandIsPrivate.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
            }
        }
    }
}