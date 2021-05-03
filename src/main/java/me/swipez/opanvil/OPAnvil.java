package me.swipez.opanvil;

import me.swipez.opanvil.command.TestCommand;
import me.swipez.opanvil.items.ItemButtons;
import me.swipez.opanvil.listeners.ChatListener;
import me.swipez.opanvil.listeners.EnchantmentExpanderListener;
import me.swipez.opanvil.listeners.InventoryClickListener;
import me.swipez.opanvil.listeners.SteelBlockBreakListener;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class OPAnvil extends JavaPlugin {

    public static HashMap<UUID, Integer> anvilLevel = new HashMap<>();
    public static HashMap<UUID, Integer> putInItems = new HashMap<>();
    public static HashMap<UUID, Material> requiredItem = new HashMap<>();
    public static HashMap<UUID, Material> selectedItem = new HashMap<>();
    public static HashMap<UUID, String> selectedName = new HashMap<>();
    public static List<UUID> currentlyNaming = new ArrayList<>();

    public static OPAnvil plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getCommand("testgui").setExecutor(new TestCommand());
        ItemButtons.initRecipes();
        getServer().getPluginManager().registerEvents(new SteelBlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentExpanderListener(this), this);
        saveDefaultConfig();
        getConfig().options().copyDefaults();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
