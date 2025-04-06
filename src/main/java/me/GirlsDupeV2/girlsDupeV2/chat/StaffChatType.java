package me.GirlsDupeV2.girlsDupeV2.chat;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import me.GirlsDupeV2.girlsDupeV2.utils.DiscordWebhook;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;

public interface StaffChatType {

    String getCommand();
    String getToggleCommand();
    String getLockCommand();
    String getPrefix();
    String getPermission();
    String getType();

    default boolean sendChatMessage(final Player player, final String message) {
        // Fix messages sending even if no permission.
        if (player.hasPermission("staff.staffchat") || player.hasPermission("staff.developerchat") || player.hasPermission("staff.adminchat")) {
            sendWebhook(player.getName(), message);
        }
        if (!player.hasPermission(getPermission()) && !player.isOp()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', GirlsDupeV2.getInstance().getConfig().getString(getPrefix() + ".error")));
            return false;
        }

        if (message.equals("")) {
            player.sendMessage("§cUsage:§7 /" + getPrefix() + " <message>");
            return false;
        }

        if (!GirlsDupeV2.getInstance().isChatEnabled(player, this)) {
            player.sendMessage("§7Do /" + getType() + "chatdisable to talk in " + getType() + " chat!");
            return true;
        }

        final boolean isPapi = GirlsDupeV2.getInstance().getPapiEnabled().get();
        final String header = GirlsDupeV2.getInstance().getConfig().getString(getPrefix() + ".header");
        final String placeholder = GirlsDupeV2.getInstance().getConfig().getString(getPrefix() + ".placeholder.name");

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission(getPermission())) {
                if (!GirlsDupeV2.getInstance().toggleTable.contains(staff.getUniqueId(), getType()))
                    GirlsDupeV2.getInstance().toggleTable.put(staff.getUniqueId(), getType(), true);
                if (GirlsDupeV2.getInstance().toggleTable.get(staff.getUniqueId(), getType())) {
                    String sendMessage = ChatColor.translateAlternateColorCodes('&', header) +
                            (isPapi ? placeholder : player.getName())
                            + ": "
                            + message;

                    if (isPapi) {
                        staff.sendMessage(PlaceholderAPI.setPlaceholders(player, sendMessage));
                    } else {
                        staff.sendMessage(sendMessage);
                    }
                }
            }
        }
        return true;
    }

    // Discord send webhook.
    static void sendWebhook(String name, String message) {
        if (!GirlsDupeV2.getInstance().getConfig().getBoolean("discordwebhook.enabled")) return;
        DiscordWebhook discordWebhook = new DiscordWebhook(GirlsDupeV2.getInstance().getConfig().getString("discordwebhook.webhook"));
        discordWebhook.setUsername(GirlsDupeV2.getInstance().getConfig().getString("discordwebhook.webhookusername"));
        discordWebhook.addEmbed(new DiscordWebhook.EmbedObject().setDescription(name + ": " + message).setColor(Color.RED).setFooter(GirlsDupeV2.getInstance().getConfig().getString("discordwebhook.footer"), GirlsDupeV2.getInstance().getConfig().getString("discordwebhook.footericon")));
        try {
            discordWebhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}