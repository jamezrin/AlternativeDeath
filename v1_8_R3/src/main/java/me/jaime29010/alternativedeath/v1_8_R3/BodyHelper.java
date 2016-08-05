package me.jaime29010.alternativedeath.v1_8_R3;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import me.jaime29010.alternativedeath.AbstractBodyHelper;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class BodyHelper extends AbstractBodyHelper {
    private int entityId = 0;

    @Override
    public void sendBody(Player player) {
        sendBody(player, new Location(player.getWorld(), player.getLocation().getBlockX(), 0, player.getLocation().getBlockZ()));
    }

    @Override
    public void sendBody(Player player, Location location) {
        PacketPlayOutNamedEntitySpawn packetEntitySpawn = new PacketPlayOutNamedEntitySpawn();

        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer handle = craftPlayer.getHandle();
        BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        double locY = craftPlayer.getHandle().locY;

        DataWatcher watcher = (DataWatcher) cloneDataWatcher(player, entityId);
        watcher.watch(10, craftPlayer.getHandle().getDataWatcher().getByte(10));

        GameProfile profile = new GameProfile(craftPlayer.getUniqueId(), craftPlayer.getName());

        PacketPlayOutPlayerInfo packetInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        PacketPlayOutPlayerInfo.PlayerInfoData data = packetInfo.new PlayerInfoData(profile, 0, WorldSettings.EnumGamemode.SURVIVAL, new ChatMessage(""));

        List<PacketPlayOutPlayerInfo.PlayerInfoData> dataList = Lists.newArrayList();
        dataList.add(data);
        setValue(packetInfo, "b", dataList);

        setValue(packetEntitySpawn, "a", entityId);
        setValue(packetEntitySpawn, "b", profile.getId());
        setValue(packetEntitySpawn, "c", MathHelper.floor(handle.locX * 32D));
        setValue(packetEntitySpawn, "d", MathHelper.floor(locY * 32D));
        setValue(packetEntitySpawn, "e", MathHelper.floor(handle.locZ * 32D));
        setValue(packetEntitySpawn, "f", (byte) ((int) (handle.yaw * 256.0F / 360.0F)));
        setValue(packetEntitySpawn, "g", (byte) ((int) (handle.pitch * 256.0F / 360.0F)));
        setValue(packetEntitySpawn, "i", watcher);

        PacketPlayOutBed packetBed = new PacketPlayOutBed();

        setValue(packetBed, "a", entityId);
        setValue(packetBed, "b", position);

        PacketPlayOutEntityTeleport packetTeleport = new PacketPlayOutEntityTeleport();
        setValue(packetTeleport, "a", entityId);
        setValue(packetTeleport, "b", MathHelper.floor(handle.locX * 32.0D));
        setValue(packetTeleport, "c", MathHelper.floor(locY * 32.0D));
        setValue(packetTeleport, "d", MathHelper.floor(handle.locZ * 32.0D));
        setValue(packetTeleport, "e", (byte) ((int) (handle.yaw * 256.0F / 360.0F)));
        setValue(packetTeleport, "f", (byte) ((int) (handle.pitch * 256.0F / 360.0F)));
        setValue(packetTeleport, "g", true);

        PacketPlayOutEntityTeleport packetTeleportDown = new PacketPlayOutEntityTeleport();
        setValue(packetTeleportDown, "a", entityId);
        setValue(packetTeleportDown, "b", MathHelper.floor(handle.locX * 32.0D));
        setValue(packetTeleportDown, "c", 0);
        setValue(packetTeleportDown, "d", MathHelper.floor(handle.locZ * 32.0D));
        setValue(packetTeleportDown, "e", (byte) ((int) (handle.yaw * 256.0F / 360.0F)));
        setValue(packetTeleportDown, "f", (byte) ((int) (handle.pitch * 256.0F / 360.0F)));
        setValue(packetTeleportDown, "g", true);

        for (Player other : Bukkit.getOnlinePlayers()) {
            other.sendBlockChange(location.clone().subtract(0, location.getY(), 0), Material.BED, (byte) 0);
            if (other != player) {
                PlayerConnection connection = ((CraftPlayer) other).getHandle().playerConnection;
                connection.sendPacket(packetInfo);
                connection.sendPacket(packetEntitySpawn);
                connection.sendPacket(packetTeleportDown);
                connection.sendPacket(packetBed);
                connection.sendPacket(packetTeleport);
            }
        }

        dataList.clear();
        entityId++;
    }

    @Override
    public Object cloneDataWatcher(Player player, int entityId) {
        EntityHuman h = new EntityHuman(((CraftWorld) player.getWorld()).getHandle(), ((CraftPlayer) player).getProfile()) {
            public void sendMessage(IChatBaseComponent arg0) {
                return;
            }

            public boolean a(int arg0, String arg1) {
                return false;
            }

            public BlockPosition getChunkCoordinates() {
                return null;
            }

            public boolean isSpectator() {
                return false;
            }
        };
        h.d(entityId);
        return h.getDataWatcher();
    }
}
