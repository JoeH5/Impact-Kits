package me.impact.impact_kits.managers.inventoryBridge;

import org.bukkit.inventory.Inventory;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;

public class inventorySave {
    public static String toBase64(Inventory inventory)
    {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(inventory.getSize());


            for(int i = 0; i < inventory.getSize(); i++)
            {
                dataOutput.writeObject(inventory.getItem(i));
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        }catch(Exception ex)
        {
            throw new IllegalStateException("cant save item stacks", ex);
        }
    }
}
