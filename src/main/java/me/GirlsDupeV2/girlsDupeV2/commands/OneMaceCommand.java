package me.GirlsDupeV2.girlsDupeV2.commands;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.minecart.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.scheduler.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.block.*;
import org.bukkit.inventory.meta.*;

public class OneMaceCommand implements CommandExecutor
{
    private final GirlsDupeV2 plugin;

    public OneMaceCommand(final GirlsDupeV2 plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /onemace <locate> | <info> | <fix>");
            return true;
        }
        if (args[0].equalsIgnoreCase("locate")) {
            if (!sender.hasPermission("girlsdupe.onemace.admin")) {
                sender.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                return true;
            }
            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] Locating the Mace...");
            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.locateMace(sender));
            return true;
        }
        else {
            if (args[0].equalsIgnoreCase("fix")) {
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> this.fixDuplicateMaces(sender));
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] Ensuring only one Mace exists on the server.");
                sender.sendMessage(String.valueOf(ChatColor.GRAY) + "If the Mace is destroyed, crafting is restored.");
                sender.sendMessage(String.valueOf(ChatColor.GRAY) + "Use /onemace locate to manually verify Mace status.");
                sender.sendMessage(String.valueOf(ChatColor.GOLD) + "made by Emma_the_sigma <33");
            }
            sender.sendMessage(String.valueOf(ChatColor.RED) + "Invalid subcommand. Use /onemace <locate> | <info> | <fix>");
            return true;
        }
    }

    private void fixDuplicateMaces(final CommandSender sender) {
        final List<ItemStack> foundMaces = new ArrayList<ItemStack>();
        final List<ItemStack> duplicates = new ArrayList<ItemStack>();
        final UUID maceOwner = this.plugin.getMaceOwner();
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] Running Mace scan...");
        for (final Player player : Bukkit.getOnlinePlayers()) {
            for (final ItemStack item : player.getInventory().getContents()) {
                if (this.isAnyMace(item)) {
                    foundMaces.add(item);
                }
                else if (this.isMaceInsideShulker(item)) {
                    foundMaces.addAll(this.getMacesFromShulker(item));
                }
            }
            for (final ItemStack item : player.getEnderChest().getContents()) {
                if (this.isAnyMace(item)) {
                    foundMaces.add(item);
                }
                else if (this.isMaceInsideShulker(item)) {
                    foundMaces.addAll(this.getMacesFromShulker(item));
                }
            }
        }
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof final AbstractHorse horse) {
                    for (final ItemStack item2 : horse.getInventory().getContents()) {
                        if (this.isAnyMace(item2)) {
                            foundMaces.add(item2);
                        }
                        else if (this.isMaceInsideShulker(item2)) {
                            foundMaces.addAll(this.getMacesFromShulker(item2));
                        }
                    }
                }
            }
        }
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof final Item item3) {
                    final ItemStack droppedItem = item3.getItemStack();
                    if (this.isAnyMace(droppedItem)) {
                        foundMaces.add(droppedItem);
                    }
                    else {
                        if (!this.isMaceInsideShulker(droppedItem)) {
                            continue;
                        }
                        foundMaces.addAll(this.getMacesFromShulker(droppedItem));
                    }
                }
            }
        }
        for (final World world : Bukkit.getWorlds()) {
            for (final Chunk chunk : world.getLoadedChunks()) {
                for (final BlockState state : chunk.getTileEntities()) {
                    if (state instanceof final Container container) {
                        for (final ItemStack item4 : container.getInventory().getContents()) {
                            if (this.isAnyMace(item4)) {
                                foundMaces.add(item4);
                            }
                            else if (this.isMaceInsideShulker(item4)) {
                                foundMaces.addAll(this.getMacesFromShulker(item4));
                            }
                        }
                    }
                }
            }
        }
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof final StorageMinecart minecart) {
                    for (final ItemStack item2 : minecart.getInventory().getContents()) {
                        if (this.isAnyMace(item2)) {
                            foundMaces.add(item2);
                        }
                        else if (this.isMaceInsideShulker(item2)) {
                            foundMaces.addAll(this.getMacesFromShulker(item2));
                        }
                    }
                }
                if (entity instanceof final ChestBoat chestBoat) {
                    for (final ItemStack item2 : chestBoat.getInventory().getContents()) {
                        if (this.isAnyMace(item2)) {
                            foundMaces.add(item2);
                        }
                        else if (this.isMaceInsideShulker(item2)) {
                            foundMaces.addAll(this.getMacesFromShulker(item2));
                        }
                    }
                }
            }
        }
        if (foundMaces.isEmpty()) {
            sender.sendMessage(String.valueOf(ChatColor.GREEN) + "[OneMace] No Mace found. Enabling crafting...");
            this.plugin.resetMaceCrafting();
            return;
        }
        final ItemStack officialMace = foundMaces.get(0);
        this.plugin.markMace(officialMace);
        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] Marked one Mace as the official Mace.");
        for (int i = 1; i < foundMaces.size(); ++i) {
            duplicates.add(foundMaces.get(i));
        }
        for (final ItemStack duplicate : duplicates) {
            duplicate.setAmount(0);
        }
        this.plugin.getConfig().set("settings.mace-crafted", (Object)true);
        this.plugin.saveConfig();
        final BukkitScheduler scheduler = Bukkit.getScheduler();
        final GirlsDupeV2 plugin = this.plugin;
        final GirlsDupeV2 plugin2 = this.plugin;
        Objects.requireNonNull(plugin2);
        scheduler.runTask((Plugin)plugin, plugin2::removeAllMaceRecipes);
        sender.sendMessage(String.valueOf(ChatColor.RED) + "[OneMace] Recipe removed to prevent further crafting.");
    }

    private boolean isAnyMace(final ItemStack item) {
        return item != null && item.getType() == Material.MACE;
    }

    private void locateMace(final CommandSender sender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (this.isAnyMace(item) || this.isMaceInsideShulker(item)) {
                    sender.sendMessage(String.valueOf(ChatColor.GREEN) + "[OneMace] The Mace is in " + String.valueOf(ChatColor.AQUA) + player.getName() + "'s Inventory.");
                    return;
                }
            }
            for (ItemStack item : player.getEnderChest().getContents()) {
                if (this.isAnyMace(item) || this.isMaceInsideShulker(item)) {
                    sender.sendMessage(String.valueOf(ChatColor.GREEN) + "[OneMace] The Mace is in " + String.valueOf(ChatColor.AQUA) + player.getName() + "'s Ender Chest.");
                    return;
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final AbstractHorse horse) {
                    for (ItemStack item2 : horse.getInventory().getContents()) {
                        if (this.isAnyMace(item2) || this.isMaceInsideShulker(item2)) {
                            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is in a storage animal at " + String.valueOf(ChatColor.GOLD) + "X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + String.valueOf(ChatColor.GRAY) + " in world " + entity.getWorld().getName());
                            return;
                        }
                    }
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final Item item3) {
                    final ItemStack droppedItem = item3.getItemStack();
                    if (this.isAnyMace(droppedItem) || this.isMaceInsideShulker(droppedItem)) {
                        final Location loc = entity.getLocation();
                        sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is dropped at " + String.valueOf(ChatColor.GOLD) + "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ() + String.valueOf(ChatColor.GRAY) + " in world " + loc.getWorld().getName());
                        return;
                    }
                    continue;
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (state instanceof final Container container) {
                        final Inventory inv = container.getInventory();
                        for (ItemStack item4 : inv.getContents()) {
                            if (this.isAnyMace(item4) || this.isMaceInsideShulker(item4)) {
                                final Location loc2 = state.getLocation();
                                sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is stored in a container at " + String.valueOf(ChatColor.GOLD) + "X: " + loc2.getBlockX() + " Y: " + loc2.getBlockY() + " Z: " + loc2.getBlockZ() + String.valueOf(ChatColor.GRAY) + " in world " + loc2.getWorld().getName());
                                return;
                            }
                        }
                    }
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final StorageMinecart minecart) {
                    final Inventory inv2 = minecart.getInventory();
                    for (ItemStack item5 : inv2.getContents()) {
                        if (this.isAnyMace(item5) || this.isMaceInsideShulker(item5)) {
                            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is in a storage minecart at " + String.valueOf(ChatColor.GOLD) + "X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + String.valueOf(ChatColor.GRAY) + " in world " + entity.getWorld().getName());
                            return;
                        }
                    }
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final ChestBoat chestBoat) {
                    final Inventory inv2 = chestBoat.getInventory();
                    for (ItemStack item5 : inv2.getContents()) {
                        if (this.isAnyMace(item5) || this.isMaceInsideShulker(item5)) {
                            sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is in a Chest Boat at " + String.valueOf(ChatColor.GOLD) + "X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + String.valueOf(ChatColor.GRAY) + " in world " + entity.getWorld().getName());
                            return;
                        }
                    }
                }
            }
        }
        if (this.plugin.getConfig().isConfigurationSection("offline_inventory")) {
            for (String uuid : this.plugin.getConfig().getConfigurationSection("offline_inventory").getKeys(true)) {
                if (this.plugin.getConfig().getBoolean("offline_inventory." + uuid, true)) {
                    sender.sendMessage(String.valueOf(ChatColor.YELLOW) + "[OneMace] The Mace is in an offline player's inventory (UUID: " + uuid + ").");
                    return;
                }
            }
        }
        sender.sendMessage(String.valueOf(ChatColor.RED) + "[OneMace] The Mace is either missing or in an unloaded chunk.");
    }

    private boolean isMaceInsideShulker(final ItemStack item) {
        if (item == null || item.getType() != Material.SHULKER_BOX) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta instanceof final BlockStateMeta blockStateMeta) {
            final BlockState blockState = blockStateMeta.getBlockState();
            if (blockState instanceof final ShulkerBox shulkerBox) {
                final Inventory shulkerInv = shulkerBox.getInventory();
                for (final ItemStack storedItem : shulkerInv.getContents()) {
                    if (this.plugin.isMace(storedItem)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<ItemStack> getMacesFromShulker(final ItemStack item) {
        final List<ItemStack> maces = new ArrayList<ItemStack>();
        if (item == null || item.getType() != Material.SHULKER_BOX) {
            return maces;
        }
        final BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();
        if (meta != null) {
            final BlockState blockState = meta.getBlockState();
            if (blockState instanceof final ShulkerBox shulkerBox) {
                final Inventory shulkerInv = shulkerBox.getInventory();
                for (final ItemStack storedItem : shulkerInv.getContents()) {
                    if (this.isAnyMace(storedItem)) {
                        maces.add(storedItem);
                    }
                }
                return maces;
            }
        }
        return maces;
    }
}
