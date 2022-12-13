package com.adenark.villagernames;

import com.adenark.villagernames.command.HelpCmd;
import com.adenark.villagernames.listener.EventVillagerSpawn;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Spigot VillagerNames plugin
 *
 * @version 1.1
 * @author Mancdev
 * @website https://mancdev.com
 */
public class VillagerNames extends JavaPlugin {

    /*
        Static access
     */
    public static VillagerNames instance;

    /*
        Spigot configuration files
     */
    private File configFile;
    private FileConfiguration config;

    /*
        Plugin Config objects
     */
    public static boolean DEBUG;
    public static boolean NAME_VISIBLE;
    public static double NAME_POTENTIAL;
    public static String NAME_DISPLAY;
    public static String NAME_DISPLAY_WITH_PROFF;
    public static List<String> NAMES;


    /**
     * Enables the plugin.
     */
    @Override
    public void onEnable() {
        // Static access
        instance = this;

        // Load configuration file
        this.initConfig();

        // Set configuration objects
        NAME_VISIBLE = getConfig().getBoolean("name_visible");
        NAME_POTENTIAL = getConfig().getDouble("name_potential");
        NAME_DISPLAY = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("display_name")));
        NAME_DISPLAY_WITH_PROFF = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig().getString("display_name_with_profession")));
        DEBUG = getConfig().getBoolean("debug");
        NAMES = getConfig().getStringList("villager_names");

        // Register commands
        getCommand("va").setExecutor(new HelpCmd());

        // Register listeners
        getServer().getPluginManager().registerEvents(new EventVillagerSpawn(), this);

        // Enabled message
        String enableMessage = "&r\n" +
                "&r\n" +
                "&c▉▉▉▉▉▉▉▉▉ &a&l(!) &aVillageNames has been enabled! \n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉" + "\n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉" + "\n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉ Loaded villager names: " + NAMES.size() + "\n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉ Version: " + getDescription().getVersion() + "\n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉ Website: " + getDescription().getWebsite()  + "\n" +
                "&c▉▉▉▉▉▉▉▉▉ " + "\n" +
                "&c▉▉▉&f▉▉▉&c▉▉▉" + "\n" +
                "&c▉▉▉▉▉▉▉▉▉" + "\n" +
                "&r Made by &cMancdev" + "\n" +
                "&r";
        this.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', enableMessage));
    }

    @Override
    public void onDisable() {
        // Save configuration file
        saveResource("config.yml", false);
    }

    /**
     * Initalise the plugin's config file.
     * If the file does not exist then copy from within
     * the plugin.jar file.
     */
    private void initConfig() {
        this.configFile = new File(getDataFolder(), "config.yml");
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        this.config = new YamlConfiguration();
        try {
            this.config.load(this.configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Server server() {
        return instance.getServer();
    }


}
