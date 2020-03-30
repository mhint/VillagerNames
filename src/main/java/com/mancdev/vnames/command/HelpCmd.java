package com.mancdev.vnames.command;

import com.mancdev.vnames.VillagerNames;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HelpCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Plugin]: &r" + VillagerNames.instance.getName()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Config]"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Debug mode: " + VillagerNames.DEBUG));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Names loaded: " + VillagerNames.NAMES.size()));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Chance: : " + VillagerNames.NAME_POTENTIAL * 100 + "%"));
        commandSender.sendMessage("");
        return true;
    }

}
