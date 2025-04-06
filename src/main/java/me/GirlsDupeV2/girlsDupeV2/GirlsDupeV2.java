package me.GirlsDupeV2.girlsDupeV2;

import me.GirlsDupeV2.girlsDupeV2.chat.*;
import me.GirlsDupeV2.girlsDupeV2.chat.executor.*;
import me.GirlsDupeV2.girlsDupeV2.chat.listener.*;
import me.GirlsDupeV2.girlsDupeV2.commands.*;
import me.GirlsDupeV2.girlsDupeV2.listener.*;
import me.GirlsDupeV2.girlsDupeV2.renderer.*;
import me.GirlsDupeV2.girlsDupeV2.managers.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.luckperms.api.LuckPerms;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import org.bukkit.command.CommandSender;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Sound;
import org.bukkit.plugin.*;
import org.bukkit.persistence.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.minecart.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.*;
import com.destroystokyo.paper.event.entity.*;
import org.bukkit.event.inventory.*;

public final class GirlsDupeV2 extends JavaPlugin implements Listener {

    private FileConfiguration config;

    private Location spawnLocation;
    private boolean soundEnabled;
    private String sound;
    private final Set<UUID> verbosePlayers = new HashSet<>();
    private boolean verboseConsole = false;
    private static GirlsDupeV2 instance;

    public static BukkitTask timer = null;
    public static MiniMessage miniMessage;
    private String apiKey;
    private String promptTemplate;
    private String apiUrl;
    private String botName;
    private boolean privateMessages;
    private final LinkedHashSet<TextComponent> components = new LinkedHashSet<>();
    public static final Map<String, String> advancmentToDisplayMap = new HashMap<>();
    private LuckPerms luckPerms;
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private ViolationManager violationManager;
    private Notifier notifier;
    private String ColoredVersion;
    public MiningDetectionExtension miningDetectionExtension;
    private Map<String, String> joinMessages;
    private Map<String, String> leaveMessages;
    public static long serverStart;
    private boolean maceCrafted;
    private NamespacedKey maceKey;
    private Set<UUID> trackedDestroyedItems;
    public Table<UUID, String, Boolean> toggleTable = HashBasedTable.create();
    public HashMap<UUID, String> lockMap = new HashMap<>();
    public AtomicBoolean papiEnabled = new AtomicBoolean(false);
    public ArrayList<StaffChatType> channels = new ArrayList<>();

    @Override
    public void onEnable() {
        GirlsDupeV2 instance = this; // Set the instance
        serverStart = System.currentTimeMillis();

        if (config != null) {
            getLogger().severe("Configuration is null. Plugin cannot be enabled.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        config = getConfig();
        createConfig(); // Load the config before using it
        config = this.getConfig();
        config.options().copyDefaults(true);
        config.addDefault("staffchat-enabled", true);
        config.addDefault("developerchat-enabled", true);
        config.addDefault("adminchat-enabled", true);
        config.addDefault("update-checker", true); // Update checker is now enabled by default.
        this.saveConfig();

        channels.add(new AdminChatImpl());
        channels.add(new DeveloperChatImpl());
        channels.add(new StaffChatImpl());
        final Logger logger = this.getLogger();
        //startTabListUpdater();

        PluginManager manager = Bukkit.getPluginManager();
        Objects.requireNonNull(this.getServer().getPluginManager()).registerEvents(this, this); //
        Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new CommandHandler(this)); //
        Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new CommandHandler(this));//
        Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new CommandHandler(this));//
        Objects.requireNonNull(this.getCommand("nightvision")).setExecutor(new NightVision());//
        Objects.requireNonNull(this.getCommand("dupe")).setExecutor(new DupeCommand());//
        Objects.requireNonNull(this.getCommand("lpc")).setExecutor(new LPCCommand(this));//
        Objects.requireNonNull(this.getCommand("rules")).setExecutor(this);//
        Objects.requireNonNull(this.getCommand("invsee")).setExecutor(this);
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(this);
        Objects.requireNonNull(this.getCommand("setspawn")).setExecutor(this);//
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(this);//
        Objects.requireNonNull(this.getCommand("ram")).setExecutor(new ramCommand());//
        Objects.requireNonNull(this.getCommand("screload")).setExecutor(new StaffChatReload());
        Objects.requireNonNull(this.getCommand("schelp")).setExecutor(new StaffChatHelp());
        Objects.requireNonNull(this.getCommand("staffchathelp")).setExecutor(new StaffChatHelp());

