package me.swipez.opanvil.utils;


import me.swipez.opanvil.OPAnvil;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLootPicker {
    private static final Random rng = new Random();
    private final OPAnvil plugin;
    private final List<Integer> cumulativeWeights = new ArrayList<>();
    private final List<Material> allMaterials = new ArrayList<Material>();
    private Configuration itemsConfig;

    public RandomLootPicker(OPAnvil plugin) {
        this.itemsConfig = plugin.getConfig();
        this.plugin = plugin;
        buildMaterialAndWeightList();
    }

    public void reloadConfig() {
        this.itemsConfig = plugin.getConfig();
        buildMaterialAndWeightList();
    }

    private void buildMaterialAndWeightList() {
        int weightSum = 0;
        cumulativeWeights.add(0);
        for (String itemKey : this.itemsConfig.getConfigurationSection("drop_items").getKeys(false)) {
            weightSum += this.itemsConfig.getInt("drop_items." + itemKey + ".relative_chance");
            allMaterials.add(Material.matchMaterial(itemKey));
            cumulativeWeights.add(weightSum);
        }
    }

    public ItemStack randomItemStack() {
        int maxWeight = cumulativeWeights.get(cumulativeWeights.size() - 1);
        double randomWeight = rng.nextDouble() * maxWeight;
        int numberOfWeights = cumulativeWeights.size();
        int bracket;
        for (bracket = numberOfWeights - 1; bracket >= 0; bracket--) {
            int weight = cumulativeWeights.get(bracket);
            if (randomWeight >= weight) {
                break;
            }
        }
        Material randomMaterial = allMaterials.get(bracket);
        int amount = itemsConfig.getInt("drop_items." + randomMaterial.toString().toLowerCase() + ".amount");
        return new ItemStack(randomMaterial, amount);
    }
}
