package me.GirlsDupeV2.girlsDupeV2.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DupeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,  String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
        }
        if (cmd.getName().equalsIgnoreCase("dupe")){
            if (sender instanceof Player){
                Player player = (Player) sender;
                player.getInventory().addItem(player.getItemInHand());
            }
        }
        return true;
    }
}