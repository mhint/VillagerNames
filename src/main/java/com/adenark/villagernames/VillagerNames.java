package com.adenark.villagernames;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import com.adenark.villagernames.command.HelpCmd;
import com.adenark.villagernames.listener.EventVillagerSpawn;

public class VillagerNames extends JavaPlugin {
    public static VillagerNames getInstance() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("VillagerNames");
        if (!(plugin instanceof VillagerNames)) {
            throw new RuntimeException("'VillagerNames' not found.");
        } else {
            return (VillagerNames) plugin;
        }
    }

    public static boolean DEBUG;
    public static boolean SET_CUSTOM_NAME_VISIBLE;
    public static boolean DISPLAY_NAMES_WITH_PROFESSION;
    public static double SPAWN_WITH_NAME_CHANCE;
    public static String FULL_DISPLAY_NAME;
    public static String FULL_DISPLAY_NAME_WITH_PROFESSION;
    public static List<String> NAMES;

    private void loadConfiguredVariables() {
        DEBUG = getConfig().getBoolean("debug");
        SET_CUSTOM_NAME_VISIBLE = getConfig().getBoolean("set_custom_name_visible");
        DISPLAY_NAMES_WITH_PROFESSION = getConfig().getBoolean("display_names_with_profession");
        SPAWN_WITH_NAME_CHANCE = getConfig().getDouble("spawn_with_name_chance");
        FULL_DISPLAY_NAME = ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(getConfig().getString("full_display_name")));
        FULL_DISPLAY_NAME_WITH_PROFESSION = ChatColor.translateAlternateColorCodes('&',
            Objects.requireNonNull(getConfig().getString("full_display_name_with_profession")));
        NAMES = getConfig().getStringList("villager_names");
    }

    @Override
    public void onEnable() {
        Logger logger = getLogger();

        saveDefaultConfig();
        loadConfiguredVariables();

        getCommand("villagernames").setExecutor(new HelpCmd());
        getServer().getPluginManager().registerEvents(new EventVillagerSpawn(), this);

        logger.log(Level.INFO, "VillagerNames has been enabled!");
        logger.log(Level.INFO, "Loaded villager names: {0}", NAMES.size());
        logger.log(Level.INFO, "Version: {0}", getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        saveResource("config.yml", false);
    }
}
