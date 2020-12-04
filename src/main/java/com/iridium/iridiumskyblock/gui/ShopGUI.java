package com.iridium.iridiumskyblock.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ShopGUI extends GUI implements Listener {

    public ShopGUI root;

    public Shop.ShopObject shop;

    public int page;

    public Map<Integer, ShopGUI> shops = new HashMap<>();

    public Map<Integer, Shop.ShopItem> items = new HashMap<>();

    public ShopGUI() {
        super(IridiumSkyblock.getInventories().shopGUISize, IridiumSkyblock.getInventories().shopGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);

        for (Shop.ShopObject shop : IridiumSkyblock.getShop().shop) {
            setItem(shop.slot, Utils.makeItem(shop.display, 1, shop.displayName));
            if (!shops.containsKey(shop.slot)) {
                shops.put(shop.slot, new ShopGUI(shop, this));
            }
        }
    }

    public ShopGUI(Shop.ShopObject shop, int page, ShopGUI root) {
        super(IridiumSkyblock.getInventories().shopGUISize, IridiumSkyblock.getInventories().shopGUITitle);
        this.shop = shop;
        this.page = page;
        this.root = root;
    }

    public ShopGUI(Shop.ShopObject shop, ShopGUI root) {
        scheduler = Bukkit.getScheduler().scheduleAsyncRepeatingTask(IridiumSkyblock.getInstance(), this::addPages, 0, 5);
        this.shop = shop;
        this.root = root;

    }

    public void addPages() {
        for (Shop.ShopItem item : shop.items) {
            if (item == null) continue;
            if (!shops.containsKey(item.page)) {
                shops.put(item.page, new ShopGUI(shop, item.page, this));
            }
        }
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (!IridiumSkyblock.getConfiguration().islandShop) return;
        if (shop != null) {
            for (Shop.ShopItem item : shop.items) {
                if (item.page == page) {
                    items.put(item.slot, item);
                    setItem(item.slot, Utils.makeItem(item.material, item.amount, item.displayName,
                            Utils.color(Utils.processMultiplePlaceholders(IridiumSkyblock.getShop().lore, Arrays.asList(
                                    new Utils.Placeholder("buyvaultprice", Utils.NumberFormatter.format(item.buyVault)),
                                    new Utils.Placeholder("sellvaultprice", Utils.NumberFormatter.format(item.sellVault)),
                                    new Utils.Placeholder("buycrystalprice", Utils.NumberFormatter.format(item.buyCrystals)),
                                    new Utils.Placeholder("sellcrystalprice", Utils.NumberFormatter.format(item.sellCrystals)))))));
                }
            }
            setItem(getInventory().getSize() - 3, Utils.makeItem(IridiumSkyblock.getInventories().nextPage));
            setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
            setItem(getInventory().getSize() - 7, Utils.makeItem(IridiumSkyblock.getInventories().previousPage));
        }
    }

    public boolean contains(Player p, XMaterial materials, int amount) {
        int total = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;
            if (materials.isSimilar(item)) {
                total += item.getAmount();
            }
        }
        return total >= amount;
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            if (shop == null) {
                if (shops.containsKey(e.getSlot())) {
                    if (shops.get(e.getSlot()).shops.containsKey(1)) { // This should always be called, but just incase the user configured the plugin incorrectly
                        e.getWhoClicked().openInventory(shops.get(e.getSlot()).shops.get(1).getInventory());
                    }
                }
            } else {
                if (items.containsKey(e.getSlot())) {
                    Shop.ShopItem item = items.get(e.getSlot());
                    if (e.getClick().equals(ClickType.RIGHT)) {
                        if (item.sellVault > 0 || item.sellCrystals > 0) {
                            if (contains((Player) e.getWhoClicked(), item.material, item.amount)) {
                                int removed = 0;
                                int index = 0;
                                for (ItemStack itemStack : e.getWhoClicked().getInventory().getContents()) {
                                    if (itemStack == null) {
                                        index++;
                                        continue;
                                    }
                                    if (removed >= item.amount) break;
                                    if (itemStack != null) {
                                        if (item.material.isSimilar(itemStack)) {
                                            if (removed + itemStack.getAmount() <= item.amount) {
                                                removed += itemStack.getAmount();
                                                e.getWhoClicked().getInventory().setItem(index, null);
                                            } else {
                                                itemStack.setAmount(itemStack.getAmount() - (item.amount - removed));
                                                removed += item.amount;
                                            }
                                        }
                                    }
                                    index++;
                                }
                                Utils.pay((Player) e.getWhoClicked(), item.sellVault, item.sellCrystals);
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().shopSoldMessage
                                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                        .replace("%item%", item.material + "")
                                        .replace("%amount%", item.amount + "")
                                        .replace("%crystals%", Utils.NumberFormatter.format(item.sellCrystals))
                                        .replace("%money%", Utils.NumberFormatter.format(item.sellVault))));
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cantSell.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cannotSellItem.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        double sellVault = (item.sellVault / item.amount) * 64;
                        int sellCrystals = (int) Math.floor((item.sellCrystals / (double) item.amount) * 64);
                        if (item.sellVault > 0 || item.sellCrystals > 0) {
                            if (contains((Player) e.getWhoClicked(), item.material, 64)) {
                                int removed = 0;
                                int index = 0;
                                for (ItemStack itemStack : e.getWhoClicked().getInventory().getContents()) {
                                    if (itemStack == null) {
                                        index++;
                                        continue;
                                    }
                                    if (removed >= 64) break;
                                    if (itemStack != null) {
                                        if (item.material.isSimilar(itemStack)) {
                                            if (removed + itemStack.getAmount() <= 64) {
                                                removed += itemStack.getAmount();
                                                e.getWhoClicked().getInventory().setItem(index, null);
                                            } else {
                                                itemStack.setAmount(itemStack.getAmount() - (64 - removed));
                                                removed += 64;
                                            }
                                        }
                                    }
                                    index++;
                                }
                                Utils.pay((Player) e.getWhoClicked(), sellVault, sellCrystals);
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().shopSoldMessage
                                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                        .replace("%item%", item.material + "")
                                        .replace("%amount%", 64 + "")
                                        .replace("%crystals%", Utils.NumberFormatter.format(sellCrystals))
                                        .replace("%money%", Utils.NumberFormatter.format(sellVault))));
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cantSell.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cannotSellItem.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else if (e.getClick().equals(ClickType.LEFT)) {
                        if (item.buyVault > 0 || item.buyCrystals > 0) {
                            Utils.BuyResponce responce = Utils.canBuy((Player) e.getWhoClicked(), item.buyVault, item.buyCrystals);
                            if (responce == Utils.BuyResponce.SUCCESS) {
                                if (item.commands != null) {
                                    for (String Command : item.commands) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command.replace("%player%", e.getWhoClicked().getName()));
                                    }
                                } else {
                                    ItemStack itemStack = item.material.parseItem();
                                    itemStack.setAmount(item.amount);
                                    if (Utils.hasOpenSlot(e.getWhoClicked().getInventory())) {
                                        e.getWhoClicked().getInventory().addItem(itemStack);
                                    } else {
                                        e.getWhoClicked().getLocation().getWorld().dropItem(e.getWhoClicked().getLocation(), itemStack);
                                    }
                                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().shopBoughtMessage
                                            .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                            .replace("%item%", item.material + "")
                                            .replace("%amount%", item.amount + "")
                                            .replace("%crystals%", Utils.NumberFormatter.format(item.buyCrystals))
                                            .replace("%money%", Utils.NumberFormatter.format(item.buyVault))));
                                }
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color((responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cannotBuyItem.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                        if (item.buyVault > 0 || item.buyCrystals > 0) {
                            //If we are running commands we dont want to charge them 64x the price since we dont stack buy commands
                            double buyVault = item.commands != null ? item.buyVault : (item.buyVault / item.amount) * 64;
                            int buyCrystals = item.commands != null ? item.buyCrystals : (int) Math.floor((item.buyCrystals / (double) item.amount) * 64);
                            Utils.BuyResponce responce = Utils.canBuy((Player) e.getWhoClicked(), buyVault, buyCrystals);
                            if (responce == Utils.BuyResponce.SUCCESS) {
                                if (item.commands != null) {
                                    for (String Command : item.commands) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), Command.replace("%player%", e.getWhoClicked().getName()));
                                    }
                                } else {
                                    ItemStack itemStack = item.material.parseItem();
                                    itemStack.setAmount(64);
                                    if (Utils.hasOpenSlot(e.getWhoClicked().getInventory())) {
                                        e.getWhoClicked().getInventory().addItem(itemStack);
                                    } else {
                                        e.getWhoClicked().getLocation().getWorld().dropItem(e.getWhoClicked().getLocation(), itemStack);
                                    }
                                    e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().shopBoughtMessage
                                            .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)
                                            .replace("%item%", item.material + "")
                                            .replace("%amount%", 64 + "")
                                            .replace("%crystals%", Utils.NumberFormatter.format(buyCrystals))
                                            .replace("%money%", Utils.NumberFormatter.format(buyVault))));
                                }
                            } else {
                                e.getWhoClicked().sendMessage(Utils.color(responce == Utils.BuyResponce.NOT_ENOUGH_VAULT ? IridiumSkyblock.getMessages().cantBuy : IridiumSkyblock.getMessages().notEnoughCrystals.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        } else {
                            e.getWhoClicked().sendMessage(Utils.color(IridiumSkyblock.getMessages().cannotBuyItem.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 3) {
                    if (root.shops.containsKey(page + 1)) {
                        e.getWhoClicked().openInventory(root.shops.get(page + 1).getInventory());
                    }
                }
                if (e.getSlot() == getInventory().getSize() - 5) {
                    e.getWhoClicked().openInventory(root.root.getInventory());
                }
                if (e.getSlot() == getInventory().getSize() - 7) {
                    if (root.shops.containsKey(page - 1)) {
                        e.getWhoClicked().openInventory(root.shops.get(page - 1).getInventory());
                    }
                }
            }
        }
        for (ShopGUI gui : shops.values()) {
            gui.onInventoryClick(e);
        }
    }
}