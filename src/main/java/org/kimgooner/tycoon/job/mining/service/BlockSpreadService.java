package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.*;

public class BlockSpreadService {
    private JavaPlugin plugin;
    private final MiningDropService miningDropService;
    private final BlockRegenService blockRegenService;

    public BlockSpreadService(JavaPlugin plugin,
                              MiningDropService miningDropService,
                              BlockRegenService blockRegenService) {
        this.plugin = plugin;
        this.miningDropService = miningDropService;
        this.blockRegenService = blockRegenService;
    }

    private List<Block> getNearbyValidBlocks(Block origin) {
        List<Block> list = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    // 자기 자신은 제외
                    if (dx == 0 && dy == 0 && dz == 0) continue;

                    Block target = origin.getRelative(dx, dy, dz);
                    if (isValidSpreadTarget(target)) {
                        list.add(target);
                    }
                }
            }
        }
        return list;
    }
    private boolean isValidSpreadTarget(Block block) {
        if (!miningDropService.oreDropTable.containsKey(block.getType())) return false;

        for (BlockFace face : BlockFace.values()) {
            if (block.getRelative(face).getType() == Material.AIR) {
                return true; // 최소 한 면이 AIR와 접해 있음
            }
        }
        return false;
    }

    int BATCH_SIZE = 3;
    public void applyBlockSpread(Player player, MiningStat miningStat, Block origin, int floor) {
        List<Block> candidates = getNearbyValidBlocks(origin);
        Collections.shuffle(candidates);

        int maxCount = miningStat.calcSpread();
        new BukkitRunnable() {
            int index = 0;
            int count = 0;

            @Override
            public void run() {
                int processedThisTick = 0;
                while (processedThisTick < BATCH_SIZE && count < maxCount && index < candidates.size()) {
                    Block target = candidates.get(index++);
                    if (isValidSpreadTarget(target)) {
                        Material material = target.getType();
                        target.setType(Material.AIR);
                        miningDropService.getDropItem(player, miningStat, material, floor);
                        blockRegenService.getRegenBlock(player, miningStat, target, floor);
                        count++;
                    }
                    processedThisTick++;
                }
                if (count >= maxCount || index >= candidates.size()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}