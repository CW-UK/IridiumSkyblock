package com.iridium.iridiumskyblock.nms;

import com.iridium.iridiumskyblock.Color;
import com.iridium.iridiumskyblock.User;
import com.iridium.iridiumskyblock.Utils;
import com.iridium.iridiumskyblock.managers.IslandManager;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class v1_9_R2 implements NMS {
    @Override
    public void setBlockFast(Block block, int blockId, byte data) {
        net.minecraft.server.v1_9_R2.World nmsWorld = ((org.bukkit.craftbukkit.v1_9_R2.CraftWorld) block.getWorld()).getHandle();
        net.minecraft.server.v1_9_R2.BlockPosition bp = new net.minecraft.server.v1_9_R2.BlockPosition(block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ());
        net.minecraft.server.v1_9_R2.IBlockData ibd = net.minecraft.server.v1_9_R2.Block.getByCombinedId(blockId + (data << 12));
        nmsWorld.setTypeAndData(bp, ibd, 2);
    }

    @Override
    public void sendChunk(Player player, Chunk chunk) {
        PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(), 65535);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutMapChunk);
    }

    @Override
    public void sendWorldBorder(Player player, Color color, double size, Location centerLocation) {
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.world = ((CraftWorld) centerLocation.getWorld()).getHandle();

        if (centerLocation.getWorld().equals(IslandManager.getNetherWorld())) {
            worldBorder.setCenter(8 * (centerLocation.getBlockX() + 0.5), 8 * (centerLocation.getBlockZ() + 0.5));
        } else {
            worldBorder.setCenter(centerLocation.getBlockX() + 0.5, centerLocation.getBlockZ() + 0.5);
        }

        if (color == Color.Off) {
            worldBorder.setSize(Integer.MAX_VALUE);
        } else {
            worldBorder.setSize(size);
        }

        worldBorder.setWarningDistance(0);
        worldBorder.setWarningTime(0);

        if (color == Color.Red) {
            worldBorder.transitionSizeBetween(size, size - 1.0D, 20000000L);
        } else if (color == Color.Green) {
            worldBorder.transitionSizeBetween(size - 0.1D, size, 20000000L);
        }
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
    }

    @Override
    public void sendSubTitle(Player player, String message, int fadeIn, int displayTime, int fadeOut) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\":\"" + message + "\"}"));
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, iChatBaseComponent, fadeIn, displayTime, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
    }

    @Override
    public void sendTitle(Player player, String message, int fadeIn, int displayTime, int fadeOut) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\":\"" + message + "\"}"));
        PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, iChatBaseComponent, fadeIn, displayTime, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
    }

    @Override
    public void sendHologram(Player player, Location location, List<String> text) {
        location = location.add(0, 0.4 * (text.size() - 1), 0);
        User user = User.getUser(player);
        CraftWorld craftWorld = (CraftWorld) location.getWorld();
        for (int i = -1; ++i < text.size(); ) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ());

            entityArmorStand.setInvisible(true);
            entityArmorStand.setCustomNameVisible(true);
            entityArmorStand.setCustomName(Utils.color(text.get(i)));

            user.addHologram(entityArmorStand);

            PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
            location = location.subtract(0, 0.4, 0);
        }
    }

    @Override
    public void removeHologram(Player player, Object hologram) {
        EntityArmorStand entityArmorStand = (EntityArmorStand) hologram;
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
    }
}
