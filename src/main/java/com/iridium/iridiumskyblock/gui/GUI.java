package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class GUI {

    private Inventory inventory;
    public int islandID;
    public int scheduler;

    public GUI() {

    }

    public GUI(Island island, int size, String name) {
        islandID = island.getId();
        this.inventory = Bukkit.createInventory(null, size, Utils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, 2);
    }

    public GUI(int size, String name) {
        this.inventory = Bukkit.createInventory(null, size, Utils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, 2);
    }

    public GUI(Island island, int size, String name, int refresh) {
        islandID = island.getId();
        this.inventory = Bukkit.createInventory(null, size, Utils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, refresh);
    }

    public GUI(int size, String name, int refresh) {
        this.inventory = Bukkit.createInventory(null, size, Utils.color(name));
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addContent, 0, refresh);
    }

    public void addContent() {
        if (inventory.getViewers().isEmpty()) return;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR)) {
                setItem(i, Utils.makeItemHidden(IridiumSkyblock.getInventories().background));
            }
        }
    }

    public void setItem(int i, ItemStack itemStack) {
        if (getInventory().getItem(i) == null || !getInventory().getItem(i).isSimilar(itemStack)) {
            getInventory().setItem(i, itemStack);
        }
    }

    public abstract void onInventoryClick(InventoryClickEvent e);

    public Inventory getInventory() {
        return inventory;
    }

    public Island getIsland() {
        return IslandManager.getIslandViaId(islandID);
    }
}
