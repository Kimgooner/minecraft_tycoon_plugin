package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.DataStorageDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.job.mining.dto.MiningDataRequestDto;
import org.kimgooner.tycoon.job.mining.dto.MiningResultDto;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.List;

public class MiningDataService {
    private final JavaPlugin plugin;
    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;
    private final DataStorageDAO dataStorageDAO;

    public MiningDataService(JavaPlugin plugin,
                      MiningDAO miningDAO, HeartDAO heartDAO, HeartInfoDAO heartInfoDAO, DataStorageDAO dataStorageDAO) {
        this.plugin = plugin;
        this.miningDAO = miningDAO;
        this.heartDAO = heartDAO;
        this.heartInfoDAO = heartInfoDAO;
        this.dataStorageDAO = dataStorageDAO;
    }

    private final List<Integer> DUST_BASE = List.of(
            2, 4, 6, 10
    );

    public MiningDataRequestDto getPlayerData(Player player) {
        return new MiningDataRequestDto(miningDAO.getLevel(player), heartDAO.getAllLevels(player));
    }

    public void processMiningData(Player player, MiningStat miningStat, MiningResultDto dto, int floor) {
        calcExp(player, miningStat, dto.exp());
        if(dto.grade() != 0){
            if(floor <= 2) {
                int grade = dto.grade() - 1;
                if(grade < 0) grade = 0;
                calcLowDust(player, miningStat, DUST_BASE.get(grade));
            }
            else {
                int grade = dto.grade() - 5;
                if(grade < 0) grade = 0;
                calcHighDust(player, miningStat, DUST_BASE.get(grade));
            }
        }
        calcItem(player, miningStat, dto);
    }

    private void calcExp(Player player, MiningStat miningStat, int exp) {
        double resultExp = miningStat.calcExp(exp);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            miningDAO.addExpOnly(player, resultExp);
            miningDAO.processLevelUpIfNeeded(player);
        });
    }

    private void calcLowDust(Player player, MiningStat miningStat, int base) {
        int resultLowDust = miningStat.calcLowDust(base);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> heartInfoDAO.addLowPowder(player, resultLowDust));
    }

    private void calcHighDust(Player player, MiningStat miningStat, int base) {
        int resultHighDust = miningStat.calcHighDust(base);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> heartInfoDAO.addHighPowder(player, resultHighDust));
    }

    private void calcItem(Player player, MiningStat miningStat, MiningResultDto dto) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> dataStorageDAO.addAmount(player, 1, dto.target(), dto.result_amount()));
    }
}
