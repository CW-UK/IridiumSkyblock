package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.configs.Missions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class MissionsGUI extends GUI implements Listener {

    public MissionsGUI(Island island) {
        super(island, IridiumSkyblock.getInventories().missionsGUISize, IridiumSkyblock.getInventories().missionsGUITitle);
        IridiumSkyblock.getInstance().registerListeners(this);
    }

    @Override
    public void addContent() {
        super.addContent();
        if (getInventory().getViewers().isEmpty()) return;
        if (getIsland()!=null) {
            Island island = getIsland();
            for (Missions.Mission mission : IridiumSkyblock.getMissions().missions) {
                List<Utils.Placeholder> placeholderList = Utils.getIslandPlaceholders(island);

                if (!island.getMissionLevels().containsKey(mission.name))
                    island.getMissionLevels().put(mission.name, 1);
                
                Missions.MissionData data = mission.levels.get(island.getMissionLevels().get(mission.name));

                placeholderList.add(new Utils.Placeholder("level", island.getMissionLevels().get(mission.name) + ""));
                placeholderList.add(new Utils.Placeholder("vaultReward", data.vaultReward + ""));
                placeholderList.add(new Utils.Placeholder("crystalsReward", data.crystalReward + ""));
                placeholderList.add(new Utils.Placeholder("amount", data.amount + ""));
                placeholderList.add(new Utils.Placeholder("status", island.getMission(mission.name) == Integer.MIN_VALUE ? IridiumSkyblock.getMessages().completed : island.getMission(mission.name) + "/" + data.amount + ""));
                setItem(mission.item.slot, Utils.makeItemHidden(mission.item, placeholderList));
            }
            if (IridiumSkyblock.getInventories().backButtons) setItem(getInventory().getSize() - 5, Utils.makeItem(IridiumSkyblock.getInventories().back));
        }
    }

    @EventHandler
    @Override
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(getInventory())) {
            e.setCancelled(true);
            if (e.getSlot() == getInventory().getSize() - 5 && IridiumSkyblock.getInventories().backButtons) {
                e.getWhoClicked().openInventory(getIsland().getIslandMenuGUI().getInventory());
            }
        }
    }
}
