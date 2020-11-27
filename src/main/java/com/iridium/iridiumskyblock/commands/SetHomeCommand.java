package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetHomeCommand extends Command {

    public SetHomeCommand() {
        super(Collections.singletonList("sethome"), "Set your island home", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        User user = User.getUser(p);
        if (user.getIsland() != null) {
            if (Utils.isSafe(p.getLocation(), user.getIsland()) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                user.getIsland().setHome(p.getLocation());
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().setHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().isNotSafe.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        Player p = (Player) sender;
        if (island != null) {
            if (Utils.isSafe(p.getLocation(), island) && p.getLocation().getWorld().equals(IslandManager.getWorld())) {
                island.setHome(p.getLocation());
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().setHome.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            } else {
                p.sendMessage(Utils.color(IridiumSkyblock.getMessages().isNotSafe.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            p.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
