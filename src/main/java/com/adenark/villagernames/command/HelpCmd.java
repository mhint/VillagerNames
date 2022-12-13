package com.adenark.villagernames.command;

import com.adenark.villagernames.VillagerNames;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Plugin]: &r" +
            VillagerNames.getInstance().getName()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Config]"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Debug mode: " +
            VillagerNames.DEBUG));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Names loaded: " +
            VillagerNames.NAMES.size()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Chance: : " +
            VillagerNames.NAME_POTENTIAL * 100 + "%"));
        commandSender.sendMessage("");
        return true;
    }
}
