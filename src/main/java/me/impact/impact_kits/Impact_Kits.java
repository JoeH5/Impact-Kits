package me.impact.impact_kits;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.impact.impact_kits.commands.NpcCommand;
import me.impact.impact_kits.config.ConfigManager;
import me.impact.impact_kits.gui.NpcItemVendorGUI;
import me.impact.impact_kits.listeners.LaunchPads;
import me.impact.impact_kits.listeners.npcs.NpcClickListener;
import me.impact.impact_kits.listeners.npcs.NpcListener;
import me.impact.impact_kits.managers.NpcManager;
import me.impact.impact_kits.packets.PacketReader;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public final class Impact_Kits extends JavaPlugin implements Listener {

    public ConfigManager data;

    public NpcManager manager = new NpcManager(this);
    public PacketReader reader = new PacketReader(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.data = new ConfigManager(this);
        registerEvents();
        if(data.getConfig().contains("data")) {
            loadNPC();
        }
        packetUpdateOnEnable();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        packetUpdateOnDisable();

    }

    private void registerEvents()
    {
        new LaunchPads(this);
        new NpcCommand(this);
        new NpcListener(this);
        new NpcClickListener(this);
    }

    // if players on server resend packets on reload to ensure they receive them
    public void packetUpdateOnEnable()
    {
        if(!Bukkit.getOnlinePlayers().isEmpty())
        {
            for(Player player : Bukkit.getOnlinePlayers())
            {
                reader.inject(player);
            }
        }
    }

    public void packetUpdateOnDisable()
    {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                reader.unInject(player);
                for(EntityPlayer npc : manager.getNPC())
                {
                    manager.removeNPC(player, npc);
                }
            }
        }
    }

    public FileConfiguration getData()
    {
        return data.getConfig();
    }

    public void saveData()
    {
        this.data.saveConfig();
    }

    public void loadNPC()
    {
        FileConfiguration file = data.getConfig();
        Objects.requireNonNull(data.getConfig().getConfigurationSection("data")).getKeys(false).forEach(npc -> {
            Location location = new Location(Bukkit.getWorld(Objects.requireNonNull(file.getString("data." + npc + ".world"))), file.getInt("data." + npc + ".x"), file.getInt("data." + npc + ".y"), file.getInt("data." + npc + ".z"));
        location.setPitch((float) file.getDouble("data." + npc + ".p"));
        location.setPitch((float) file.getDouble("data." + npc + ".yaw"));

        String name = file.getString("data." + npc + ".name");
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        gameProfile.getProperties().put("textures", new Property("textures", file.getString("data." + npc + ".text"), file.getString("data." + npc + ".signature")));
        manager.loadNPC(location, gameProfile);
        });

    }


}
