package me.swipez.opanvil.enchants;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CustomEnchantManager {

    public static Integer getEnchantmentLength(ItemStack itemStack, String enchantment){
        int length = 0;
        if (itemStack.getItemMeta() != null){
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore != null){
                for (String string : lore){
                    if (string.toLowerCase().contains(enchantment.toLowerCase())){
                        length = string.length()-enchantment.length();
                        break;
                    }
                }
            }
        }
        return length;
    }

    public static Boolean isAppliedEnchantment(ItemStack itemStack, String enchantment){
        boolean bool = false;
        if (itemStack.getItemMeta() != null){
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore != null){
                for (String string : lore){
                    if (string.toLowerCase().contains(enchantment.toLowerCase())){
                        bool = true;
                        break;
                    }
                }
            }
        }
        return bool;
    }


}
