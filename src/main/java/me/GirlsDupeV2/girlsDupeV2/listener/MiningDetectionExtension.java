package me.GirlsDupeV2.girlsDupeV2.listener;

import me.GirlsDupeV2.girlsDupeV2.GirlsDupeV2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import java.util.*;

public class MiningDetectionExtension implements Listener {

    private final Map<UUID, List<MiningFeature>> miningHistory = new HashMap<>();
    private final Map<UUID, Integer> playerSuspicionMap = new HashMap<>();
    private final GirlsDupeV2 plugin;

    public MiningDetectionExtension(GirlsDupeV2 plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        //plugin.getLogger().info("ExtensionLoader > MiningDetectionExtension loaded!");
    }

    private static class MiningFeature {
        int airBlocks;
        int solidBlocks;

        MiningFeature(int airBlocks, int solidBlocks) {
            this.airBlocks = airBlocks;
            this.solidBlocks = solidBlocks;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        analyzeMiningPattern(player, block);
    }

    private void analyzeMiningPattern(Player player, Block block) {
        World world = block.getWorld();
        if (world == null) return;

        Location location = block.getLocation();
        int airBlocks = 0, solidBlocks = 0;

        // 分析周围方块
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // 忽略中心方块
                    Block nearby = world.getBlockAt(location.clone().add(x, y, z));
                    if (nearby.getType() == Material.AIR && nearby.getType() == Material.CAVE_AIR) {
                        airBlocks++;
                    } else if (nearby.getType().isSolid()) {
                        solidBlocks++;
                    }
                }
            }
        }

        // 保存挖掘特征
        UUID playerId = player.getUniqueId();
        MiningFeature currentFeature = new MiningFeature(airBlocks, solidBlocks);
        miningHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(currentFeature);

        // 保持历史记录的窗口大小为 5
        List<MiningFeature> history = miningHistory.get(playerId);
        if (history.size() > 5) {
            history.remove(0);
        }

        // 计算历史特征平均值
        int totalAir = 0, totalSolid = 0;
        for (MiningFeature feature : history) {
            totalAir += feature.airBlocks;
            totalSolid += feature.solidBlocks;
        }
        int averageAir = totalAir / history.size();
        int averageSolid = totalSolid / history.size();

        // 根据特征趋势调整可疑值
        int suspicionLevel = playerSuspicionMap.getOrDefault(playerId, 0);
        if (averageSolid >= 7 && averageAir <= 1) {
            // 矿道模式（持续挖掘密集区域）
            suspicionLevel += 5;
        } else if (averageAir >= 6) {
            // 洞穴模式（空气比例较高）
            suspicionLevel -= 5;
        } else {
            // 常规挖掘模式（不确定）
            suspicionLevel += 1;
        }

        // 更新可疑值
        playerSuspicionMap.put(playerId, suspicionLevel);

        // 输出调试信息
        plugin.getLogger().info(player.getName() + " -> Air: " + averageAir + ", Solid: " + averageSolid + ", Suspicion: " + suspicionLevel);
    }

    // 提供可疑值获取接口
    public int getSuspicionLevel(UUID playerId) {
        return playerSuspicionMap.getOrDefault(playerId, 0);
    }

    public int getSuspicionLevel(Player player) {
        UUID playerID = Bukkit.getOfflinePlayer(player.getName()).getUniqueId();
        return getSuspicionLevel(playerID);
    }

    public void resetSuspicionLevel(Player player) {
        UUID playerID = Bukkit.getOfflinePlayer(player.getName()).getUniqueId();
        playerSuspicionMap.put(playerID, 0);
    }
}