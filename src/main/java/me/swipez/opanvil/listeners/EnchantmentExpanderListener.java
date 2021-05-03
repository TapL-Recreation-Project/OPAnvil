package me.swipez.opanvil.listeners;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.enchants.CustomEnchantManager;
import me.swipez.opanvil.utils.RandomLootPicker;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantmentExpanderListener implements Listener {

    OPAnvil plugin;

    public EnchantmentExpanderListener(OPAnvil plugin) {
        this.plugin = plugin;
    }

    Random random = new Random();

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            Player player = (Player) event.getDamager();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (CustomEnchantManager.isAppliedEnchantment(itemStack, "sweep")){
                double offset = CustomEnchantManager.getEnchantmentLength(itemStack, "sweep")*0.3;
                List<Entity> entities = event.getEntity().getNearbyEntities(1+offset, 1+offset, 1+offset);
                for (Entity entity : entities){
                    if (entity instanceof LivingEntity){
                        if (entity.getUniqueId() != player.getUniqueId()){
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(event.getDamage());
                        }
                    }
                }
            }
            if (CustomEnchantManager.isAppliedEnchantment(itemStack, "fire aspect")){
                double offset = CustomEnchantManager.getEnchantmentLength(itemStack, "fire aspect")*0.3;
                List<Entity> entities = event.getEntity().getNearbyEntities(1+offset, 1+offset, 1+offset);
                for (Entity entity : entities){
                    if (entity instanceof LivingEntity){
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.setFireTicks(300);
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerShootBow(ProjectileLaunchEvent event){
        if (!(event.getEntity().getShooter() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (CustomEnchantManager.isAppliedEnchantment(itemStack, "power")){
            double offset = CustomEnchantManager.getEnchantmentLength(itemStack, "power");
            if (!(offset > 5)){
                return;
            }
            List<Entity> entities = event.getEntity().getNearbyEntities(1+offset, 1+offset, 1+offset);
            for (Entity entity : entities){
                if (entity instanceof LivingEntity){
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.damage(offset*20);
                }
            }
        }
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        if (event.getEntity().getKiller() != null){
            Player player = event.getEntity().getKiller();
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (CustomEnchantManager.isAppliedEnchantment(itemStack, "looting")){
                double offset = CustomEnchantManager.getEnchantmentLength(itemStack, "looting");
                double randomPercent = random.nextDouble();
                if (offset/10 > randomPercent){
                    RandomLootPicker randomLootPicker = new RandomLootPicker(plugin);
                    ItemStack itemStack1 = randomLootPicker.randomItemStack();
                    checkForApply(itemStack1, plugin);
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack1);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerMine(BlockBreakEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (CustomEnchantManager.isAppliedEnchantment(itemStack, "efficiency")){
            int offset = CustomEnchantManager.getEnchantmentLength(itemStack, "efficiency");
            if (offset > 4){
                List<Block> nearbyBlocks = getNearbyBooksAndOres(event.getBlock(), offset/2);
                for (Block block : nearbyBlocks) {
                    block.breakNaturally(itemStack);
                }
            }
        }
    }
    private void checkForApply(ItemStack ritem, OPAnvil plugin){
        List<String> enchants = plugin.getConfig().getStringList("enchants_list");
        List<String> peffects = plugin.getConfig().getStringList("potion_effects");

        if (ritem.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta meta = ritem.getItemMeta();
            EnchantmentStorageMeta emeta = (EnchantmentStorageMeta) meta;
            int mine = 0;
            int maxe = enchants.size() - 1;
            double rench = Math.random() * (maxe - mine + 1) + mine;
            emeta.addStoredEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchants.get((int) rench).toLowerCase())), 10, true);
            ritem.setItemMeta(emeta);
        }
        if (ritem.getType() == Material.POTION) {
            ItemMeta meta = ritem.getItemMeta();
            PotionMeta pmeta = (PotionMeta) meta;
            int mine = 0;
            int maxe = peffects.size() - 1;
            double rench = Math.random() * (maxe - mine + 1) + mine;
            pmeta.addCustomEffect(new PotionEffect((PotionEffectType.getByName(peffects.get((int) rench).toUpperCase())), 1200, 2), true);
            pmeta.setColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            pmeta.setDisplayName(ChatColor.WHITE+"OP Potion");
            ritem.setItemMeta(pmeta);
        }
    }

    public List<Block> getNearbyBooksAndOres(Block block, int range) {
        List<Block> blockList = new ArrayList<>();

        int firstx = block.getLocation().getBlockX() - range;
        int firsty = block.getLocation().getBlockY();
        int firstz = block.getLocation().getBlockZ() - range;

        int secondx = block.getLocation().getBlockX() + range;
        int secondy = block.getLocation().getBlockY() + 1;
        int secondz = block.getLocation().getBlockZ() + range;

        for (int x = firstx; x < secondx; x++) {
            for (int y = firsty; y < secondy; y++) {
                for (int z = firstz; z < secondz; z++) {
                    blockList.add(block.getWorld().getBlockAt(x,y,z));
                }
            }
        }
        blockList.remove(block);
        return blockList;
    }
}
