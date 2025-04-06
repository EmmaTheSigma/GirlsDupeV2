package me.GirlsDupeV2.girlsDupeV2.commands;

import me.GirlsDupeV2.girlsDupeV2.*;
import me.GirlsDupeV2.girlsDupeV2.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class GirlsDupeCommand implements CommandExecutor, TabCompleter {
    private final GirlsDupeV2 plugin;

    public GirlsDupeCommand(GirlsDupeV2 plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || sender instanceof ConsoleCommandSender) {
            // pass
        } else if (!sender.hasPermission("girlsdupe.use")) {
            sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            if (!sender.hasPermission("girlsdupe.help")) {
                sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                return true;
            }
            List<String> helpMessages = plugin.getLanguageManager().getHelpMessages();
            for (String message : helpMessages) {
                sender.sendMessage(plugin.getLanguageManager().applyColors(message));
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "notify":
                if (!sender.hasPermission("girlsdupe.sendnotify")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("usage-notify"));
                    return true;
                }
                String messageContent = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                plugin.getNotifier().sendNotifyMessage(messageContent);
                break;

            case "verbose":
                if (!sender.hasPermission("girlsdupe.verbose")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                plugin.toggleVerboseMode(sender);
                break;

            case "check":
                if (!sender.hasPermission("girlsdupe.check")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("usage-check"));
                    return true;
                }
                Player target = plugin.getServer().getPlayer(args[1]);
                if (target != null) {
                    int violationLevel = plugin.getViolationManager().getViolationLevel(target);
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("violation-level")
                            .replace("{player}", target.getName())
                            .replace("{level}", String.valueOf(violationLevel)));
                } else {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("player-not-found")
                            .replace("{player}", args[1]));
                }
                break;

            case "reset":
                if (!sender.hasPermission("girlsdupe.reset")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("usage-reset"));
                    return true;
                }
                Player targetToReset = plugin.getServer().getPlayer(args[1]);
                if (targetToReset != null) {
                    plugin.getViolationManager().resetViolationLevel(targetToReset);
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("reset-success")
                            .replace("{player}", args[1]));
                } else {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("player-not-found")
                            .replace("{player}", args[1]));
                }
                break;

            case "kick":
                if (!sender.hasPermission("girlsdupe.kick")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("usage-kick"));
                    return true;
                }

                Player playerToKick = plugin.getServer().getPlayer(args[1]);
                if (playerToKick != null) {
                    String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                    if (plugin.getConfigManager().isKickStrikeLightning()) {
                        if (FoliaCheck.isFolia()) {
                            Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
                                try {
                                    playerToKick.getWorld().strikeLightningEffect(playerToKick.getLocation());
                                } catch (Exception e) {
                                    plugin.getLogger().severe("Failed to strike lightning effect on Folia: " + e.getMessage());
                                }
                            });
                        } else {
                            playerToKick.getWorld().strikeLightningEffect(playerToKick.getLocation());
                        }
                    }

                    if (plugin.getLanguageManager().isKickBroadcastEnabled()) {
                        String kickMessage = plugin.getLanguageManager().getPrefixedMessage("kick-format")
                                .replace("%player%", playerToKick.getName())
                                .replace("%reason%", reason);
                        plugin.getServer().broadcastMessage(kickMessage);
                    }

                    plugin.getNotifier().kickPlayer(playerToKick, reason);

                } else {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("player-not-found")
                            .replace("{player}", args[1]));
                }
                break;

            case "reload":
                if (!sender.hasPermission("girlsdupe.reload")) {
                    sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("no-permission"));
                    return true;
                }
                plugin.reloadConfig();
                LanguageManager.getInstance(plugin).reloadLanguage();
                sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("config-reloaded"));
                break;
            default:
                sender.sendMessage(plugin.getLanguageManager().getPrefixedMessage("unknown-command"));
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("help", "notify", "verbose", "check", "reset", "kick", "reload"));
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("kick")) {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}