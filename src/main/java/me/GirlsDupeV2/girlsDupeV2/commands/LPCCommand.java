package me.GirlsDupeV2.girlsDupeV2.commands;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LPCCommand implements CommandExecutor, TabCompleter {

    private final GirlsDupeV2 plugin;

    public LPCCommand(GirlsDupeV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1 && "reload".equals(args[0])) {
            plugin.reloadConfig();

            String rawReloadMessage = plugin.getConfig().getString("reload-message", "<green>Reloaded LPC Configuration!");
            Component reloadMessage;
            reloadMessage = MiniMessage.miniMessage().deserialize(rawReloadMessage);

            sender.sendMessage(reloadMessage);
            return true;
        }

        return false;
    }



    public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final String[] args) {
        if (args.length == 1)
            return Collections.singletonList("reload");

        return new ArrayList<>();
    }
}