        Bukkit.getPluginManager().registerEvents(new MiningListener(this), this);

        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "golden_apple"), goldenApple);
        recipe.shape(new String[] { "XGX", "GAG", "XGX" });
        recipe.setIngredient('X', Material.AIR);
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe((Recipe)recipe);

        for (StaffChatType type : channels) {
            if (config.getBoolean(type.getPrefix() + "-enabled")) {
                Objects.requireNonNull(getCommand(type.getCommand())).setExecutor(new StaffChatExecutor(type));
                Objects.requireNonNull(getCommand(type.getToggleCommand())).setExecutor(new StaffChatToggleExecutor(type));
                Objects.requireNonNull(getCommand(type.getLockCommand())).setExecutor(new StaffChatLockExecutor(type));
            }
        }

        this.getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerPortalEventListener(), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        //this.getServer().getPluginManager().registerEvents(new TablistListener(this), this);
        config.addDefault("elytra_drop_percentage", 100);
        config.addDefault("elytra_drop_amount", 1);

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        // Load PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            // PlaceholderAPI is alive.
            if (config.getBoolean("enable-placeholders")) {
                papiEnabled.set(true);
                getLogger().info("Hooked into PlaceholderAPI! If you encounter any bugs within config with placeholders make sure to report it to the plugin developer.");
            }
            getLogger().info("Hooked into PlaceholderAPI! But configuration disables it, it is still in beta testing so we do not recommend using it.");
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is optional.");
        }

        LPCChatRenderer lpChatRenderer = new LPCChatRenderer(this);

        config = getConfig();
        loadSpawnLocation();

        this.maceCrafted = this.getConfig().getBoolean("settings.mace-crafted", false);
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (!this.doesMaceExist()) {
                this.resetMaceCrafting();
                this.getConfig().set("offline_inventory", (Object)null);
                this.saveConfig();
            }
            else {
                this.removeAllMaceRecipes();
                this.getLogger().info("[OneMace] Mace already crafted. Recipes removed.");
            }
            return;
        }, 40L);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this);
        Objects.requireNonNull(this.getCommand("onemace")).setExecutor((CommandExecutor)new OneMaceCommand(this));
    }

    public AtomicBoolean getPapiEnabled() {
        return papiEnabled;
    }

    public boolean isChatEnabled(Player player, StaffChatType type) {
        if (!toggleTable.contains(player.getUniqueId(), type.getType())) {
            return true;
        }
        return Boolean.TRUE.equals(GirlsDupeV2.getInstance().toggleTable.get(player.getUniqueId(), type.getType()));
    }

    public ArrayList<StaffChatType> getChannels() {
        return channels;
    }

    public GirlsDupeV2() {
    }

    @Override
    public void onDisable() {
        super.onDisable();
        saveConfig();
        this.getLogger().info("[OneMace] Saving Ender Chest data before shutdown...");
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean hasMace = false;
            for (final ItemStack item : player.getInventory().getContents()) {
                if (this.isMace(item)) {
                    hasMace = true;
                    break;
                }
            }
            for (ItemStack item : player.getEnderChest().getContents()) {
                if (this.isMace(item)) {
                    hasMace = true;
                    this.getLogger().info("[OneMace] Player " + player.getName() + " had the Mace in their Ender Chest before shutdown.");
                    break;
                }
            }
            this.getConfig().set("offline_inventory." + player.getUniqueId(), hasMace);

        }
        this.saveConfig();
        this.getLogger().info("[OneMace] Plugin disabled! Ender Chest data saved.");
    }

    public void reloadConfiguration() {
        this.reloadConfig();
        config = this.getConfig();
        papiEnabled.set(config.getBoolean("enable-placeholders"));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage().substring(9).trim();
        if (message.startsWith("!ask")) {
            String question = message.substring(4).trim();
            getLogger().info("Asking question: " + question);
            if (privateMessages) {
                event.setCancelled(true);
            }
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                String answer = queryGemini(question); // Call the integrated function
                getServer().getScheduler().runTask(this, () -> {
                    String coloredMessage = String.format("\n§c[%s]§r %s", botName, answer);
                    if (privateMessages) {
                        event.getPlayer().sendMessage(answer);
                    } else {
                        getServer().broadcastMessage(coloredMessage);
                    }
                });
            });
        }
    }

    @EventHandler
    public void dragonDefeat(EntityDeathEvent e) {
        LivingEntity ent = e.getEntity();
        Random ran = new Random();
        int num = ran.nextInt(100);

        int elytra_pe = this.getConfig().getInt("elytra_drop_percentage", 100);
        int elytra_am = this.getConfig().getInt("elytra_drop_amount", 1);

        if (elytra_pe > 100 || elytra_pe < 1) {
            elytra_pe = 100;
        }
        if (elytra_am > 3 || elytra_am < 1) {
            elytra_am = 1;
        }
        if (num <= elytra_pe) {
            if (ent.getType() == EntityType.ENDER_DRAGON) {
                ItemStack elytra = new ItemStack(Material.ELYTRA, elytra_am);
                ItemMeta itemStackMeta = elytra.getItemMeta();
                itemStackMeta.setDisplayName(ChatColor.GOLD + "Dragon's Wings");
                elytra.setItemMeta(itemStackMeta);
                e.getDrops().add(elytra);
            }
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public ViolationManager getViolationManager() {
        return violationManager;
    }

    public Set<UUID> getVerbosePlayers() {
        return verbosePlayers;
    }

    public boolean isVerboseConsoleEnabled() {
        return verboseConsole;
    }

    public static GirlsDupeV2 getInstance() {
        return getPlugin(GirlsDupeV2.class);
    }

    private void loadSpawnLocation() {
        if (config.contains("spawn")) {
            spawnLocation = (Location) config.get("spawn");
        } else {
            spawnLocation = new Location(Bukkit.getWorlds().getFirst(), 0, 0, 0);
            config.set("spawn", spawnLocation);
            saveConfig();
        }
    }

    private void saveSpawnLocation() {
        if (spawnLocation != null) {
            config.set("spawn", spawnLocation);
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("rules")) {
            FileConfiguration config = getConfig();

            if (config != null) {
                List<String> rules = config.getStringList("rules");
                if (rules.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No rules have been set.");
                } else {
                    sender.sendMessage(ChatColor.GOLD + "--- Rules ---");
                    for (String rule : rules) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', rule));
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error: Configuration not found.");
            }
            return false;
        }

        if (command.getName().equalsIgnoreCase("invsee")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.isOp() && !player.hasPermission("girlsdupe.invsee")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Usage: /invsee <player>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player not found or offline.");
                return true;
            }

            Inventory targetInventory = target.getInventory();
            player.openInventory(targetInventory);
            return true;
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (!player.isOp() && !player.hasPermission("girlsdupe.setspawn")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to set the spawn.");
                return true;
            }

            spawnLocation = player.getLocation();
            saveSpawnLocation();
            player.sendMessage(ChatColor.GREEN + "Spawn location set!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (spawnLocation != null) {
                player.teleport(spawnLocation);
                player.sendMessage(ChatColor.GREEN + "Teleported to spawn!");
            } else {
                player.sendMessage(ChatColor.RED + "Spawn location has not been set.");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        boolean hasMace = false;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (this.isMace(item)) {
                hasMace = true;
                break;
            }
        }
        for (ItemStack item : player.getEnderChest().getContents()) {
            if (this.isMace(item)) {
                hasMace = true;
                this.getLogger().info("[OneMace] Player " + player.getName() + " logged out with the Mace in their Ender Chest.");
                break;
            }
        }
        this.getConfig().set("offline_inventory." + player.getUniqueId().toString(), (Object)hasMace);
        this.saveConfig();
    }

    private void initialise() {
        reloadConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));

        this.components.clear();

        String textClick = config.getString("message.textClick", null);
        String textHover = config.getString("message.textHover", null);
        for (String textLine : config.getStringList("message.text")) {
            TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', textLine));

            if (textClick != null) {
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, textClick));
            }
            if (textHover != null) {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', textHover))));
            }
            this.components.add(component);
        }

        this.soundEnabled = config.getBoolean("sound.enabled", false);
        this.sound = config.getString("sound.value", null);
        boolean presentOnJoin = config.getBoolean("message.presentOnJoin", false);
    }

    private void play(CommandSender commandSender) {
        for (TextComponent component : components) {
            commandSender.spigot().sendMessage(component);
        }
        
        // Play the sound if the command sender is a player
        if (soundEnabled && commandSender instanceof Player) {
            try {
                Sound sound = Sound.valueOf(this.sound);
                Player player = (Player) commandSender;
                player.playSound(player.getLocation(), sound, 0.5f, 1f);
            } catch (Exception ignored) {
            }
        }
    }

    public void toggleVerboseMode(CommandSender sender) {
        String enableMessage = getLanguageManager().getPrefixedMessage("verbose-enable");
        String disableMessage = getLanguageManager().getPrefixedMessage("verbose-disable");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerId = player.getUniqueId();

            if (verbosePlayers.contains(playerId)) {
                verbosePlayers.remove(playerId);
                player.sendMessage(disableMessage);
            } else {
                verbosePlayers.add(playerId);
                player.sendMessage(enableMessage);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            verboseConsole = !verboseConsole;

            if (verboseConsole) {
                sender.sendMessage(enableMessage);
            } else {
                sender.sendMessage(disableMessage);
            }
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent playerDeathEvent) {
        //Gets players coordinates
        Player player = playerDeathEvent.getEntity().getPlayer();
        int x = (int) player.getLocation().getX();
        int y = (int) player.getLocation().getY();
        int z = (int) player.getLocation().getZ();

        String environment = player.getWorld().getEnvironment().name();
        String dimension;

        if (environment.equals("NORMAL")) {
            dimension = "the Overworld";
        }
        else if (environment.equals("NETHER")) {
            dimension = "the Nether";
        }
        else if (environment.equals("THE_END")) {
            dimension = "the End";
        }
        else {
            dimension = "an Unknown Dimension";
        }

        //Sends message to player
        //Delay to send message after death message
        Bukkit.getScheduler().runTaskLater(this, () -> player.sendMessage("You died in " + dimension + " at (" + x + ", " + y + ", " + z + ")"), 1);
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public void removeAllMaceRecipes() {
        final NamespacedKey vanillaMaceKey = NamespacedKey.minecraft("mace");
        if (Bukkit.getRecipe(vanillaMaceKey) != null) {
            Bukkit.removeRecipe(vanillaMaceKey);
            this.getLogger().info("[OneMace] Removed vanilla Mace recipe.");
        }
    }

    @EventHandler
    public void onMaceDrop(final PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Item droppedItem = event.getItemDrop();
        if (this.isMace(droppedItem.getItemStack())) {
            this.saveMaceOwner(null);
            this.getLogger().info("[OneMace] Mace ownership cleared due to drop.");
            this.getConfig().set("offline_inventory." + player.getUniqueId().toString(), (Object)false);
            this.saveConfig();
        }
    }

    @EventHandler
    public void onMacePickup(final EntityPickupItemEvent event) {
        final ItemStack pickedItem = event.getItem().getItemStack();
        if (this.isMace(pickedItem)) {
            if (event.getEntity() instanceof Player) {
                this.saveMaceOwner(event.getEntity().getUniqueId());
            }
            else {
                this.saveMaceOwner(null);
            }
        }
    }

    @EventHandler
    public void onMaceMove(final InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof final Player player) {
            final ItemStack cursorItem = event.getCursor();
            final ItemStack clickedItem = event.getCurrentItem();
            if (this.isMace(cursorItem)) {
                this.saveMaceOwner(player.getUniqueId());
                this.getConfig().set("offline_inventory." + player.getUniqueId().toString(), (Object)false);
                this.saveConfig();
            }
            else if (this.isMace(clickedItem) && (this.isStorageContainer(event.getInventory().getType()) || this.isAnimalStorage(event))) {
                this.saveMaceOwner(null);
            }
        }
    }

    private boolean isStorageContainer(final InventoryType type) {
        return type == InventoryType.CHEST || type == InventoryType.BARREL || type == InventoryType.DROPPER || type == InventoryType.DISPENSER || type == InventoryType.SHULKER_BOX || type == InventoryType.HOPPER;
    }

    private boolean isAnimalStorage(final InventoryClickEvent event) {
        return event.getInventory().getHolder() instanceof AbstractHorse;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();
        this.getConfig().set("offline_inventory." + playerUUID.toString(), (Object)null);
        this.saveConfig();
        if (!this.doesMaceExist()) {
            this.getLogger().warning("[OneMace] No Mace found! However, it may still be inside an unloaded chunk.");
            this.getLogger().warning("[OneMace] Recipe removed. Please manually check if the Mace exists to prevent duplication.");
            this.resetMaceCrafting();
        }
    }

    public void markMace(final ItemStack mace) {
        final ItemMeta meta = mace.getItemMeta();
        if (meta != null) {
            final PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(this.maceKey, PersistentDataType.STRING, "true");
            mace.setItemMeta(meta);
        }
    }

    public boolean isMace(final ItemStack item) {
        if (item == null || item.getType() != Material.MACE || !item.hasItemMeta()) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            final PersistentDataContainer data = meta.getPersistentDataContainer();
            return data.has(this.maceKey, PersistentDataType.STRING);
        }
        return false;
    }

    @EventHandler
    public void onPrepareCraft(final PrepareItemCraftEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult().getType() == Material.MACE && this.maceCrafted) {
            event.getInventory().setResult((ItemStack)null);
        }
    }

    @EventHandler
    public void onCraft(final CraftItemEvent event) {
        if (event.getRecipe() != null && event.getRecipe().getResult().getType() == Material.MACE) {
            if (this.maceCrafted) {
                event.setCancelled(true);
                return;
            }
            this.maceCrafted = true;
            this.getConfig().set("settings.mace-crafted", (Object)true);
            this.saveMaceOwner(event.getWhoClicked().getUniqueId());
            this.saveConfig();
            final ItemStack mace = event.getInventory().getResult();
            if (mace != null) {
                this.markMace(mace);
            }
            Bukkit.getScheduler().runTask((Plugin)this, this::removeAllMaceRecipes);
            this.getLogger().info("[OneMace] Mace crafted! Removing all recipes.");
            if (this.getConfig().getBoolean("settings.announce-mace-messages", true)) {
                Bukkit.broadcastMessage("§b[OneMace] The Mace has been crafted!");
            }
        }
    }

    @EventHandler
    public void onCrafterCraft(final CrafterCraftEvent event) {
        if (event.getRecipe().getResult().getType() == Material.MACE) {
            event.setCancelled(true);
        }
    }

    private boolean doesMaceExist() {
        this.getLogger().info("[OneMace] Checking if Mace exists...");
        final boolean maceFound = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (this.isMace(item) || this.isMaceInsideShulker(item)) {
                    this.getLogger().info("[OneMace] Mace found in " + player.getName() + "'s inventory.");
                    return true;
                }
            }
            for (ItemStack item : player.getEnderChest().getContents()) {
                if (this.isMace(item) || this.isMaceInsideShulker(item)) {
                    this.getLogger().info("[OneMace] Mace found in " + player.getName() + "'s Ender Chest.");
                    return true;
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final Item item2) {
                    if (this.isMace(item2.getItemStack()) || this.isMaceInsideShulker(item2.getItemStack())) {
                        this.getLogger().info("[OneMace] Mace found as a dropped item in world: " + world.getName());
                        return true;
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
                        for (ItemStack item3 : inv.getContents()) {
                            if (this.isMace(item3) || this.isMaceInsideShulker(item3)) {
                                this.getLogger().info("[OneMace] Mace found inside a container at " + String.valueOf(state.getLocation()));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof final AbstractHorse horse) {
                    for (final ItemStack item4 : horse.getInventory().getContents()) {
                        if (this.isMace(item4) || this.isMaceInsideShulker(item4)) {
                            this.getLogger().info("[OneMace] Mace found in a horse inventory!");
                            return true;
                        }
                    }
                }
                if (entity instanceof final StorageMinecart minecart) {
                    for (final ItemStack item4 : minecart.getInventory().getContents()) {
                        if (this.isMace(item4) || this.isMaceInsideShulker(item4)) {
                            this.getLogger().info("[OneMace] Mace found in a storage minecart!");
                            return true;
                        }
                    }
                }
                if (entity instanceof final ChestBoat chestBoat) {
                    for (ItemStack item4 : chestBoat.getInventory().getContents()) {
                        if (this.isMace(item4) || this.isMaceInsideShulker(item4)) {
                            this.getLogger().info("[OneMace] Mace found in a Chest Boat at X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " in world " + entity.getWorld().getName());
                            return true;
                        }
                    }
                }
            }
        }
        if (this.getConfig().isConfigurationSection("offline_inventory")) {
            for (String uuid : this.getConfig().getConfigurationSection("offline_inventory").getKeys(true)) {
                if (this.getConfig().getBoolean("offline_inventory." + uuid, true)) {
                    this.getLogger().info("[OneMace] Mace is in an offline player's inventory (UUID: " + uuid + ").");
                    return true;
                }
            }
        }
        this.getLogger().info("[OneMace] Mace does NOT exist! Crafting can be re-enabled.");
        this.getConfig().set("offline_inventory", (Object)null);
        this.saveConfig();
        return false;
    }

    private boolean isMaceInsideShulker(final ItemStack item) {
        if (item == null || item.getType() != Material.SHULKER_BOX) {
            return false;
        }
        final BlockStateMeta meta = (BlockStateMeta)item.getItemMeta();
        if (meta != null) {
            final BlockState blockState = meta.getBlockState();
            if (blockState instanceof final ShulkerBox shulkerBox) {
                final Inventory shulkerInv = shulkerBox.getInventory();
                for (final ItemStack storedItem : shulkerInv.getContents()) {
                    if (this.isMace(storedItem) || this.isMaceInsideShulker(storedItem)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void saveMaceOwner(final UUID ownerUUID) {
        if (ownerUUID == null) {
            this.getConfig().set("settings.mace-owner", (Object)null);
        }
        else {
            this.getConfig().set("settings.mace-owner", (Object)ownerUUID.toString());
        }
        this.saveConfig();
    }

    public UUID getMaceOwner() {
        final String ownerUUID = this.getConfig().getString("settings.mace-owner");
        return (ownerUUID != null) ? UUID.fromString(ownerUUID) : null;
    }

    public boolean isMaceOwner(final UUID playerUUID) {
        final UUID maceOwner = this.getMaceOwner();
        return maceOwner != null && maceOwner.equals(playerUUID);
    }

    public void resetMaceCrafting() {
        this.maceCrafted = false;
        this.getConfig().set("settings.mace-crafted", (Object)false);
        this.getConfig().set("offline_inventory", (Object)null);
        this.saveConfig();
        this.addVanillaMaceRecipe();
        this.getLogger().info("[OneMace] No Mace found. Crafting is re-enabled.");
        if (this.getConfig().getBoolean("settings.announce-mace-messages", true)) {
            Bukkit.broadcastMessage("§b[OneMace] The Mace has been lost! It can now be crafted again.");
        }
    }

    private void addVanillaMaceRecipe() {
        final NamespacedKey vanillaMaceKey = NamespacedKey.minecraft("mace");
        if (Bukkit.getRecipe(vanillaMaceKey) == null) {
            Bukkit.reloadData();
            this.getLogger().info("[OneMace] Vanilla Mace recipe has been restored.");
        }
    }

    @EventHandler
    public void onMaceBreak(final PlayerItemBreakEvent event) {
        final ItemStack brokenItem = event.getBrokenItem();
        if (this.isMace(brokenItem)) {
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                if (!this.doesMaceExist()) {
                    this.resetMaceCrafting();
                }
            }, 50L);
        }
    }

    @EventHandler
    public void onItemDespawn(final ItemDespawnEvent event) {
        if (this.isMace(event.getEntity().getItemStack())) {
            Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                if (!this.doesMaceExist()) {
                    this.resetMaceCrafting();
                }
            }, 50L);
        }
    }

    @EventHandler
    public void onItemRemoved(final EntityRemoveFromWorldEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof final Item item) {
            if (this.isMace(item.getItemStack())) {
                Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
                    if (!this.doesMaceExist()) {
                        this.resetMaceCrafting();
                    }
                }, 50L);
            }
        }
    }

    @EventHandler
    public void onBundleStore(final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final ItemStack cursorItem = event.getCursor();
        final ItemStack clickedItem = event.getCurrentItem();
        if (((clickedInventory != null && clickedInventory.getType() == InventoryType.SHULKER_BOX) || clickedInventory.getType() == InventoryType.HOPPER) && (this.isMace(cursorItem) || this.isMace(clickedItem))) {
            event.setCancelled(true);
        }
        if (event.isShiftClick() && this.isMace(clickedItem)) {
            final Inventory topInventory = event.getView().getTopInventory();
            if (topInventory.getType() == InventoryType.SHULKER_BOX || topInventory.getType() == InventoryType.HOPPER) {
                event.setCancelled(true);
            }
        }
        if ((this.isMace(cursorItem) && this.isBundle(clickedItem)) || (this.isBundle(cursorItem) && this.isMace(clickedItem))) {
            event.setCancelled(true);
        }
        if (event.isShiftClick() && this.isMace(clickedItem) && event.getInventory().getType() == InventoryType.PLAYER) {
            for (final ItemStack item : event.getWhoClicked().getInventory().getContents()) {
                if (this.isBundle(item)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onHopperMove(final InventoryMoveItemEvent event) {
        final ItemStack item = event.getItem();
        if (this.isMace(item)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperPickup(final InventoryPickupItemEvent event) {
        final Item item = event.getItem();
        if (this.isMace(item.getItemStack())) {
            event.setCancelled(true);
        }
    }

    private boolean isBundle(final ItemStack item) {
        return item != null && item.getType() == Material.BUNDLE;
    }
    /// //////////////////////////////////////////////////////////////////

    private void loadConfig() {
        apiKey = getConfig().getString("api-key");
        promptTemplate = getConfig().getString("prompt-template", "You are an AI assistant responding to questions within a Minecraft chat environment. \n" +
                "Please keep your answers concise, informative, and relevant to the context. \n" +
                "Do not include images, links, or any other content that cannot be displayed in chat.\n" +
                "Question: %s");
        apiUrl = getConfig().getString("api-url", "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"); // Default URL
        botName = getConfig().getString("bot-name", "Notch");
        privateMessages =  getConfig().getBoolean("private-questions", false);

        if (apiKey == null || apiKey.isEmpty()) {
            getLogger().severe("API key not found in config.yml. Please configure it.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    public String getPromptBody(String prompt) {
        // Create prompt for generating summary in document language
        JSONObject promptJson = new JSONObject();

        // Array to contain all the content-related data, including the text and role
        JSONArray contentsArray = new JSONArray();
        JSONObject contentsObject = new JSONObject();
        contentsObject.put("role", "user");

        // Array to hold the specific parts (or sections) of the user's input text
        JSONArray partsArray = new JSONArray();
        JSONObject partsObject = new JSONObject();
        partsObject.put("text", prompt);
        partsArray.add(partsObject);
        contentsObject.put("parts", partsArray);

        contentsArray.add(contentsObject);
        promptJson.put("contents", contentsArray);

        // Array to hold various safety setting objects to ensure the content is safe and appropriate
        JSONArray safetySettingsArray = new JSONArray();

        // Creating and setting generation configuration parameters such as temperature and topP
        JSONObject parametersJson = new JSONObject();
        parametersJson.put("temperature", 0.5);
        parametersJson.put("topP", 0.99);
        promptJson.put("generationConfig", parametersJson);

        // Convert the JSON object to a JSON string
        return promptJson.toJSONString();
    }

    public String parseGeminiResponse(String jsonResponse) throws IOException, ParseException, org.json.simple.parser.ParseException {
        // Parse the JSON string
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonResponse);

        // Get the "candidates" array
        JSONArray candidatesArray = (JSONArray) jsonObject.get("candidates");

        // Assuming there's only one candidate (index 0), extract its content
        JSONObject candidateObject = (JSONObject) candidatesArray.get(0);
        JSONObject contentObject = (JSONObject) candidateObject.get("content");

        // Get the "parts" array within the content
        JSONArray partsArray = (JSONArray) contentObject.get("parts");

        // Assuming there's only one part (index 0), extract its text
        JSONObject partObject = (JSONObject) partsArray.get(0);
        String responseText = (String) partObject.get("text");

        return responseText;
    }

    private String queryGemini(String question) {
        try {
            // Build the request body
            String prompt = String.format(promptTemplate, question);
            String requestBody = getPromptBody(prompt);

            // Build the HTTP request (no RestTemplate)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send the request and get the response (using java.net.http.HttpClient)
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            // Handle the response based on the status code
            if (response.statusCode() == 200) {
                String responseText = response.body();
                try {
                    responseText = parseGeminiResponse(responseText);
                } catch (ParseException | org.json.simple.parser.ParseException e) {
                    getLogger().warning("Error parsing Gemini response: " + e.getMessage());
                }
                return responseText;
            } else {
                getLogger().warning("Gemini API Error: " + response.statusCode());
                return "Error communicating with Gemini. (Error code: " + response.statusCode() + ")";
            }
        } catch (IOException | InterruptedException e) {
            getLogger().warning("Error querying Gemini API: " + e.getMessage());
            return "An error occurred while contacting Gemini.";
        }
    }
}