package com.iridium.iridiumskyblock.configs;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.iridiumskyblock.MissionType;
import org.bukkit.CropState;

import java.util.*;

public class Missions {

    public List<Mission> missions = Arrays.asList(
            new Mission("Treasure Hunter", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(5, 1000, 100, MissionType.EXPERIENCE, Collections.emptyList(), Collections.emptyList()));
                put(2, new MissionData(10, 5000, 1000, MissionType.EXPERIENCE, Collections.emptyList(), Collections.emptyList()));
                put(3, new MissionData(15, 10000, 10000, MissionType.EXPERIENCE, Collections.emptyList(), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.EXPERIENCE_BOTTLE, 10, 1, "&b&lTreasure Hunter Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bCollect {amount} Experience", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Competitor", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(5, 10000, 100, MissionType.VALUE_INCREASE, Collections.emptyList(), Collections.emptyList()));
                put(2, new MissionData(10, 10000, 500, MissionType.VALUE_INCREASE, Collections.emptyList(), Collections.emptyList()));
                put(3, new MissionData(15, 10000, 1000, MissionType.VALUE_INCREASE, Collections.emptyList(), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 5000, MissionType.VALUE_INCREASE, Collections.emptyList(), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 10000, MissionType.VALUE_INCREASE, Collections.emptyList(), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.GOLD_INGOT, 11, 1, "&b&lCompetitor Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bGain {amount} Island Value", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Miner", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(1, 10000, 50, MissionType.BLOCK_BREAK, Arrays.asList(XMaterial.COAL_ORE.name(), XMaterial.DIAMOND_ORE.name(), XMaterial.EMERALD_ORE.name(), XMaterial.GOLD_ORE.name(), XMaterial.IRON_ORE.name(), XMaterial.LAPIS_ORE.name(), XMaterial.NETHER_QUARTZ_ORE.name(), XMaterial.REDSTONE_ORE.name()), Collections.emptyList()));
                put(2, new MissionData(5, 10000, 100, MissionType.BLOCK_BREAK, Arrays.asList(XMaterial.COAL_ORE.name(), XMaterial.DIAMOND_ORE.name(), XMaterial.EMERALD_ORE.name(), XMaterial.GOLD_ORE.name(), XMaterial.IRON_ORE.name(), XMaterial.LAPIS_ORE.name(), XMaterial.NETHER_QUARTZ_ORE.name(), XMaterial.REDSTONE_ORE.name()), Collections.emptyList()));
                put(3, new MissionData(10, 10000, 500, MissionType.BLOCK_BREAK, Arrays.asList(XMaterial.COAL_ORE.name(), XMaterial.DIAMOND_ORE.name(), XMaterial.EMERALD_ORE.name(), XMaterial.GOLD_ORE.name(), XMaterial.IRON_ORE.name(), XMaterial.LAPIS_ORE.name(), XMaterial.NETHER_QUARTZ_ORE.name(), XMaterial.REDSTONE_ORE.name()), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 1000, MissionType.BLOCK_BREAK, Arrays.asList(XMaterial.COAL_ORE.name(), XMaterial.DIAMOND_ORE.name(), XMaterial.EMERALD_ORE.name(), XMaterial.GOLD_ORE.name(), XMaterial.IRON_ORE.name(), XMaterial.LAPIS_ORE.name(), XMaterial.NETHER_QUARTZ_ORE.name(), XMaterial.REDSTONE_ORE.name()), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 2000, MissionType.BLOCK_BREAK, Arrays.asList(XMaterial.COAL_ORE.name(), XMaterial.DIAMOND_ORE.name(), XMaterial.EMERALD_ORE.name(), XMaterial.GOLD_ORE.name(), XMaterial.IRON_ORE.name(), XMaterial.LAPIS_ORE.name(), XMaterial.NETHER_QUARTZ_ORE.name(), XMaterial.REDSTONE_ORE.name()), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.DIAMOND_ORE, 12, 1, "&b&lMiner Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bDestroy {amount} Ores", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Farmer", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(1, 10000, 50, MissionType.BLOCK_BREAK, Arrays.asList(CropState.RIPE.toString(), XMaterial.MELON.name(), XMaterial.CACTUS.name(), XMaterial.PUMPKIN.name(), XMaterial.SUGAR_CANE.name()), Collections.emptyList()));
                put(2, new MissionData(5, 10000, 100, MissionType.BLOCK_BREAK, Arrays.asList(CropState.RIPE.toString(), XMaterial.MELON.name(), XMaterial.CACTUS.name(), XMaterial.PUMPKIN.name(), XMaterial.SUGAR_CANE.name()), Collections.emptyList()));
                put(3, new MissionData(10, 10000, 500, MissionType.BLOCK_BREAK, Arrays.asList(CropState.RIPE.toString(), XMaterial.MELON.name(), XMaterial.CACTUS.name(), XMaterial.PUMPKIN.name(), XMaterial.SUGAR_CANE.name()), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 1000, MissionType.BLOCK_BREAK, Arrays.asList(CropState.RIPE.toString(), XMaterial.MELON.name(), XMaterial.CACTUS.name(), XMaterial.PUMPKIN.name(), XMaterial.SUGAR_CANE.name()), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 5000, MissionType.BLOCK_BREAK, Arrays.asList(CropState.RIPE.toString(), XMaterial.MELON.name(), XMaterial.CACTUS.name(), XMaterial.PUMPKIN.name(), XMaterial.SUGAR_CANE.name()), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.SUGAR_CANE, 13, 1, "&b&lFarmer Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bHarvest {amount} Crops", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Hunter", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(1, 10000, 10, MissionType.ENTITY_KILL, Collections.emptyList(), Collections.emptyList()));
                put(2, new MissionData(5, 10000, 50, MissionType.ENTITY_KILL, Collections.emptyList(), Collections.emptyList()));
                put(3, new MissionData(10, 10000, 100, MissionType.ENTITY_KILL, Collections.emptyList(), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 500, MissionType.ENTITY_KILL, Collections.emptyList(), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 1000, MissionType.ENTITY_KILL, Collections.emptyList(), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.BLAZE_POWDER, 14, 1, "&b&lHunter Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bKill {amount} Mobs", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Fisherman", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(1, 10000, 5, MissionType.FISH_CATCH, Collections.emptyList(), Collections.emptyList()));
                put(2, new MissionData(5, 10000, 10, MissionType.FISH_CATCH, Collections.emptyList(), Collections.emptyList()));
                put(3, new MissionData(10, 10000, 50, MissionType.FISH_CATCH, Collections.emptyList(), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 100, MissionType.FISH_CATCH, Collections.emptyList(), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 500, MissionType.FISH_CATCH, Collections.emptyList(), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.FISHING_ROD, 15, 1, "&b&lFisherman Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bCatch {amount} Fish", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards."))),

            new Mission("Builder", new HashMap<Integer, MissionData>() {{
                put(1, new MissionData(1, 10000, 100, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
                put(2, new MissionData(5, 10000, 500, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
                put(3, new MissionData(10, 10000, 1000, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
                put(4, new MissionData(15, 10000, 5000, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
                put(5, new MissionData(15, 10000, 10000, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
                put(6, new MissionData(15, 10000, 50000, MissionType.BLOCK_PLACE, Collections.emptyList(), Collections.emptyList()));
            }}, new Inventories.Item(XMaterial.COBBLESTONE, 16, 1, "&b&lBuilder Level {level}", Arrays.asList("&7Complete island missions to gain crystals", "&7that can be spent on Boosters and Upgrades.", "", "&b&lInformation:", "&b&l * &7Objective: &bPlace {amount} Blocks", "&b&l * &7Current Status: &b{status}", "&b&l * &7Reward: &b{crystalsReward} Island Crystals and ${vaultReward}", "", "&b&l[!] &bComplete this mission for rewards.")))

    );

    public static class Mission {
        public String name;
        public Map<Integer, MissionData> levels;
        public Inventories.Item item;

        public Mission(String name, Map<Integer, MissionData> levels, Inventories.Item item) {
            this.name = name;
            this.levels = levels;
            this.item = item;
        }
    }

    public static class MissionData {
        public int crystalReward;
        public int vaultReward;
        public int amount;
        public MissionType type;
        public List<String> conditions;
        public List<String> commandReward;

        public MissionData(int crystalReward, int vaultReward, int amount, MissionType type, List<String> conditions, List<String> commandReward) {
            this.crystalReward = crystalReward;
            this.vaultReward = vaultReward;
            this.amount = amount;
            this.type = type;
            this.conditions = conditions;
            this.commandReward = commandReward;
        }
    }
}
