package com.iridium.iridiumskyblock.nms;

import com.iridium.iridiumskyblock.Color;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public interface NMS {
    void setBlockFast(Block block, int blockId, byte data);
    void sendChunk(Player player, Chunk chunk);
    void sendWorldBorder(Player player, Color color, double size, Location centerLocation);
    void sendSubTitle(Player player, String message, int fadeIn, int displayTime, int fadeOut);
    void sendTitle(Player player, String message, int fadeIn, int displayTime, int fadeOut);
    void sendHologram(Player player, Location location, List<String> text);
    void removeHologram(Player player, Object hologram);
}
