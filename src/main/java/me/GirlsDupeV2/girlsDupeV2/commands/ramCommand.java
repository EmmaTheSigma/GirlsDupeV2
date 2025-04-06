package me.GirlsDupeV2.girlsDupeV2.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ramCommand implements CommandExecutor {
    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args) {
        Runtime r = Runtime.getRuntime();
        long memUsed = (r.maxMemory() - r.freeMemory()) / 1048576;
        long memMax = r.maxMemory() / 1048576;
        sender.sendMessage(ChatColor.GOLD + "Ram Usage: " + ChatColor.WHITE + memUsed + "/" + memMax);
        return false;
    }
}