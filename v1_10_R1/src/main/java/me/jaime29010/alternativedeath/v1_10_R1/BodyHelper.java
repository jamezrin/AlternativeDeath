package me.jaime29010.alternativedeath.v1_10_R1;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import me.jaime29010.alternativedeath.AbstractBodyHelper;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
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
        DataWatcherSerializer serializer = DataWatcherRegistry.a;
        DataWatcherObject object = serializer.a(10);
        watcher.set(object, handle.getDataWatcher().get(object));

        GameProfile profile = new GameProfile(craftPlayer.getUniqueId(), craftPlayer.getName());

        PacketPlayOutPlayerInfo packetInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        PacketPlayOutPlayerInfo.PlayerInfoData data = packetInfo.new PlayerInfoData(profile, 0, EnumGamemode.SURVIVAL, new ChatMessage(""));

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
        setValue(packetEntitySpawn, "h", watcher);

        PacketPlayOutBed packetBed = new PacketPlayOutBed();

        setValue(packetBed, "a", entityId);
        setValue(packetBed, "b", position);
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

            public boolean z() {
                return false;
            }
        };
        setValue(h, "id", entityId);
        return h.getDataWatcher();
    }
}