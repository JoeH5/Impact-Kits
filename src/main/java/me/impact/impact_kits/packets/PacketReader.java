package me.impact.impact_kits.packets;

import me.impact.impact_kits.Impact_Kits;
import me.impact.impact_kits.events.NpcClickEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PacketReader {

    Impact_Kits main;

    public PacketReader(Impact_Kits main)
    {
        this.main = main;
    }

    Channel channel;
    public Map<UUID, Channel> channels = new HashMap<UUID, Channel>();

    public void inject(Player player)
    {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;

        channels.put(player.getUniqueId(), channel);

        if(channel.pipeline().get("PacketInjector") != null)
        {
            return;
        }
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) throws Exception {
                arg.add(packet);
                readPacket(player, packet);
            }
        });
    }

    public void unInject(Player player)
    {
        channel = channels.get(player.getUniqueId());
        if(channel.pipeline().get("PacketInjector") != null)
        {
            channel.pipeline().remove("PacketInjector");
            channels.remove(player.getUniqueId());
        }
    }

    public void readPacket(Player player, Packet<?> packet)
    {
       // debug System.out.println("PACKET: " + packet);

        if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity"))
        {
            if(getValue(packet, "action").toString().equalsIgnoreCase("ATTACK"))
                return;
            if(getValue(packet, "d").toString().equalsIgnoreCase("OFF_HAND"))
                return;
            if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
                return;
            int id = (int) getValue(packet, "a");
            if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT"))
            {
                for(EntityPlayer npc : main.manager.getNPC())
                {
                    if(npc.getId() == id)
                    {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Impact_Kits.getPlugin(Impact_Kits.class), new Runnable() {
                            @Override
                            public void run() {
                                Bukkit.getPluginManager().callEvent(new NpcClickEvent(player, npc));

                            }
                        }, 0);
                    }
                }
            }

        }
    }

    private Object getValue(Object instance, String name)
    {
        Object result = null;

        try{
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);


        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
}
