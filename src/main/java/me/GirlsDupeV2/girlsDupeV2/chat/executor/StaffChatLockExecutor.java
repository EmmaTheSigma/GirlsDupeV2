package me.GirlsDupeV2.girlsDupeV2.chat.executor;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import me.GirlsDupeV2.girlsDupeV2.chat.StaffChatType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatLockExecutor implements CommandExecutor {

    private final StaffChatType chatType;

    public StaffChatLockExecutor(StaffChatType chatType) {
        this.chatType = chatType;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player) sender;
        final GirlsDupeV2 staffChat = GirlsDupeV2.getInstance();

        if (!player.hasPermission(chatType.getPermission()) && !player.isOp()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', staffChat.getConfig().getString(chatType.getPrefix() + ".error")));
            return true;
        }

        if (!staffChat.toggleTable.contains(player.getUniqueId(), chatType.getType())) {
            staffChat.toggleTable.put(player.getUniqueId(), chatType.getType(), true);
        }
        final boolean isEnabled = GirlsDupeV2.getInstance().toggleTable.get(player.getUniqueId(), chatType.getType());
        GirlsDupeV2.getInstance().toggleTable.put(player.getUniqueId(), chatType.getType(), !isEnabled); // if it was disabled, this is true,
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', staffChat.getConfig().getString(chatType.getPrefix() + ".disable-" + (!isEnabled ? "on" : "off"))));
        return true;
    }
}