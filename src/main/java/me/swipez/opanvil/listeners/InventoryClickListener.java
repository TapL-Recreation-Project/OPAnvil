package me.swipez.opanvil.listeners;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.gui.AnvilGui;
import me.swipez.opanvil.items.ItemButtons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.NumberFormat;
import java.util.*;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onPlayerOpenAnvil(InventoryOpenEvent event){
        if (event.getInventory().getType().equals(InventoryType.ANVIL)){
            event.setCancelled(true);
            Player player = (Player) event.getPlayer();
            OPAnvil.selectedItem.remove(player.getUniqueId());
            OPAnvil.anvilLevel.putIfAbsent(player.getUniqueId(), 0);
            OPAnvil.putInItems.putIfAbsent(player.getUniqueId(), 0);
            int level = OPAnvil.anvilLevel.get(player.getUniqueId());
            int storedItems = OPAnvil.putInItems.get(player.getUniqueId());
            event.getPlayer().openInventory(AnvilGui.createInventory(player, level, storedItems));
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event){
        if (event.getView().getTitle().toLowerCase().contains("overpowered")){
            ItemStack itemStack = event.getInventory().getItem(21);
            ItemStack otherItem = event.getInventory().getItem(23);
            Player player = (Player) event.getPlayer();
            if (itemStack != null){
                for (Map.Entry<Integer, ItemStack> entry : player.getInventory().addItem(
                        itemStack
                ).entrySet()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue());
                }
            }
            if (otherItem != null){
                for (Map.Entry<Integer, ItemStack> entry : player.getInventory().addItem(
                        otherItem
                ).entrySet()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEnchant(EnchantItemEvent event){
        ItemStack itemStack = event.getItem();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                convertToLore(itemStack);
            }
        }.runTaskLater(OPAnvil.plugin, 1);
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        if (!event.getView().getTitle().toLowerCase().contains("overpowered")) {
            return;
        }
        if (event.getClickedInventory().contains(ItemButtons.COAL_PROGRESS) && event.getSlot() != 21 && event.getSlot() != 23) {
            event.setCancelled(true);
        }
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (clickedItem == null) {
            return;
        }
        if (clickedItem.getItemMeta().getDisplayName().toLowerCase().contains("progress")) {
            List<String> lore = clickedItem.getItemMeta().getLore();
            if (lore.size() > 1) {
                String importantString = lore.get(1).replace("§a", "");
                String[] otherImportant = importantString.split("/");
                int firstNumber = Integer.parseInt(otherImportant[0]);
                String[] secondNumber = otherImportant[1].split(" ");
                int realSecondNumber = Integer.parseInt(secondNumber[0]);

                int difference = realSecondNumber - firstNumber;
                OPAnvil.anvilLevel.putIfAbsent(player.getUniqueId(), 0);
                OPAnvil.putInItems.putIfAbsent(player.getUniqueId(), 0);
                if (player.getItemOnCursor().getType().equals(OPAnvil.requiredItem.get(player.getUniqueId()))) {
                    ItemStack heldItems = player.getItemOnCursor();
                    Material material = heldItems.getType();
                    int initialCount = heldItems.getAmount();
                    heldItems.setAmount(heldItems.getAmount() - difference);
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
                    int secondCount = heldItems.getAmount();
                    OPAnvil.putInItems.put(player.getUniqueId(), OPAnvil.putInItems.get(player.getUniqueId()) + (initialCount - secondCount));
                    heldItems.setAmount(0);
                    player.openInventory((AnvilGui.createInventory(player, OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId()))));
                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.openInventory(AnvilGui.createInventory(player, OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId())));
                            player.setItemOnCursor(new ItemStack(material, secondCount));
                        }
                    }.runTaskLater(OPAnvil.plugin, 1);
                }
            }
        }
        String name = clickedItem.getItemMeta().getDisplayName();
        if (name.equals(ItemButtons.MAX_ENCHANTS.getItemMeta().getDisplayName()) || name.equals(ItemButtons.ENCHANT_RANDOMIZER.getItemMeta().getDisplayName()) || name.equals(ItemButtons.ADDITIVE_ENCHANTS.getItemMeta().getDisplayName()) || name.equals(ItemButtons.NETHERITE_PLATING.getItemMeta().getDisplayName()) || name.equals(ItemButtons.ENCHANT_MULTIPLIER.getItemMeta().getDisplayName())) {
            if (OPAnvil.selectedItem.get(player.getUniqueId()) == null || !OPAnvil.selectedItem.get(player.getUniqueId()).equals(clickedItem.getType())) {
                setSelectedItem(clickedItem, player, event.getClickedInventory());
            } else {
                removeSelectedItem(player, clickedItem);
            }
        }
        if (name.equals(ItemButtons.RENAME.getItemMeta().getDisplayName())){
            if (event.getClick().isLeftClick()){
                OPAnvil.currentlyNaming.add(player.getUniqueId());
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD+"Type the new name of your item in chat!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            }
            if (event.getClick().isRightClick()){
                OPAnvil.selectedName.remove(player.getUniqueId());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.3F);
                player.openInventory((AnvilGui.createInventory(player, OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId()))));
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.openInventory(AnvilGui.createInventory(player, OPAnvil.anvilLevel.get(player.getUniqueId()), OPAnvil.putInItems.get(player.getUniqueId())));
                    }
                }.runTaskLater(OPAnvil.plugin, 1);
            }
        }
        if (clickedItem.isSimilar(ItemButtons.ANVIL_APPLY)) {
            ItemStack firstItem = event.getClickedInventory().getItem(21);
            ItemStack secondItem = event.getClickedInventory().getItem(23);
            if (OPAnvil.selectedItem.get(player.getUniqueId()) == null) {
                if (OPAnvil.selectedName.get(player.getUniqueId()) != null){
                    if (firstItem != null){
                        ItemStack itemStack = firstItem.clone();
                        doAnvilMake(itemStack, player, event.getClickedInventory());
                    }
                }
                if (firstItem != null && secondItem != null) {
                    if (firstItem.getType() == secondItem.getType()) {
                        ItemStack itemStack = new ItemStack(firstItem.getType(), firstItem.getAmount());
                        if (firstItem.getItemMeta() != null && secondItem.getItemMeta() != null) {
                            Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
                            Map<Enchantment, Integer> firstItemEnchants = firstItem.getItemMeta().getEnchants();
                            Map<Enchantment, Integer> secondItemEnchants = secondItem.getItemMeta().getEnchants();
                            for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                                finalEnchantments.put(allFirstEnchants.getKey(), allFirstEnchants.getValue());
                                for (Map.Entry<Enchantment, Integer> allSecondEnchants : secondItemEnchants.entrySet()) {
                                    if (finalEnchantments.containsKey(allSecondEnchants.getKey())) {
                                        if (finalEnchantments.get(allSecondEnchants.getKey()).equals(allSecondEnchants.getValue())) {
                                            finalEnchantments.put(allSecondEnchants.getKey(), allSecondEnchants.getValue() + finalEnchantments.get(allSecondEnchants.getKey()));
                                        }
                                    } else {
                                        finalEnchantments.put(allSecondEnchants.getKey(), allSecondEnchants.getValue());
                                    }
                                }
                            }
                            ItemMeta meta = itemStack.getItemMeta();
                            for (Map.Entry<Enchantment, Integer> finalApplyEnchantments : finalEnchantments.entrySet()) {
                                int determinedLevel = finalApplyEnchantments.getValue();
                                if (determinedLevel > finalApplyEnchantments.getKey().getMaxLevel()) {
                                    determinedLevel = finalApplyEnchantments.getKey().getMaxLevel();
                                }
                                meta.addEnchant(finalApplyEnchantments.getKey(), determinedLevel, false);
                            }
                            meta.setDisplayName(firstItem.getItemMeta().getDisplayName());
                            meta.setLore(firstItem.getItemMeta().getLore());
                            itemStack.setItemMeta(meta);
                            convertToLore(itemStack);
                            doAnvilMake(itemStack, player, event.getClickedInventory());
                        }
                    }
                    else if (secondItem.getType().equals(Material.ENCHANTED_BOOK) && firstItem.getItemMeta() != null) {
                        String steelLevel = null;
                        if (secondItem.getItemMeta().getLore() != null){
                            List<String> steel = secondItem.getItemMeta().getLore();
                            for (String string : steel){
                                if (string.toLowerCase().contains("steel")){
                                    steelLevel = string;
                                }
                            }
                        }
                        List<String> newList = new ArrayList<>();
                        ItemStack itemStack = firstItem.clone();
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
                        Map<Enchantment, Integer> storedBookEnchants = enchantmentStorageMeta.getStoredEnchants();
                        Map<Enchantment, Integer> firstItemEnchants = firstItem.getEnchantments();
                        Map<Enchantment, Integer> finalEnchantApply = new HashMap<>();
                        for (Map.Entry<Enchantment, Integer> allSecondEnchants : storedBookEnchants.entrySet()) {
                            finalEnchantApply.put(allSecondEnchants.getKey(), allSecondEnchants.getValue());
                            for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                                if (finalEnchantApply.containsKey(allFirstEnchants.getKey())) {
                                    if (finalEnchantApply.get(allFirstEnchants.getKey()).equals(allFirstEnchants.getValue())) {
                                        finalEnchantApply.put(allFirstEnchants.getKey(), allFirstEnchants.getValue() + finalEnchantApply.get(allFirstEnchants.getKey()));
                                    }
                                } else {
                                    finalEnchantApply.put(allFirstEnchants.getKey(), allFirstEnchants.getValue());

                                }
                            }
                        }
                        ItemMeta meta = itemStack.getItemMeta();
                        for (Map.Entry<Enchantment, Integer> finalApplyEnchantments : finalEnchantApply.entrySet()) {
                            int determinedLevel = finalApplyEnchantments.getValue();
                            if (determinedLevel > finalApplyEnchantments.getKey().getMaxLevel()) {
                                determinedLevel = finalApplyEnchantments.getKey().getMaxLevel();
                            }
                            meta.addEnchant(finalApplyEnchantments.getKey(), determinedLevel, false);
                        }
                        if (steelLevel != null){
                            newList.add(steelLevel);
                            meta.setLore(newList);
                        }
                        itemStack.setItemMeta(meta);
                        convertToLore(itemStack);
                        doAnvilMake(itemStack, player, event.getClickedInventory());
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()) == null){
                return;
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()).equals(Material.SLIME_BALL)) {
                if (firstItem != null && secondItem != null) {
                    if (firstItem.getType() == secondItem.getType()) {
                        ItemStack itemStack = new ItemStack(firstItem.getType(), firstItem.getAmount());
                        if (firstItem.getItemMeta() != null && secondItem.getItemMeta() != null) {
                            Map<Enchantment, Integer> finalEnchantments = new HashMap<>();
                            Map<Enchantment, Integer> firstItemEnchants = firstItem.getItemMeta().getEnchants();
                            Map<Enchantment, Integer> secondItemEnchants = secondItem.getItemMeta().getEnchants();
                            List<Enchantment> affectedEnchants = new ArrayList<>();
                            for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                                finalEnchantments.put(allFirstEnchants.getKey(), allFirstEnchants.getValue());
                            }
                            for (Map.Entry<Enchantment, Integer> allSecondEnchants : secondItemEnchants.entrySet()) {
                                if (!affectedEnchants.contains(allSecondEnchants.getKey())) {
                                    if (finalEnchantments.containsKey(allSecondEnchants.getKey())) {
                                        finalEnchantments.put(allSecondEnchants.getKey(), allSecondEnchants.getValue() + finalEnchantments.get(allSecondEnchants.getKey()));
                                    } else {
                                        finalEnchantments.put(allSecondEnchants.getKey(), allSecondEnchants.getValue());
                                    }
                                    affectedEnchants.add(allSecondEnchants.getKey());
                                }
                            }
                            affectedEnchants.clear();
                            ItemMeta meta = itemStack.getItemMeta();
                            for (Map.Entry<Enchantment, Integer> finalApplyEnchantments : finalEnchantments.entrySet()) {
                                int determinedLevel = finalApplyEnchantments.getValue();
                                if (determinedLevel >= 32000) {
                                    determinedLevel = 32000;
                                }
                                meta.addEnchant(finalApplyEnchantments.getKey(), determinedLevel, true);
                            }
                            meta.setDisplayName(firstItem.getItemMeta().getDisplayName());
                            meta.setLore(firstItem.getItemMeta().getLore());
                            itemStack.setItemMeta(meta);
                            convertToLore(itemStack);
                            doAnvilMake(itemStack, player, event.getClickedInventory());
                        }
                    }
                    else if (secondItem.getType().equals(Material.ENCHANTED_BOOK) && firstItem.getItemMeta() != null) {
                        ItemStack itemStack = firstItem.clone();
                        String steelLevel = null;
                        if (secondItem.getItemMeta().getLore() != null){
                            List<String> steel = secondItem.getItemMeta().getLore();
                            for (String string : steel){
                                if (string.toLowerCase().contains("steel")){
                                    steelLevel = string;
                                }
                            }
                        }
                        List<String> newList = new ArrayList<>();
                        EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();
                        Map<Enchantment, Integer> storedBookEnchants = enchantmentStorageMeta.getStoredEnchants();
                        Map<Enchantment, Integer> firstItemEnchants = firstItem.getEnchantments();
                        Map<Enchantment, Integer> finalEnchantApply = new HashMap<>();
                        for (Map.Entry<Enchantment, Integer> allSecondEnchants : storedBookEnchants.entrySet()) {
                            finalEnchantApply.put(allSecondEnchants.getKey(), allSecondEnchants.getValue());
                            for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                                if (finalEnchantApply.containsKey(allFirstEnchants.getKey())) {
                                    finalEnchantApply.put(allFirstEnchants.getKey(), allFirstEnchants.getValue() + finalEnchantApply.get(allFirstEnchants.getKey()));
                                } else {
                                    finalEnchantApply.put(allFirstEnchants.getKey(), allFirstEnchants.getValue());

                                }
                            }
                        }
                        ItemMeta meta = itemStack.getItemMeta();
                        for (Map.Entry<Enchantment, Integer> finalApplyEnchantments : finalEnchantApply.entrySet()) {
                            int determinedLevel = finalApplyEnchantments.getValue();
                            if (determinedLevel >= 32000) {
                                determinedLevel = 32000;
                            }
                            meta.addEnchant(finalApplyEnchantments.getKey(), determinedLevel, true);
                        }
                        if (steelLevel != null){
                            newList.add(steelLevel);
                            meta.setLore(newList);
                        }
                        itemStack.setItemMeta(meta);
                        convertToLore(itemStack);
                        doAnvilMake(itemStack, player, event.getClickedInventory());
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()).equals(Material.ENCHANTING_TABLE)) {
                if (firstItem != null && secondItem == null) {
                    ItemStack itemStack = firstItem.clone();
                    Map<Enchantment, Integer> firstItemEnchants = firstItem.getEnchantments();
                    Map<Enchantment, Integer> finalEnchants = new HashMap<>();
                    for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                        if (!(allFirstEnchants.getValue() > allFirstEnchants.getKey().getMaxLevel())) {
                            finalEnchants.put(allFirstEnchants.getKey(), allFirstEnchants.getKey().getMaxLevel());
                        }
                    }
                    ItemMeta meta = itemStack.getItemMeta();
                    for (Map.Entry<Enchantment, Integer> finalApplyEnchantments : finalEnchants.entrySet()) {
                        int determinedLevel = finalApplyEnchantments.getValue();
                        if (determinedLevel >= 32000) {
                            determinedLevel = 32000;
                        }
                        meta.addEnchant(finalApplyEnchantments.getKey(), determinedLevel, true);
                    }
                    itemStack.setItemMeta(meta);
                    convertToLore(itemStack);
                    doAnvilMake(itemStack, player, event.getClickedInventory());
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()).equals(Material.COMMAND_BLOCK)) {
                if (firstItem != null && secondItem == null) {
                    ItemStack itemStack = firstItem.clone();
                    if (EnchantmentTarget.TOOL.includes(itemStack) || EnchantmentTarget.ARMOR.includes(itemStack) || EnchantmentTarget.WEAPON.includes(itemStack) || EnchantmentTarget.BOW.includes(itemStack) || EnchantmentTarget.CROSSBOW.includes(itemStack) || EnchantmentTarget.TRIDENT.includes(itemStack) || EnchantmentTarget.FISHING_ROD.includes(itemStack)) {
                        ItemMeta meta = firstItem.getItemMeta();
                        int randomint = 1;
                        Enchantment randEnchant;
                        for (int i = 0; i < 3; i++) {
                            if (randomint != 0) {
                                randEnchant = Enchantment.values()[(int) (Math.random() * (Enchantment.values()).length)];
                                if (randEnchant.canEnchantItem(itemStack) && !randEnchant.getKey().toString().toLowerCase().contains("curse")) {
                                    randomint--;
                                    if (meta.hasEnchant(randEnchant)) {
                                        meta.addEnchant(randEnchant, meta.getEnchantLevel(randEnchant) + 1, false);
                                    } else {
                                        meta.addEnchant(randEnchant, 1, true);
                                    }
                                } else {
                                    i = 0;
                                }
                            }
                        }
                        itemStack.setItemMeta(meta);
                        convertToLore(itemStack);
                        doAnvilMake(itemStack, player, event.getClickedInventory());
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()).equals(Material.NETHERITE_INGOT)) {
                if (firstItem != null && secondItem == null) {
                    ItemStack itemStack = firstItem.clone();
                    String originalMaterial = firstItem.getType().toString();
                    if (originalMaterial.contains("_")){
                        String[] splitStrings = originalMaterial.split("_");
                        String itemType = splitStrings[1];
                        try {
                            Material material = Material.valueOf("NETHERITE_" + itemType);
                            itemStack.setType(material);
                            doAnvilMake(itemStack, player, event.getClickedInventory());
                        } catch (IllegalArgumentException exception) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                        }
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
            if (OPAnvil.selectedItem.get(player.getUniqueId()).equals(Material.LAPIS_LAZULI)) {
                if (firstItem != null && secondItem == null) {
                    if (firstItem.getEnchantments().size() >= 1) {
                        if (firstItem.getItemMeta().hasLore()) {
                            ItemStack itemStack = firstItem.clone();
                            ItemMeta meta = firstItem.getItemMeta();
                            Map<Enchantment, Integer> firstItemEnchants = firstItem.getEnchantments();
                            Map<Enchantment, Integer> finalItemEnchants = new HashMap<>();
                            for (Map.Entry<Enchantment, Integer> allFirstEnchants : firstItemEnchants.entrySet()) {
                                finalItemEnchants.put(allFirstEnchants.getKey(), (allFirstEnchants.getValue() * 2));
                            }
                            for (Map.Entry<Enchantment, Integer> allFinalEnchants : finalItemEnchants.entrySet()) {
                                int determinedValue = allFinalEnchants.getValue();
                                if (determinedValue >= 32000) {
                                    determinedValue = 32000;
                                }
                                meta.addEnchant(allFinalEnchants.getKey(), determinedValue, true);
                            }
                            List<String> list = new ArrayList<>();
                            List<String> realItemLore = meta.getLore();
                            String steelPossible = null;
                            for (String string : realItemLore){
                                if (string.toLowerCase().contains("steel")){
                                    steelPossible = string;
                                }
                            }
                            for (Map.Entry<Enchantment, Integer> enchantLore : firstItemEnchants.entrySet()) {
                                long randomLevel = 0;
                                NumberFormat myFormat = NumberFormat.getInstance();
                                myFormat.setGroupingUsed(true);
                                String[] lowercaseWords = enchantLore.getKey().toString().replace("Enchantment[minecraft:", "").replace(", ", "").replace(enchantLore.getKey().getName(), "").replace("]", "").replace("_", " ").split(" ");
                                List<String> uppercaseWords = new ArrayList<>();
                                for (String s : lowercaseWords) {
                                    uppercaseWords.add(s.replaceFirst(s.charAt(0) + "", Character.toUpperCase(s.charAt(0)) + ""));
                                }
                                for (String string : realItemLore) {
                                    if (string.toLowerCase().contains(uppercaseWords.get(0).toLowerCase())) {
                                        String[] originalSplit = string.split(" ");
                                        String number = originalSplit[originalSplit.length - 1];
                                        randomLevel = Long.parseLong(number.replace(",", "")) * 2;
                                        if (randomLevel < 0){
                                            randomLevel = Long.parseLong("6969696969696969696");
                                        }
                                        break;
                                    }
                                }
                                if (!list.contains(ChatColor.GRAY + String.join(" ", uppercaseWords) + " " + myFormat.format(randomLevel))) {
                                    list.add(ChatColor.GRAY + String.join(" ", uppercaseWords) + " " + myFormat.format(randomLevel));
                                }
                            }
                            if (steelPossible != null){
                                list.add(steelPossible);
                            }
                            meta.setLore(list);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            itemStack.setItemMeta(meta);
                            doAnvilMake(itemStack, player, event.getClickedInventory());
                        }
                    }
                    else {
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    }
                }
                else {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                }
            }
        }
    }

    public static void setSelectedItem(ItemStack itemStack, Player player, Inventory inventory){
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();

        lore.add(ChatColor.GREEN.toString()+ChatColor.ITALIC+"(SELECTED)");
        meta.addEnchant(Enchantment.CHANNELING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

        if (OPAnvil.selectedItem.containsKey(player.getUniqueId())){
            for (int i = 0; i < inventory.getSize(); i++){
                if (inventory.getItem(i) != null){
                    if (inventory.getItem(i).getType().equals(OPAnvil.selectedItem.get(player.getUniqueId()))){
                        removeSelectedItem(player, inventory.getItem(i));
                    }
                }
            }
        }

        OPAnvil.selectedItem.put(player.getUniqueId(), itemStack.getType());
    }

    public static void removeSelectedItem(Player player, ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(ChatColor.GREEN.toString()+ChatColor.ITALIC+"(SELECTED)");
        meta.setLore(lore);
        meta.removeEnchant(Enchantment.CHANNELING);
        itemStack.setItemMeta(meta);
        OPAnvil.selectedItem.remove(player.getUniqueId());
    }

    public static void convertToLore(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        List<String> list = new ArrayList<>();
        String savedSteel = null;
        if (meta.hasLore()){
            List<String> currentLore = meta.getLore();
            for (String string : currentLore){
                if (string.toLowerCase().contains("steel")){
                    savedSteel = string;
                }
            }
        }
        Map<Enchantment, Integer> allEnchantments = meta.getEnchants();
        for (Map.Entry<Enchantment, Integer> enchantLore : allEnchantments.entrySet()) {
            NumberFormat myFormat = NumberFormat.getInstance();
            myFormat.setGroupingUsed(true);
            int randomLevel = enchantLore.getValue();
            String[] lowercaseWords = enchantLore.getKey().toString().replace("Enchantment[minecraft:", "").replace(", ", "").replace(enchantLore.getKey().getName(), "").replace("]", "").replace("_", " ").split(" ");
            List<String> uppercaseWords = new ArrayList<>();
            for (String s : lowercaseWords) {
                uppercaseWords.add(s.replaceFirst(s.charAt(0) + "", Character.toUpperCase(s.charAt(0)) + ""));
            }
            if (!list.contains(ChatColor.GRAY + String.join(" ", uppercaseWords) + " " + myFormat.format(randomLevel))){
                list.add(ChatColor.GRAY + String.join(" ", uppercaseWords) + " " + myFormat.format(randomLevel));
            }
        }
        if (savedSteel != null){
            list.add(savedSteel);
        }
        meta.setLore(list);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
    }

    public static void doAnvilMake(ItemStack result, Player player, Inventory inventory){
        inventory.clear(21);
        inventory.clear(23);
        ItemMeta meta = result.getItemMeta();
        if (OPAnvil.selectedName.get(player.getUniqueId()) != null){
            meta.setDisplayName(OPAnvil.selectedName.get(player.getUniqueId()));
            result.setItemMeta(meta);
        }
        player.setItemOnCursor(result);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
    }
}
