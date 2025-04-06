package me.GirlsDupeV2.girlsDupeV2.chat.executor;

import me.GirlsDupeV2.girlsDupeV2.chat.StaffChatType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatExecutor implements CommandExecutor {

    private final StaffChatType chatType;

    public StaffChatExecutor(StaffChatType chatType) {
        this.chatType = chatType;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        chatType.sendChatMessage(((Player) sender), String.join(" ", args));
        return true;
    }
}