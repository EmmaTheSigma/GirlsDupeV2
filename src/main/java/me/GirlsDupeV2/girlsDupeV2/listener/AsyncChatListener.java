package me.GirlsDupeV2.girlsDupeV2.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import me.GirlsDupeV2.girlsDupeV2.renderer.LPCChatRenderer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static java.util.regex.Pattern.*;

public class AsyncChatListener implements Listener {

    private final GirlsDupeV2 plugin;
    private final LPCChatRenderer lpcChatRenderer;

    public AsyncChatListener(GirlsDupeV2 plugin) {
        this.plugin = plugin;
        this.lpcChatRenderer = new LPCChatRenderer(plugin);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        final Player player = event.getPlayer();

        if(!plugin.getConfig().getBoolean("use-item-placeholder", false) || !player.hasPermission("lpc.itemplaceholder")){
            event.renderer(lpcChatRenderer);
            return;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();
        final Component displayName = item.getItemMeta() != null && item.getItemMeta().hasDisplayName() ? item.getItemMeta().displayName() : Component.text(item.getType().toString().toLowerCase().replace("_", " "));
        if (item.getType().equals(Material.AIR) || displayName == null) {
            event.renderer(lpcChatRenderer);
            return;
        }

        event.renderer((source, sourceDisplayName, message, viewer) -> lpcChatRenderer.render(source, sourceDisplayName, message, viewer)
                .replaceText(TextReplacementConfig.builder().match(compile("\\[item]", CASE_INSENSITIVE))
                        .replacement(displayName.hoverEvent(item)).build()));
    }
}