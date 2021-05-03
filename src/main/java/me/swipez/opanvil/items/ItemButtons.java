package me.swipez.opanvil.items;

import me.swipez.opanvil.OPAnvil;
import me.swipez.opanvil.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class ItemButtons {

    public static ItemStack ANVIL_APPLY = ItemBuilder.of(Material.ANVIL)
            .name(ChatColor.LIGHT_PURPLE+"Apply")
            .lore(ChatColor.GRAY+"Click here to apply the changes to the item!")
            .build();

    public static ItemStack PROGRESS = ItemBuilder.of(Material.HOPPER_MINECART)
            .name(ChatColor.GOLD+"Progress")
            .build();

    public static ItemStack MAX_ENCHANTS = ItemBuilder.of(Material.ENCHANTING_TABLE)
            .name(ChatColor.GOLD+"Max Enchants")
            .lore(ChatColor.GRAY+"Maximizes the enchantment levels for the item!")
            .build();

    public static ItemStack STEEL_1 = ItemBuilder.of(Material.ENCHANTED_BOOK)
            .lore(ChatColor.LIGHT_PURPLE+"Steel 1")
            .build();

    public static ItemStack STEEL_2 = ItemBuilder.of(Material.ENCHANTED_BOOK)
            .lore(ChatColor.LIGHT_PURPLE+"Steel 2")
            .build();

    public static ItemStack STEEL_3 = ItemBuilder.of(Material.ENCHANTED_BOOK)
            .lore(ChatColor.LIGHT_PURPLE+"Steel 3")
            .build();

    public static ItemStack STEEL_4 = ItemBuilder.of(Material.ENCHANTED_BOOK)
            .lore(ChatColor.LIGHT_PURPLE+"Steel 4")
            .build();

    public static ItemStack STEEL_5 = ItemBuilder.of(Material.ENCHANTED_BOOK)
            .lore(ChatColor.LIGHT_PURPLE+"Steel 5")
            .build();

    public static ItemStack ENCHANT_RANDOMIZER = ItemBuilder.of(Material.COMMAND_BLOCK)
            .name(ChatColor.AQUA+"Enchant Randomizer")
            .lore(ChatColor.GRAY+"Adds a random enchantment to the item!")
            .build();

    public static ItemStack ADDITIVE_ENCHANTS = ItemBuilder.of(Material.SLIME_BALL)
            .name(ChatColor.RED+"Additive Enchants")
            .lore(ChatColor.GRAY+"Adds enchantment levels instead of upgrading them")
            .build();

    public static ItemStack NETHERITE_PLATING = ItemBuilder.of(Material.NETHERITE_INGOT)
            .name(ChatColor.LIGHT_PURPLE+"Netherite Plating")
            .lore(ChatColor.GRAY+"Upgrades any item to netherite!")
            .build();

    public static ItemStack ENCHANT_MULTIPLIER = ItemBuilder.of(Material.LAPIS_LAZULI)
            .name(ChatColor.GREEN+"Enchant Multiplier")
            .lore(ChatColor.GRAY+"Doubles all enchantment levels on the item!")
            .build();

    public static ItemStack EMPTY_FILL = ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE)
            .name(ChatColor.GREEN+" ")
            .build();

    public static ItemStack RENAME = ItemBuilder.of(Material.WRITABLE_BOOK)
            .name(ChatColor.GOLD+"Rename")
            .build();

    public static ItemStack COAL_PROGRESS = ItemBuilder.of(Material.COAL)
            .name(ChatColor.GOLD+" ")
            .build();

    public static ItemStack DIAMOND_PROGRESS = ItemBuilder.of(Material.DIAMOND)
            .name(ChatColor.GOLD+" ")
            .build();

    public static void initRecipes(){
        registerSteelRecipe(Material.IRON_INGOT, STEEL_1, "steel_one_book", OPAnvil.plugin);
        registerSteelRecipe(Material.IRON_BLOCK, STEEL_2, "steel_two_book", OPAnvil.plugin);
        registerSteelRecipe(Material.ANVIL, STEEL_3, "steel_three_book", OPAnvil.plugin);
        registerSteel4Recipe("steel_four_book", OPAnvil.plugin);
        registerSteel5Recipe("steel_five_book", OPAnvil.plugin);
    }

    public static void registerSteelRecipe(Material ingredient, ItemStack result, String key, OPAnvil plugin){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, key), result)
                .shape("  I", "PPP")
                .setIngredient('I', ingredient)
                .setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(shapedRecipe);
    }

    public static void registerSteel4Recipe(String key, OPAnvil plugin){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, key), ItemButtons.STEEL_4)
                .shape("III", "PPP")
                .setIngredient('I', Material.ANVIL)
                .setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(shapedRecipe);
    }

    public static void registerSteel5Recipe(String key, OPAnvil plugin){
        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(plugin, key), ItemButtons.STEEL_5)
                .shape("III","III", "PPP")
                .setIngredient('I', Material.ANVIL)
                .setIngredient('P', Material.PAPER);
        Bukkit.addRecipe(shapedRecipe);
    }
}
