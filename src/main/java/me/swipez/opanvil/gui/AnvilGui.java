package me.swipez.opanvil.gui;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.items.ItemButtons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AnvilGui {

    public static Inventory createInventory(Player player, int level, int placedItems){
        String name = ChatColor.RED.toString()+"An Overpowered Anvil";
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.BOLD+name);
        inventory.setItem(1, ItemButtons.COAL_PROGRESS);
        inventory.setItem(7, ItemButtons.DIAMOND_PROGRESS);
        inventory.setItem(19, generateProperProgress(level, placedItems, player));
        inventory.setItem(22, ItemButtons.ANVIL_APPLY);
        inventory.setItem(25, generateProperRename(player));
        int selectedLevel = 0;
        for (int i = 2; i <= 6; i++){
            selectedLevel++;
            if (level+1 < i){
                inventory.setItem(i, generateLockedLevelItem(selectedLevel));
            }
            else {
                inventory.setItem(i, generateLevelItem(selectedLevel));
            }
        }
        generateIfCondition(ItemButtons.MAX_ENCHANTS, 1, level, inventory, 36);
        generateIfCondition(ItemButtons.ENCHANT_RANDOMIZER, 2, level, inventory, 38);
        generateIfCondition(ItemButtons.ADDITIVE_ENCHANTS, 3, level, inventory, 40);
        generateIfCondition(ItemButtons.NETHERITE_PLATING, 4, level, inventory, 42);
        generateIfCondition(ItemButtons.ENCHANT_MULTIPLIER, 5, level, inventory, 44);
        fillInventory(ItemButtons.EMPTY_FILL, inventory);
        inventory.clear(21);
        inventory.clear(23);
        // Need to fix the second one manually for some reason, the math on it is correct so? /shrug
        if (level == 0){
            inventory.setItem(38, generateSecondFix());
        }

        return inventory;
    }

    public static ItemStack generateProperRename(Player player){
        ItemStack itemStack = ItemButtons.RENAME.clone();
        List<String> lore = new ArrayList<>();
        ItemMeta meta = itemStack.getItemMeta();

        String name = OPAnvil.selectedName.get(player.getUniqueId());

        if (name == null){
            name = "(unselected)";
        }

        lore.add(ChatColor.GRAY+"Left click to pick a name, Right click to clear name");
        lore.add(" ");
        lore.add(ChatColor.GRAY+"Current name: "+ChatColor.RED+name);

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public static void fillInventory(ItemStack fillItem, Inventory inventory){
        for (int i = 0; i < inventory.getSize(); i++){
            if (inventory.getItem(i) == null){
                inventory.setItem(i, fillItem);
            }
        }
    }

    public static ItemStack generateLevelItem(int level){
        ItemStack glassPane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Level "+level);
        glassPane.setItemMeta(meta);

        return glassPane;
    }

    public static ItemStack generateLockedLevelItem(int level){
        ItemStack glassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = glassPane.getItemMeta();
        meta.setDisplayName(ChatColor.RED+"Level "+level);
        glassPane.setItemMeta(meta);

        return glassPane;
    }

    public static ItemStack generateProperProgress(int level, int placedItems, Player player){
        ItemStack defaultItem = ItemButtons.PROGRESS;
        ItemMeta meta = defaultItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        String requiredCount = null;
        String requiredItem = null;
        if (level+1 == 1){
            requiredCount = "64";
            requiredItem = "Iron Nuggets";
            OPAnvil.requiredItem.put(player.getUniqueId(), Material.IRON_NUGGET);
        }
        if (level+1 == 2){
            requiredCount = "64";
            requiredItem = "Iron Ingots";
            OPAnvil.requiredItem.put(player.getUniqueId(), Material.IRON_INGOT);
        }
        if (level+1 == 3){
            requiredCount = "15";
            requiredItem = "Iron Blocks";
            OPAnvil.requiredItem.put(player.getUniqueId(), Material.IRON_BLOCK);
        }
        if (level+1 == 4){
            requiredCount = "30";
            requiredItem = "Iron Blocks";
            OPAnvil.requiredItem.put(player.getUniqueId(), Material.IRON_BLOCK);
        }
        if (level+1 == 5){
            requiredCount = "64";
            requiredItem = "Iron Blocks";
            OPAnvil.requiredItem.put(player.getUniqueId(), Material.IRON_BLOCK);
        }
        if (level >= 5){
            lore.add(ChatColor.GRAY+"You are max level!");
            meta.setLore(lore);
            defaultItem.setItemMeta(meta);
            return defaultItem;
        }
        if (Integer.valueOf(placedItems) >= Integer.valueOf(requiredCount)){
            OPAnvil.anvilLevel.put(player.getUniqueId(), OPAnvil.anvilLevel.get(player.getUniqueId())+1);
            OPAnvil.putInItems.put(player.getUniqueId(), 0);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            return generateProperProgress(OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId()), player);
        }
        String initialDisplay = ChatColor.GRAY+"Level "+(level+1)+" requires "+requiredCount+" "+requiredItem+"!";
        String actualCount = ChatColor.GREEN+""+placedItems+"/"+requiredCount+" "+requiredItem;

        lore.add(initialDisplay);
        lore.add(actualCount);

        meta.setLore(lore);
        defaultItem.setItemMeta(meta);

        return defaultItem;
    }

    public static void generateIfCondition(ItemStack itemStackOriginal, int requiredLevel, int currentLevel, Inventory inventory, int slot){
        ItemStack itemStack = itemStackOriginal.clone();
        ItemMeta meta = itemStack.getItemMeta();
        List<String> emptyLore = new ArrayList<>();

        if (requiredLevel <= currentLevel){
            inventory.setItem(slot+9, generateLockedLevelItem(false, requiredLevel));
        }
        else if (requiredLevel - currentLevel == 1){
            meta.setLore(emptyLore);
            meta.setDisplayName(ChatColor.RED+ChatColor.BOLD.toString()+"(LOCKED)");
            inventory.setItem(slot+9, generateLockedLevelItem(true, requiredLevel));
        }
        else {
            meta.setDisplayName(ChatColor.RED+ChatColor.BOLD.toString()+"(LOCKED)");
            meta.setLore(emptyLore);
            itemStack.setType(Material.BARRIER);
            inventory.setItem(slot+9, generateLockedLevelItem(true, requiredLevel));
        }

        itemStack.setItemMeta(meta);
        inventory.setItem(slot, itemStack);
    }

    public static ItemStack generateLockedLevelItem(boolean locked, int requiredLevel){
        ItemStack glassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        if (locked){
            ItemMeta meta = glassPane.getItemMeta();
            meta.setDisplayName(ChatColor.RED+"This ability is unlocked at Level "+requiredLevel);
            glassPane.setItemMeta(meta);
        }
        else {
            glassPane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            ItemMeta meta = glassPane.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"Unlocked");
            glassPane.setItemMeta(meta);
        }
        return glassPane;
    }

    public static ItemStack generateSecondFix(){
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RED+ChatColor.BOLD.toString()+"(LOCKED)");
        meta.setLore(new ArrayList<>());
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
