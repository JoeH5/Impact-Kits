package me.impact.impact_kits.listeners;

import me.impact.impact_kits.Impact_Kits;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class LaunchPads implements Listener {

    private ArrayList<UUID> jumpers = new ArrayList<UUID>();

    public LaunchPads(Impact_Kits main)
    {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onLaunch(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Block block = location.getBlock().getRelative(BlockFace.DOWN);
        if(block.getType().equals(Material.SLIME_BLOCK))
        {
            //debug player.sendMessage("You've JUMPED!!");
            location.getWorld().createExplosion(location, 0.0f);
            event.getPlayer().setVelocity(player.getLocation().getDirection().multiply(4).add(new Vector(player.getVelocity().getX(), 2.5D, player.getVelocity().getZ())));
            if(!jumpers.contains(player.getUniqueId()))
            {
                if(!(player.getGameMode() == GameMode.CREATIVE)) {
                    jumpers.add(player.getUniqueId());
                }
//                else
//                {
//                    //debug player.sendMessage("in creative, not added");
//                }
            }
        }
        if(jumpers.contains(player.getUniqueId())) {
            if (block.getType().equals(Material.WATER)) {
                // debug player.sendMessage("SWIMMING AND REMOVED");
                jumpers.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player) {

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                Player player = (Player) event.getEntity();
                if(jumpers.contains(player.getUniqueId()))
                {
                    // debug player.sendMessage(player.getName() + "HAS LANDED!!");
                    event.setCancelled(true);
                    jumpers.remove(player.getUniqueId());
                }
            }
        }
    }
}
