package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.*;
import com.iridium.iridiumskyblock.Utils.TransactionLogger;
import com.iridium.iridiumskyblock.Utils.TransactionLogger.Transaction;
import com.iridium.iridiumskyblock.Utils.TransactionLogger.TransactionType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BankGUI extends GUI implements Listener {

    public BankGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().bankGUISize, IridiumSkyblock.getInventories().bankGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            setItem(IridiumSkyblock.getInventories().experience.slot == null ? 11 : IridiumSkyblock.getInventories().experience.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().experience, getIsland()));
            setItem(IridiumSkyblock.getInventories().crystals.slot == null ? 13 : IridiumSkyblock.getInventories().crystals.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().crystals, getIsland()));
            setItem(IridiumSkyblock.getInventories().money.slot == null ? 15 : IridiumSkyblock.getInventories().money.slot, Utils.makeItemHidden(IridiumSkyblock.getInventories().money, getIsland()));
            if (IridiumSkyblock.getInventories().backButtons)
                setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }

    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getClickedInventory() == null || !e.getClickedInventory().equals(getInventory())) return;
            Player p = (Player) e.getWhoClicked();
            Island island = getIsland();
            User u = User.getUser(p);
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
                return;
            }
            if (!IridiumSkyblock.getConfiguration().bankWithdrawing) {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().withdrawDisabled
                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                return;
            }
            if (e.getSlot() == (IridiumSkyblock.getInventories().experience.slot == null ? 11 : IridiumSkyblock.getInventories().experience.slot)) {
                if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                    if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                        Utils.setTotalExperience(p, Utils.getTotalExperience(p) + island.exp);
                        TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, -island.exp));
                        island.exp = 0;
                    }
                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    island.exp += Utils.getTotalExperience(p);
                    TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, Utils.getTotalExperience(p)));
                    Utils.setTotalExperience(p, 0);
                } else if (e.getClick().equals(ClickType.RIGHT)) {
                    if (Utils.getTotalExperience(p) > 100) {
                        island.exp += 100;
                        Utils.setTotalExperience(p, Utils.getTotalExperience(p) - 100);
                        TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, 100));
                    } else {
                        island.exp += Utils.getTotalExperience(p);
                        TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, Utils.getTotalExperience(p)));
                        Utils.setTotalExperience(p, 0);
                    }
                } else if (e.getClick().equals(ClickType.LEFT)) {
                    if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                        if (island.exp > 100) {
                            island.exp -= 100;
                            Utils.setTotalExperience(p, Utils.getTotalExperience(p) + 100);
                            TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, -100));
                        } else {
                            Utils.setTotalExperience(p, Utils.getTotalExperience(p) + island.exp);
                            TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.EXPERIENCE, -island.exp));
                            island.exp = 0;
                        }
                    }
                }
            }
            if (e.getSlot() == (IridiumSkyblock.getInventories().crystals.slot == null ? 13 : IridiumSkyblock.getInventories().crystals.slot)) {
                if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                    if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                        if (island.getCrystals() > 0) {
                            if (p.getInventory().firstEmpty() != -1) {
                                p.getInventory().addItem(Utils.getCrystals(island.getCrystals()));
                                TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.CRYSTALS, -island.getCrystals()));
                                island.setCrystals(0);
                            } else {
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().inventoryFull
                                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    }
                } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                    int i = 0;
                    int total = 0;
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack == null) {
                            i++;
                            continue;
                        }
                        int crystals = Utils.getCrystals(itemStack) * itemStack.getAmount();
                        if (crystals != 0) {
                            island.setCrystals(island.getCrystals() + crystals);
                            p.getInventory().clear(i);
                            total += crystals;
                        }
                        i++;
                    }
                    TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.CRYSTALS, total));
                } else if (e.getClick().equals(ClickType.RIGHT)) {
                    int i = 0;
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack == null) {
                            i++;
                            continue;
                        }
                        int crystals = Utils.getCrystals(itemStack) * itemStack.getAmount();
                        if (crystals != 0) {
                            island.setCrystals(island.getCrystals() + crystals);
                            p.getInventory().clear(i);
                            TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.CRYSTALS, crystals));
                            return;
                        }
                        i++;
                    }
                } else if (e.getClick().equals(ClickType.LEFT)) {
                    if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                        if (island.getCrystals() > 0) {
                            if (p.getInventory().firstEmpty() != -1) {
                                island.setCrystals(island.getCrystals() - 1);
                                p.getInventory().addItem(Utils.getCrystals(1));
                                TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.CRYSTALS, -1));
                            } else {
                                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().inventoryFull
                                        .replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            }
                        }
                    }
                }
            }
            if (e.getSlot() == (IridiumSkyblock.getInventories().money.slot == null ? 15 : IridiumSkyblock.getInventories().money.slot)) {
                if (IridiumSkyblock.getInstance().getEconomy() != null) {
                    if (e.getClick().equals(ClickType.SHIFT_LEFT)) {
                        if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                            double depositValue = island.money;
                            TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.MONEY, -depositValue));
                            island.money = 0;
                            IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, depositValue);
                        }
                    } else if (e.getClick().equals(ClickType.SHIFT_RIGHT)) {
                        double playerBalance = IridiumSkyblock.getInstance().getEconomy().getBalance(p);
                        IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, playerBalance);
                        if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) == playerBalance) return;
                        island.money += playerBalance;
                        TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.MONEY, playerBalance));
                    } else if (e.getClick().equals(ClickType.RIGHT)) {
                        double playerBalance = IridiumSkyblock.getInstance().getEconomy().getBalance(p);
                        double depositValue = playerBalance > 1000 ? 1000 : playerBalance;
                        if (!(island.money > Double.MAX_VALUE - depositValue)) {
                            IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, depositValue);
                            if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) == playerBalance) return;
                            island.money += depositValue;
                            TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.MONEY, depositValue));
                        }
                    } else if (e.getClick().equals(ClickType.LEFT)) {
                        if ((island.getPermissions((u.islandID == island.getId() || island.isCoop(u.getIsland())) ? (island.isCoop(u.getIsland()) ? Role.Member : u.getRole()) : Role.Visitor).withdrawBank) || u.bypassing) {
                            if (island.money > 1000) {
                                island.money -= 1000;
                                IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, 1000);
                                TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.MONEY, -1000));
                            } else {
                                double depositAmount = island.money;
                                island.money = 0;
                                IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, depositAmount);
                                TransactionLogger.saveBankBalanceChange(p, new Transaction().add(TransactionType.MONEY, -depositAmount));
                            }
                        }
                    }
                }
            }
        }
    }
}
