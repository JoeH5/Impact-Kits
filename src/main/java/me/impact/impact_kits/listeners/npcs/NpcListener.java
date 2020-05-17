package me.impact.impact_kits.listeners.npcs;

import me.impact.impact_kits.Impact_Kits;
import me.impact.impact_kits.managers.NpcManager;
import me.impact.impact_kits.packets.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NpcListener implements Listener {

    Impact_Kits main;

    public NpcListener(Impact_Kits main)
    {
        this.main = main;
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        main.reader.inject(event.getPlayer());
        if(main.manager.getNPC() == null)
        {
            return;
        }
        if(main.manager.getNPC().isEmpty())
        {
            return;
        }
        main.manager.addJoinPacket(event.getPlayer());

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        main.reader.unInject(event.getPlayer());
    }
}
