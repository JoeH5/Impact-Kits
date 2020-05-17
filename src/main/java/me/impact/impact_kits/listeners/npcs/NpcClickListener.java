package me.impact.impact_kits.listeners.npcs;

import me.impact.impact_kits.Impact_Kits;
import me.impact.impact_kits.events.NpcClickEvent;
import me.impact.impact_kits.gui.NpcItemVendorGUI;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class NpcClickListener implements Listener {

    Impact_Kits main;
    public NpcClickListener(Impact_Kits instance)
    {
        this.main = instance;
        Bukkit.getServer().getPluginManager().registerEvents(this, instance);
    }

    public Inventory forSale;


    @EventHandler
    public void onClick(NpcClickEvent event)
    {
        Player player = event.getPlayer();

        if(event.getNPC().getName().equals("Merchant"))
        {
            createInventory();
            player.openInventory(forSale);
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(!event.getView().getTitle().contains("Items For Sale"))
        {
            return;
        }
        if(event.getCurrentItem() == null)
        {
            return;
        }
        if(event.getCurrentItem().getItemMeta() == null)
        {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(false);

        if(event.getClickedInventory().getType() == InventoryType.PLAYER)
        {
            return;
        }

        if(event.getSlot() == 12)
        {
            // diamond sword
            //TODO: if getLevel == 15 { can buy }
                //TODO: else send message(cannot buy)

            // add sword to inventory
            this.addItem(player, this.getDiamondSword());
            player.closeInventory();
            player.updateInventory();

        }
    }

    private void addItem(Player player, ItemStack[] itemStacks)
    {
        PlayerInventory inventory = player.getInventory();

        player.getInventory().addItem(itemStacks);
    }

    private ItemStack[] getDiamondSword()
    {
        return new ItemStack[]{new ItemStack(Material.DIAMOND_SWORD, 1)};
    }

    public void createInventory()
    {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.BLACK + "Items For Sale");
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Diamond Sword");
        List<String> itemDescription = new ArrayList<>();
        itemDescription.add("");
        itemDescription.add(ChatColor.BLUE + "Diamond Sword");
        itemDescription.add(ChatColor.YELLOW + "Click here to buy a temporary " + ChatColor.BLUE + ChatColor.ITALIC + ChatColor.UNDERLINE + "Diamond Sword");
        itemMeta.setLore(itemDescription);
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(itemMeta);
        inventory.setItem(12, item);

        forSale = inventory;

    }

}
