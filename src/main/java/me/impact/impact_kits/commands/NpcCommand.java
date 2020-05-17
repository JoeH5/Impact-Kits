package me.impact.impact_kits.commands;

import me.impact.impact_kits.Impact_Kits;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import me.impact.impact_kits.managers.NpcManager;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class NpcCommand implements CommandExecutor
{

    public Impact_Kits main;

    public NpcCommand(Impact_Kits instance)
    {
        this.main = instance;
        main.getCommand("createnpc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {

            if (label.equalsIgnoreCase("createnpc")) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                if(args.length > 1)
                {
                    sender.sendMessage("Incorrect Usage!");
                    cmd.getUsage();
                }
                if(args.length == 0){
                    main.manager.createNPC(player, player.getName());
                    player.sendMessage("A NPC has been created at your position with your skin.");
                    return true;
                }
                main.manager.createNPC(player, args[0]);
                player.sendMessage("A NPC has been created at your position with the following name: " + args[0]);
                return true;
                }
        return false;
    }
}
