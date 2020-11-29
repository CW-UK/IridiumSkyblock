package com.iridium.iridiumskyblock;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.Utils.TransactionLogger.Transaction;
import com.iridium.iridiumskyblock.Utils.TransactionLogger.TransactionType;
import com.iridium.iridiumskyblock.configs.Inventories;
import com.iridium.iridiumskyblock.managers.IslandManager;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTListCompound;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    private static final boolean supports = XMaterial.supports(14);

    public static XMaterialItemId xMaterialItemId;

    static {
        {
            InputStream inputStream = IridiumSkyblock.getInstance().getResource("itemdata.json");
            Scanner sc = new Scanner(inputStream);
            //Reading line by line from scanner to StringBuffer
            StringBuffer content = new StringBuffer();
            while (sc.hasNext()) {
                content.append(sc.nextLine());
            }
            xMaterialItemId = IridiumSkyblock.getPersist().load(XMaterialItemId.class, content.toString());
        }
    }

    public static ItemStack makeItem(Material material, int amount, int type, String name, List<String> lore, Object object) {
        ItemStack item = new ItemStack(material, amount, (short) type);
        ItemMeta m = item.getItemMeta();
        m.setLore(lore);
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(XMaterial material, int amount, String name) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(XMaterial material, int amount, String name, List<String> lore) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static ItemStack makeItem(Inventories.Item item, List<Placeholder> placeholders) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(processMultiplePlaceholders(item.headOwner, placeholders));
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, processMultiplePlaceholders(item.title, placeholders), processMultiplePlaceholders(item.lore, placeholders));
        }
    }

    public static ItemStack makeItem(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, item.title, item.lore);
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, item.title, item.lore);
        }
    }

    public static ItemStack makeItem(Inventories.Item item, Island island) {
        try {
            ItemStack itemstack = makeItem(item.material, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItem(XMaterial.STONE, item.amount, processIslandPlaceholders(item.title, island), color(processIslandPlaceholders(item.lore, island)));
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item) {
        try {
            ItemStack itemstack = makeItemHidden(item.material, item.amount, item.title, item.lore);
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            return makeItemHidden(XMaterial.STONE, item.amount, item.title, item.lore);
        }
    }

    public static ItemStack makeItemHidden(Inventories.Item item, Island island) {
        return makeItemHidden(item, getIslandPlaceholders(island));
    }

    public static ItemStack makeItemHidden(Inventories.Item item, List<Placeholder> placeholders) {
        try {
            ItemStack itemstack = makeItemHidden(item.material, item.amount, processMultiplePlaceholders(item.title, placeholders), color(processMultiplePlaceholders(item.lore, placeholders)));
            if (item.material == XMaterial.PLAYER_HEAD && item.headData != null) {
                NBTItem nbtItem = new NBTItem(itemstack);
                NBTCompound skull = nbtItem.addCompound("SkullOwner");
                if (supports) {
                    skull.setUUID("Id", UUID.randomUUID());
                } else {
                    skull.setString("Id", UUID.randomUUID().toString());
                }
                NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
                texture.setString("Value", item.headData);
                return nbtItem.getItem();
            } else if (item.material == XMaterial.PLAYER_HEAD && item.headOwner != null) {
                SkullMeta m = (SkullMeta) itemstack.getItemMeta();
                m.setOwner(item.headOwner);
                itemstack.setItemMeta(m);
            }
            return itemstack;
        } catch (Exception e) {
            e.printStackTrace();
            return makeItemHidden(XMaterial.STONE, item.amount, processMultiplePlaceholders(item.title, placeholders), color(processMultiplePlaceholders(item.lore, placeholders)));
        }
    }

    public static ItemStack makeItemHidden(XMaterial material, int amount, String name, List<String> lore) {
        ItemStack item = material.parseItem();
        if (item == null) return null;
        item.setAmount(amount);
        ItemMeta m = item.getItemMeta();
        m.setLore(Utils.color(lore));
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(m);
        return item;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> color(List<String> strings) {
        return strings.stream().map(Utils::color).collect(Collectors.toList());
    }

    public static boolean isBlockValuable(Block b) {
        return IridiumSkyblock.getBlockValues().blockvalue.containsKey(XMaterial.matchXMaterial(b.getType())) || b.getState() instanceof CreatureSpawner || IridiumSkyblock.getConfiguration().limitedBlocks.containsKey(XMaterial.matchXMaterial(b.getType()));
    }

    public static boolean isBlockValuable(XMaterial material) {
        return IridiumSkyblock.getBlockValues().blockvalue.containsKey(material) || IridiumSkyblock.getConfiguration().limitedBlocks.containsKey(material);
    }

    public static List<Island> getTopIslands() {
        List<Island> islands = IslandManager.getLoadedIslands();
        islands.sort(Comparator.comparingDouble(Island::getValue));
        Collections.reverse(islands);
        return islands;
    }

    public static List<Island> getIslands() {
        List<Island> islands = IslandManager.getLoadedIslands();
        islands.sort(Comparator.comparingInt(Island::getVotes));
        Collections.reverse(islands);
        return islands;
    }

    public static boolean isSafe(Location loc, Island island) {
        if (loc == null) return false;
        if (loc.getY() < 1) return false;
        if (!island.isInIsland(loc)) return false;
        if (!loc.getBlock().getType().name().endsWith("AIR")) return false;
        if (loc.clone().add(0, -1, 0).getBlock().getType().name().endsWith("AIR"))
            return false;
        return !loc.clone().add(0, -1, 0).getBlock().isLiquid();
    }

    public static int getIslandRank(Island island) {
        int i = 1;
        for (Island is : getTopIslands()) {
            if (is.equals(island)) {
                return i;
            }
            i++;
        }
        return 0;
    }

    public static Location getNewHome(Island island, Location loc) {
        Block b;
        if (loc != null) {
            b = loc.getWorld().getHighestBlockAt(loc);
            while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                b = b.getLocation().clone().add(0, 1, 0).getBlock();
            }
            if (isSafe(b.getLocation(), island)) {
                return b.getLocation().add(0.5, 0, 0.5);
            }
        }

        for (double X = island.getPos1().getX(); X <= island.getPos2().getX(); X++) {
            for (double Z = island.getPos1().getZ(); Z <= island.getPos2().getZ(); Z++) {
                b = loc.getWorld().getHighestBlockAt((int) X, (int) Z);
                while (!XMaterial.matchXMaterial(b.getType()).name().endsWith("AIR")) {
                    b = b.getLocation().clone().add(0, 1, 0).getBlock();
                }
                if (isSafe(b.getLocation(), island)) {
                    return b.getLocation().add(0.5, 0, 0.5);
                }
            }
        }
        return null;
    }

    public static List<Placeholder> getIslandPlaceholders(Island island) {
        List<Placeholder> placeholders = new ArrayList<>(Arrays.asList(
                // Upgrades
                new Placeholder("sizevaultcost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membervaultcost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpvaultcost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("oresvaultcost", IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.containsKey(island.getOreLevel() + 1) ? IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.get(island.getOreLevel() + 1).vaultCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizecrystalscost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membercrystalscost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpcrystalscost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("orescrystalscost", IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.containsKey(island.getOreLevel() + 1) ? IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.get(island.getOreLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizecost", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.containsKey(island.getSizeLevel() + 1) ? IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("membercost", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.containsKey(island.getMemberLevel() + 1) ? IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("warpcost", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.containsKey(island.getWarpLevel() + 1) ? IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),
                new Placeholder("generatorcost", IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.containsKey(island.getOreLevel() + 1) ? IridiumSkyblock.getUpgrades().oresUpgrade.upgrades.get(island.getOreLevel() + 1).crystalsCost + "" : IridiumSkyblock.getMessages().maxlevelreached),

                new Placeholder("sizeblocks", IridiumSkyblock.getUpgrades().sizeUpgrade.upgrades.get(island.getSizeLevel()).size + ""),
                new Placeholder("membercount", IridiumSkyblock.getUpgrades().memberUpgrade.upgrades.get(island.getMemberLevel()).size + ""),
                new Placeholder("warpcount", IridiumSkyblock.getUpgrades().warpUpgrade.upgrades.get(island.getWarpLevel()).size + ""),

                new Placeholder("sizelevel", island.getSizeLevel() + ""),
                new Placeholder("memberlevel", island.getMemberLevel() + ""),
                new Placeholder("warplevel", island.getWarpLevel() + ""),
                new Placeholder("oreslevel", island.getOreLevel() + ""),
                new Placeholder("generatorlevel", island.getOreLevel() + ""),
                // Boosters
                new Placeholder("spawnerbooster", island.getSpawnerBooster() + ""),
                new Placeholder("farmingbooster", island.getFarmingBooster() + ""),
                new Placeholder("expbooster", island.getExpBooster() + ""),
                new Placeholder("flightbooster", island.getFlightBooster() + ""),

                new Placeholder("spawnerbooster_seconds", island.getSpawnerBooster() % 60 + ""),
                new Placeholder("farmingbooster_seconds", island.getFarmingBooster() % 60 + ""),
                new Placeholder("expbooster_seconds", island.getExpBooster() % 60 + ""),
                new Placeholder("flightbooster_seconds", island.getFlightBooster() % 60 + ""),
                new Placeholder("spawnerbooster_minutes", (int) Math.floor(island.getSpawnerBooster() / 60.00) + ""),
                new Placeholder("farmingbooster_minutes", (int) Math.floor(island.getFarmingBooster() / 60.00) + ""),
                new Placeholder("expbooster_minutes", (int) Math.floor(island.getExpBooster() / 60.00) + ""),
                new Placeholder("flightbooster_minutes", (int) Math.floor(island.getFlightBooster() / 60.00) + ""),
                new Placeholder("spawnerbooster_crystalcost", IridiumSkyblock.getBoosters().spawnerBooster.crystalsCost + ""),
                new Placeholder("farmingbooster_crystalcost", IridiumSkyblock.getBoosters().farmingBooster.crystalsCost + ""),
                new Placeholder("expbooster_crystalcost", IridiumSkyblock.getBoosters().experianceBooster.crystalsCost + ""),
                new Placeholder("flightbooster_crystalcost", IridiumSkyblock.getBoosters().flightBooster.crystalsCost + ""),
                new Placeholder("spawnerbooster_vaultcost", IridiumSkyblock.getBoosters().spawnerBooster.vaultCost + ""),
                new Placeholder("farmingbooster_vaultcost", IridiumSkyblock.getBoosters().farmingBooster.vaultCost + ""),
                new Placeholder("expbooster_vaultcost", IridiumSkyblock.getBoosters().experianceBooster.vaultCost + ""),
                new Placeholder("flightbooster_vaultcost", IridiumSkyblock.getBoosters().flightBooster.vaultCost + ""),

                //Bank
                new Placeholder("experience", island.getFormattedExp()),
                new Placeholder("crystals", island.getFormattedCrystals()),
                new Placeholder("money", island.getFormattedMoney()),
                new Placeholder("value", island.getFormattedValue())
        ));
        return placeholders;
    }

    public static String processIslandPlaceholders(String line, Island island) {
        return processMultiplePlaceholders(line, getIslandPlaceholders(island));
    }

    public static List<String> processIslandPlaceholders(List<String> lines, Island island) {
        List<String> newlist = new ArrayList<>();
        for (String string : lines) {
            newlist.add(processIslandPlaceholders(string, island));
        }
        return newlist;
    }

    public static List<String> processMultiplePlaceholders(List<String> lines, List<Placeholder> placeholders) {
        List<String> newlist = new ArrayList<>();
        for (String string : lines) {
            newlist.add(processMultiplePlaceholders(string, placeholders));
        }
        return newlist;
    }

    public static String processMultiplePlaceholders(String line, List<Placeholder> placeholders) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.process(line);
        }
        return color(line);
    }

    public static void pay(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        Island island = u.getIsland();
        if (island != null) {
            island.setCrystals(island.getCrystals() + crystals);
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                island.money += vault;
            } else {
                IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
            }
        } else {
            if (IridiumSkyblock.getInstance().getEconomy() == null) {
                IridiumSkyblock.getInstance().getLogger().warning("Vault plugin not found");
                return;
            }
            IridiumSkyblock.getInstance().getEconomy().depositPlayer(p, vault);
        }
        TransactionLogger.saveTransaction(p, new Transaction().add(TransactionType.MONEY, vault).add(TransactionType.CRYSTALS, crystals));
    }

    public static BuyResponce canBuy(Player p, double vault, int crystals) {
        User u = User.getUser(p);
        if (u.getIsland() != null) {
            if (u.getIsland().getCrystals() < crystals) return BuyResponce.NOT_ENOUGH_CRYSTALS;
            if (IridiumSkyblock.getInstance().getEconomy() != null) {
                if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault) {
                    IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                    u.getIsland().setCrystals(u.getIsland().getCrystals() - crystals);
                    TransactionLogger.saveTransaction(p, new Transaction().add(TransactionType.MONEY, -vault).add(TransactionType.CRYSTALS, -crystals));
                    return BuyResponce.SUCCESS;
                }
            }
            if (u.getIsland().money >= vault) {
                u.getIsland().money -= vault;
                u.getIsland().setCrystals(u.getIsland().getCrystals() - crystals);
                TransactionLogger.saveTransaction(p, new Transaction().add(TransactionType.MONEY, -vault).add(TransactionType.CRYSTALS, -crystals));
                return BuyResponce.SUCCESS;
            }
        }
        if (IridiumSkyblock.getInstance().getEconomy() != null) {
            if (IridiumSkyblock.getInstance().getEconomy().getBalance(p) >= vault && crystals == 0) {
                IridiumSkyblock.getInstance().getEconomy().withdrawPlayer(p, vault);
                TransactionLogger.saveTransaction(p, new Transaction().add(TransactionType.MONEY, -vault));
                return BuyResponce.SUCCESS;
            }
        }
        return crystals == 0 ? BuyResponce.NOT_ENOUGH_VAULT : BuyResponce.NOT_ENOUGH_CRYSTALS;
    }

    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        } else if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    public static int getTotalExperience(final Player player) {
        int exp = Math.round(getExpAtLevel(player.getLevel()) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    public static void setTotalExperience(final Player player, final int exp) {
        if (exp < 0) {
            throw new IllegalArgumentException("Experience is negative!");
        }
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = exp;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player.getLevel());
            amount -= expToLevel;
            if (amount >= 0) {
                // give until next level
                player.giveExp(expToLevel);
            } else {
                // give the rest
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    public static ItemStack getCrystals(int amount) {
        ItemStack itemStack = makeItemHidden(IridiumSkyblock.getInventories().crystal, Collections.singletonList(new Placeholder("amount", amount + "")));
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("crystals", amount);
        return nbtItem.getItem();
    }

    public static int getCrystals(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return 0;
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey("crystals")) {
            return nbtItem.getInteger("crystals");
        }
        return 0;
    }


    public static String getCurrentTimeStamp(Date date, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        return sdfDate.format(date);
    }

    public static Date getLocalDateTime(String time, String format) {
        SimpleDateFormat sdfDate = new SimpleDateFormat(format);//dd/MM/yyyy
        try {
            return sdfDate.parse(time);
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean hasOpenSlot(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            } else if (item.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public static XMaterial getXMaterialFromId(int id, byte data) {
        for (MaterialItemId materialItemId : xMaterialItemId.items) {
            if (materialItemId.type == id && materialItemId.meta == data) {
                return XMaterial.matchXMaterial(materialItemId.name).get();
            }
        }
        return null;
    }

    public static class XMaterialItemId {
        List<MaterialItemId> items;
    }

    public static class MaterialItemId {
        int type;
        byte meta;
        String name;
    }

    public static class Placeholder {

        private final String key;
        private final String value;

        public Placeholder(String key, String value) {
            this.key = "{" + key + "}";
            this.value = value;
        }

        public String process(String line) {
            if (line == null) return "";
            return line.replace(key, value);
        }
    }

    public static enum BuyResponce {
        SUCCESS,
        NOT_ENOUGH_CRYSTALS,
        NOT_ENOUGH_VAULT
    }

    public static class NumberFormatter {
        private static final String FORMAT = "%." + IridiumSkyblock.getConfiguration().numberAbbreviationDecimalPlaces + "f";
        private static final long ONE_THOUSAND_LONG = 1000;
        private static final long ONE_MILLION_LONG = 1000000;
        private static final long ONE_BILLION_LONG = 1000000000;

        private static final BigDecimal ONE_THOUSAND = new BigDecimal(1000);
        private static final BigDecimal ONE_MILLION = new BigDecimal(1000000);
        private static final BigDecimal ONE_BILLION = new BigDecimal(1000000000);

        public static String format(double number) {
            if (!IridiumSkyblock.getConfiguration().displayNumberAbbreviations) {
                return NumberFormat.getInstance().format(number);
            }
            return IridiumSkyblock.getConfiguration().prettierAbbreviations ? formatPrettyNumber(new BigDecimal(number)) : formatNumber(number);
        }

        private static String formatNumber(double number) {
            StringBuilder output = new StringBuilder();

            if (number < 0) {
                output.append("ERROR");
            } else if (number < ONE_THOUSAND_LONG) {
                output.append(String.format(FORMAT, number));
            } else if (number < ONE_MILLION_LONG) {
                output.append(String.format(FORMAT, number / ONE_THOUSAND_LONG)).append(IridiumSkyblock.getConfiguration().thousandAbbreviation);
            } else if (number < ONE_BILLION_LONG) {
                output.append(String.format(FORMAT, number / ONE_MILLION_LONG)).append(IridiumSkyblock.getConfiguration().millionAbbreviation);
            } else {
                output.append(String.format(FORMAT, number / ONE_BILLION_LONG)).append(IridiumSkyblock.getConfiguration().billionAbbreviation);
            }

            return output.toString();
        }

        private static String formatPrettyNumber(BigDecimal bigDecimal) {
            bigDecimal = bigDecimal.setScale(IridiumSkyblock.getConfiguration().numberAbbreviationDecimalPlaces, RoundingMode.HALF_DOWN);
            StringBuilder outputStringBuilder = new StringBuilder();

            if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                outputStringBuilder
                        .append("-")
                        .append(formatPrettyNumber(bigDecimal.negate()));
            } else if (bigDecimal.compareTo(ONE_THOUSAND) < 0) {
                outputStringBuilder
                        .append(bigDecimal.stripTrailingZeros().toPlainString());
            } else if (bigDecimal.compareTo(ONE_MILLION) < 0) {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_THOUSAND, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().thousandAbbreviation);
            } else if (bigDecimal.compareTo(ONE_BILLION) < 0) {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_MILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().millionAbbreviation);
            } else {
                outputStringBuilder
                        .append(bigDecimal.divide(ONE_BILLION, RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString())
                        .append(IridiumSkyblock.getConfiguration().billionAbbreviation);
            }

            return outputStringBuilder.toString();
        }
    }

    public static class TransactionLogger {
        public enum TransactionType {
            MONEY,
            CRYSTALS,
            EXPERIENCE;

            @Override
            public String toString() {
                return WordUtils.capitalize(name().toLowerCase());
            }
        }

        public static class Transaction {
            private final Map<TransactionType, Double> transactionAmounts = new HashMap<>();

            public Transaction add(TransactionType type, double amount) {
                transactionAmounts.put(type, amount);
                return this;
            }

            public double getTransactionAmount() {
                return transactionAmounts.values().stream().filter(value -> value != 0).findAny().orElse(0.0);
            }

            @Override
            public String toString() {
                String[] transactionParts = transactionAmounts.keySet().stream()
                        .filter(type -> transactionAmounts.get(type) != 0)
                        .map(type -> Math.abs(transactionAmounts.get(type)) + " " + type.toString())
                        .toArray(String[]::new);
                return String.join(", ", transactionParts);
            }

        }

        /**
         * @param transaction positive amount = sale, negative amount = purchase
         */
        public static void saveTransaction(Player player, Transaction transaction) {
            if (!IridiumSkyblock.getConfiguration().logTransactions) {
                return;
            }

            if (transaction.transactionAmounts.isEmpty()) {
                return;
            }

            if (transaction.getTransactionAmount() == 0) {
                return;
            }

            StringBuilder logEntry = new StringBuilder();
            addTimestamp(logEntry);
            addPlayerInfo(logEntry, player);
            logEntry.append(transaction.getTransactionAmount() < 0 ? "Purchased for " : "Sold for ");
            logEntry.append(transaction.toString());

            appendToFile(logEntry);
        }

        /**
         * @param transaction positive amount = deposit, negative amount = withdraw
         */
        public static void saveBankBalanceChange(Player player, Transaction transaction) {
            if (!IridiumSkyblock.getConfiguration().logBankBalanceChange) {
                return;
            }

            if (transaction.transactionAmounts.isEmpty()) {
                return;
            }

            if (transaction.getTransactionAmount() == 0) {
                return;
            }

            StringBuilder logEntry = new StringBuilder();
            addTimestamp(logEntry);
            addPlayerInfo(logEntry, player);
            logEntry.append(transaction.getTransactionAmount() < 0 ? "Withdrew " : "Deposited ");
            logEntry.append(transaction.toString());

            appendToFile(logEntry);
        }

        private static void addTimestamp(StringBuilder stringBuilder) {
            // Append: Year-Month-Day Hour:Minute:Second
            Date currentDate = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            stringBuilder.append("[").append(dateFormat.format(currentDate)).append("] ");
        }

        private static void addPlayerInfo(StringBuilder stringBuilder, Player player) {
            // Append: PlayerName (PlayerUUID):
            stringBuilder.append(player.getName()).append(" (").append(player.getUniqueId().toString()).append("): ");
        }

        private static void appendToFile(StringBuilder logEntry) {
            logEntry.append("\r\n");
            Path path = Paths.get("plugins", "IridiumSkyblock", "logs", "transactions.log");

            try {
                path.getParent().toFile().mkdirs();
                Files.write(path, logEntry.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

}
