package com.iridium.iridiumskyblock.commands;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.Island;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ExpelCommand extends Command {
    public ExpelCommand() {
        super(Arrays.asList("expel", "visitors"), "Kick a visitor from your island", "", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 2) {
            sender.sendMessage(IridiumSkyblock.getConfiguration().prefix + "/is expel <player>");
            return;
        }
        Player p = (Player) sender;
        Island island = User.getUser(p).getIsland();
        if (island != null) {
            if (args.length == 1){
                p.openInventory(User.getUser(p).getIsland().getVisitorGUI().getInventory());
                return;
            }
            Player visitor = Bukkit.getPlayer(args[1]);
            if (visitor != null) {
                if (!island.equals(User.getUser(visitor).getIsland())) {
                    if (island.isInIsland(visitor.getLocation())) {
                        if (!(User.getUser(visitor).bypassing) && !visitor.hasPermission("iridiumskyblock.visitbypass")) {
                            island.spawnPlayer(visitor);
                            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().expelledVisitor.replace("%player%", visitor.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            visitor.sendMessage(Utils.color(IridiumSkyblock.getMessages().youHaveBeenExpelled.replace("%kicker%", p.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        } else {
                            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantExpelPlayer.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", visitor.getName())));
                        }
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantExpelMember.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%",visitor.getName())));
                }
            } else {
                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
            }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }
    @Override
    public void admin(CommandSender sender, String[] args, Island island) {
        if (args.length == 3) {
            Player p = (Player) sender;
            p.openInventory(island.getVisitorGUI().getInventory());
            return;
        }
        if (args.length != 4) {
            sender.sendMessage(Utils.color(IridiumSkyblock.getConfiguration().prefix) + "/is admin <island> expel <player>");
            return;
        }
        Player visitor = Bukkit.getPlayer(args[3]);
        if (island != null) {
            if (visitor != null) {
                if (!island.equals(User.getUser(visitor).getIsland())) {
                    if (island.isInIsland(visitor.getLocation())) {
                            if (!(User.getUser(visitor).bypassing)) {
                                island.spawnPlayer(visitor);
                                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().expelledVisitor.replace("%player%", visitor.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                                visitor.sendMessage(Utils.color(IridiumSkyblock.getMessages().youHaveBeenExpelled.replace("%kicker%", sender.getName()).replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                            } else {
                                sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantExpelPlayer.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%", visitor.getName())));
                            }
                        } else {
                            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().notInYourIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                        }
                    } else {
                        sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().cantExpelMember.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix).replace("%player%",visitor.getName())));
                    }
                } else {
                    sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().playerOffline.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
                }
        } else {
            sender.sendMessage(Utils.color(IridiumSkyblock.getMessages().noIsland.replace("%prefix%", IridiumSkyblock.getConfiguration().prefix)));
        }
    }

    @Override
    public List<String> TabComplete(CommandSender cs, org.bukkit.command.Command cmd, String s, String[] args) {
        return null;
    }
}
