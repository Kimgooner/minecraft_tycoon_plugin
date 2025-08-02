package org.kimgooner.tycoon.job.mining;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.db.dao.MiningDAO;

import java.util.Collection;
import java.util.Random;


public class MiningEventHandler implements Listener {
    private final MiningDAO miningDAO;

    public MiningEventHandler(MiningDAO miningDAO) {
        this.miningDAO = miningDAO;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        MiningDAO.MiningStats stats = miningDAO.getMiningStats(player);

        int fortune = stats.getFortune();
        player.sendMessage("행운 스텟: " + fortune);

        int guaranteed = fortune / 100;
        int chance = fortune % 100;

        Random random = new Random();
        int result = (random.nextInt(100) < chance) ? 1 : 0;
        result += guaranteed + 1;

        event.setDropItems(false);

        Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand());
        for(ItemStack drop : drops){
            ItemStack newDrop = drop.clone();
            newDrop.setAmount(drop.getAmount() * result);

            Location dropLoc = block.getLocation().add(0.5, 0.5, 0.5);
            block.getWorld().dropItem(dropLoc, newDrop);
        }
    }
}
