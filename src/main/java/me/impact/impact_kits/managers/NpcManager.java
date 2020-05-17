package me.impact.impact_kits.managers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.impact.impact_kits.Impact_Kits;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class NpcManager {

    Impact_Kits main;

    public NpcManager(Impact_Kits instance)
    {
        this.main = instance;
    }

    private static List<EntityPlayer> NPC = new ArrayList<EntityPlayer>();
    private String npcName;


    public void createNPC(Player player, String name)
    {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) Bukkit.getWorld(player.getWorld().getName())).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        EntityPlayer npcEntity = new EntityPlayer(server, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        npcEntity.setLocation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), 0);
        String[] nameList = getSkin(player, name);
        gameProfile.getProperties().put("textures", new Property("textures", nameList[0], nameList[1]));
        addNpcPacket(npcEntity);
        NPC.add(npcEntity);

        String var = "NPC";
        if(main.getData().contains("data"))
        {
            var = var + Objects.requireNonNull(main.getData().getConfigurationSection("data")).getKeys(false).size();
        }

        main.getData().set("data." + var + ".x", (float) player.getLocation().getX());
        main.getData().set("data." + var + ".y", (float) player.getLocation().getY());
        main.getData().set("data." + var + ".z", (float) player.getLocation().getZ());
        main.getData().set("data." + var + ".p", player.getLocation().getPitch());
        main.getData().set("data." + var + ".yaw", player.getLocation().getYaw());
        main.getData().set("data." + var + ".world", player.getLocation().getWorld().getName());
        main.getData().set("data." + var + ".name", name);
        main.getData().set("data." + var + ".text", nameList[0]);
        main.getData().set("data." + var + ".signature", nameList[1]);
        main.saveData();

    }

    public void loadNPC(Location location, GameProfile profile)
    {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        EntityPlayer npcEntity = new EntityPlayer(server, worldServer, profile, new PlayerInteractManager(worldServer));
        npcEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);

        addNpcPacket(npcEntity);
        NPC.add(npcEntity);

    }

    public void addNpcPacket(EntityPlayer npcEntity)
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npcEntity));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npcEntity.getId(), (byte) ((npcEntity.yaw%360.)*256/360), (byte) ((npcEntity.pitch%360.)*256/360), false));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npcEntity, (byte) (npcEntity.yaw * 256/360)));

        }

    }

    public void removeNPC(Player player, EntityPlayer npc)
    {
        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
    }

    public void addJoinPacket(Player player)
    {
        for(EntityPlayer npcEntity : NPC)
        {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npcEntity));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npcEntity));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npcEntity.getId(), (byte) ((npcEntity.yaw%360.)*256/360), (byte) ((npcEntity.pitch%360.)*256/360), false));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npcEntity, (byte) (npcEntity.yaw * 256/360)));

        }
    }

    private String[] getSkin(Player player, String name)
    {
        try{
            URL userProfile = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(userProfile.openStream());
            String UUID = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            // url to get uuid
            URL sessionProfile = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(sessionProfile.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String textures = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {textures, signature};


        } catch(Exception ex) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            GameProfile gameProfile = entityPlayer.getProfile();
            Property property = gameProfile.getProperties().get("textures").iterator().next();
            String textures = property.getValue();
            String signature = property.getSignature();
            return new String[]{textures, signature};
        }

    }

    public List<EntityPlayer> getNPC()
    {
        return NPC;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }
}
