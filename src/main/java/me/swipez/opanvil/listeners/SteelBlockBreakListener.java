package me.swipez.opanvil.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SteelBlockBreakListener implements Listener {
    @EventHandler
    public void onPlayerBreaksBlock(BlockBreakEvent event){
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null){
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore() == null){
            return;
        }
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        List<String> lore = itemStack.getItemMeta().getLore();
        boolean hasSteel = false;
        int steelLevel = 0;
        for (String string : lore){
            if (string.toLowerCase().contains("steel")){
                String[] split = string.split(" ");
                steelLevel = Integer.parseInt(split[1]);
                hasSteel = true;
            }
        }
        if (hasSteel){
            if (steelLevel == 1){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_NUGGET));
            }
            if (steelLevel == 2){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_NUGGET, 2));
            }
            if (steelLevel == 3){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_NUGGET, 3));
            }
            if (steelLevel == 4){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_NUGGET, 7));
            }
            if (steelLevel == 5){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
            }
        }
    }
}
