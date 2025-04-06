package me.GirlsDupeV2.girlsDupeV2.listener;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;


public class DismountListener implements Listener {

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (e.getDismounted().hasMetadata("stair")) {
            Bukkit.getScheduler().runTaskLater(GirlsDupeV2.getInstance(), () -> e.getDismounted().remove(), 1L);
        }
    }
}