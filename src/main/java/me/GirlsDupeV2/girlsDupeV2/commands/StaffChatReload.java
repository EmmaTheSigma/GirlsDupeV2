package me.GirlsDupeV2.girlsDupeV2.commands;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatReload implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            // Be able to do /screload from  console too.
            ConsoleCommandSender console = (ConsoleCommandSender) sender;
            GirlsDupeV2.getInstance().reloadConfiguration();
            console.sendMessage(ChatColor.GREEN + "Config has been reloaded!");
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("girlsdupe.staffchat.reload")) {
            p.sendMessage(ChatColor.RED + "You are not allowed to execute this command. Contact a server administrator if you believe this is an error.");
            return true;
        }
        try {
            GirlsDupeV2.getInstance().reloadConfiguration();
            sender.sendMessage(ChatColor.GREEN + "Config has been reloaded!");
        } catch (NullPointerException e) {
            sender.sendMessage(ChatColor.DARK_RED + "Error! Check console for more details. Having issues? Ask for support from plugin developer.");
        }
        return true;
    }
}