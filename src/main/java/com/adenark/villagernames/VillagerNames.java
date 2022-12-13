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
            return (VillagerNames)plugin;
        }
    }

    public static boolean DEBUG;
    public static boolean NAME_VISIBLE;
    public static double NAME_POTENTIAL;
    public static String NAME_DISPLAY;
    public static String NAME_DISPLAY_WITH_PROFESSION;
    public static List<String> NAMES;

    private void loadConfiguredVariables() {
        NAME_VISIBLE = getConfig().getBoolean("name_visible");
        NAME_POTENTIAL = getConfig().getDouble("name_potential");
        NAME_DISPLAY = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getConfig()
            .getString("display_name")));
        NAME_DISPLAY_WITH_PROFESSION = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(
            getConfig().getString("display_name_with_profession"))
        );
        DEBUG = getConfig().getBoolean("debug");
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
        // Save configuration file
        saveResource("config.yml", false);
    }
}
