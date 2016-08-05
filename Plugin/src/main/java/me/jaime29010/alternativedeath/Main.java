package me.jaime29010.alternativedeath;

import me.jaime29010.alternativedeath.api.AbstractBodyHelper;
import me.jaime29010.alternativedeath.listeners.PlayerListener;
import me.jaime29010.alternativedeath.utils.ConfigurationManager;
import me.jaime29010.alternativedeath.utils.PluginUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Main extends JavaPlugin {
    private FileConfiguration config = null;
    private ShapedRecipe recipe = null;
    private String nmsver = null;
    private AbstractBodyHelper helper = null;

    @Override
    public void onEnable() {
        //Loading the configuration
        this.getConfig();

        //Loading the recipe
        ItemStack item = new ItemStack(Material.valueOf(config.getString("recipe.result.type")));
        item.setDurability((short) config.getInt("recipe.result.damage"));
        item.setAmount(config.getInt("recipe.result.amount"));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(PluginUtils.colorize(config.getString("recipe.result.name")));
        meta.setLore(config.getStringList("recipe.result.lore").stream().map(PluginUtils::colorize).collect(Collectors.toList()));
        item.setItemMeta(meta);

        recipe = new ShapedRecipe(item);
        List<String> shape = config.getStringList("recipe.shape");
        recipe.shape(shape.get(0), shape.get(1), shape.get(2));
        Map<String, Object> map = config.getConfigurationSection("recipe.items").getValues(false);
        Stream<Entry<String, Object>> stream = map.entrySet().stream();
        Map<Character, Material> collect = stream.collect(Collectors.toMap(entry -> entry.getKey().charAt(0), entry -> Material.valueOf(String.valueOf(entry.getValue()))));
        collect.forEach((key, ingredient) -> recipe.setIngredient(key, ingredient));
        getServer().addRecipe(recipe);

        //Loading the helper
        nmsver = getServer().getClass().getPackage().getName().split(".")[3];
        getLogger().info("Your server is running version " + nmsver);
        switch (nmsver) {
            case "v1_8_R3": {
                helper = new me.jaime29010.alternativedeath.v1_8_R3.BodyHelper();
                break;
            }
            case "v1_9_R1": {
                helper = new me.jaime29010.alternativedeath.v1_9_R1.BodyHelper();
                break;
            }
            case "v1_9_R2": {
                helper = new me.jaime29010.alternativedeath.v1_9_R2.BodyHelper();
                break;
            }
            case "v1_10_R1": {
                helper = new me.jaime29010.alternativedeath.v1_10_R1.BodyHelper();
                break;
            }
            default: {
                getLogger().info("This version is not supported, the plugin will not work");
                setEnabled(false);
                return;
            }
        }

        //Registering listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
        config = null;
        recipe = null;
        nmsver = null;
        helper = null;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public AbstractBodyHelper getBodyHelper() {
        return helper;
    }

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            config = ConfigurationManager.loadConfig("config.yml", this);
        }
        return config;
    }

    @Override
    public void saveConfig() {
        ConfigurationManager.saveConfig(config, "config.yml", this);
    }

    @Override
    public void saveDefaultConfig() {
        getConfig();
    }

    @Override
    public void reloadConfig() {
        getConfig();
    }
}
