package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.global.item.global.ItemStat;
import org.kimgooner.tycoon.global.item.global.ItemStatCalculator;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningStatService {
    private final JavaPlugin plugin;
    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;
    private final ItemStatCalculator itemStatCalculator;

    private final Map<UUID, MiningStat> miningStatCache = new HashMap<>();
    private final Map<UUID, Long> statCacheTime = new HashMap<>();
    private final long CACHE_DURATION_MS = 2000;

    private final Map<UUID, Integer> buffMap_1;
    private final Map<UUID, Integer> buffMap_2;

    public MiningStatService(JavaPlugin plugin,
                             MiningDAO miningDAO, HeartDAO heartDAO, HeartInfoDAO heartInfoDAO,
                             Map<UUID, Integer> buffMap_1, Map<UUID, Integer> buffMap_2
    ) {
        this.plugin = plugin;
        this.miningDAO = miningDAO;
        this.heartDAO = heartDAO;
        this.heartInfoDAO = heartInfoDAO;
        this.itemStatCalculator = new ItemStatCalculator(plugin);

        this.buffMap_1 = buffMap_1;
        this.buffMap_2 = buffMap_2;
    }

    private final Map<Integer, Integer> REGION_LIGHT = Map.of( // region 테이블
            0, 0,
            1, 0,
            2, 500,
            3, 1000,
            4, 1500
    );

    public MiningStat getCachedStat(Player player, int floor) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (miningStatCache.containsKey(uuid)) {
            long lastUpdate = statCacheTime.getOrDefault(uuid, 0L);
            if (now - lastUpdate < CACHE_DURATION_MS) {
                return miningStatCache.get(uuid);
            }
        }

        // 한 번에 모든 heart 레벨 조회
        Map<Integer, Integer> heartLevels = heartDAO.getAllLevels(player);

        MiningStat newStat = calculateStat(player, heartLevels, floor);
        miningStatCache.put(uuid, newStat);
        statCacheTime.put(uuid, now);
        return newStat;
    }

    private MiningStat calculateStat(Player player, Map<Integer,Integer> heartLevels, int floor) {
        UUID uuid = player.getUniqueId();
        ItemStat fromEquipment = itemStatCalculator.getStatMiningTotal(player);

        int level = miningDAO.getLevel(player);

        int power = fromEquipment.getMining_power();
        int speed = fromEquipment.getMining_speed()
                + heartLevels.getOrDefault(1, 0) * 20
                + heartLevels.getOrDefault(16, 0) * 5
                + heartLevels.getOrDefault(23, 0) * 40
                + buffMap_1.getOrDefault(uuid, 0);

        int light = heartLevels.getOrDefault(11, 0) * 10
                + heartLevels.getOrDefault(29, 0) * 10;

        int fortune = fromEquipment.getMining_fortune()
                + level * 4
                + heartLevels.getOrDefault(4, 0) * 2
                + heartLevels.getOrDefault(16, 0)
                + heartLevels.getOrDefault(25, 0) * 3
                + buffMap_2.getOrDefault(uuid, 0);

        if(floor != 0) {
            if(floor <= 2){
                fortune += heartLevels.getOrDefault(13, 0) * 5;
            }
            else{
                fortune += heartLevels.getOrDefault(27, 0) * 5;
            }
            int bonus_fortune = (light - REGION_LIGHT.get(floor)) / 100;
            if (bonus_fortune > 5) bonus_fortune = 5;
            if (bonus_fortune < 0) bonus_fortune = 0;
            fortune = (int) Math.floor(fortune * (1 + bonus_fortune / 100.0));
        }

        int spread = fromEquipment.getMining_spread()
                + heartLevels.getOrDefault(8, 0) * 2
                + heartLevels.getOrDefault(20, 0) * 3;

        int wisdom = fromEquipment.getMining_wisdom()
                + heartLevels.getOrDefault(14, 0);

        int dust = fromEquipment.getMining_dust()
                + heartLevels.getOrDefault(24, 0);

        Long regen_time = 60L - heartLevels.getOrDefault(10, 0);

        int find_ore = heartLevels.getOrDefault(3, 0) + heartLevels.getOrDefault(21, 0);
        int find_block = heartLevels.getOrDefault(5, 0) + heartLevels.getOrDefault(19, 0);
        int find_chest = heartLevels.getOrDefault(7, 0);
        int find_highChest = heartLevels.getOrDefault(9, 0);

        int low_base = heartLevels.getOrDefault(18, 0);
        int high_base = heartLevels.getOrDefault(22, 0);

        MiningStat miningStat = MiningStat.builder()
                .level(level)
                .power(power)                        // 파괴력
                .speed(speed)                        // 채광 속도
                .fortune(fortune)                      // 채광 행운
                .spread(spread)                       // 연쇄 파괴
                .light(light)                        // 빛
                .dust(dust)                         // 가루 획득량
                .wisdom(wisdom)                       // 채광 숙련
                .regen_time(regen_time)
                .find_ore(find_ore)                                                      // 광물을 발견할 확률
                .find_block(find_block)                                                  // 풍부한 광물을 발견할 확률
                .find_chest(find_chest)                                                  // 상자를 획득할 확률
                .find_highChest(find_highChest)                                          // 상위 상자를 획득할 확률
                .low_base(low_base)                                                      // 하위 가루 기본값
                .high_base(high_base)                                                    // 상위 가루 기본값
                .is_consecutiveFortune(heartDAO.getLevel(player, 2) != 0)         // 연속 채광: 행운
                .is_consecutiveSpeed(heartDAO.getLevel(player, 6) != 0)
                .is_eventSpeed(heartDAO.getLevel(player, 15) != 0)                // 이벤트 보너스 : 속도
                .is_eventFortune(heartDAO.getLevel(player, 17) != 0)              // 이벤트 보너스 : 행운
                .is_eventChest(heartDAO.getLevel(player, 26) != 0)                // 이벤트 보너스 : 상자
                .is_eventDust(heartDAO.getLevel(player, 30) != 0)                 // 이벤트 보너스 : 가루
                .is_oreTransmutation(heartDAO.getLevel(player, 31) != 0)          // 광석 변이
                .is_caveBlessing(heartDAO.getLevel(player, 33) != 0)              // 동굴의 축복
                .is_umbralOre(heartDAO.getLevel(player, 28) != 0)                 // 움브랄나이트 탐사
                .is_riftOre(heartDAO.getLevel(player, 12) != 0)                   // 균열 광물 탐사
                .build();
        return miningStat;
    }

    public void calcExp(Player player, MiningStat miningStat, int exp) {
        double resultExp = miningStat.calcExp(exp);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            miningDAO.addExpOnly(player, resultExp);
            miningDAO.processLevelUpIfNeeded(player);
        });
    }

    public void calcLowDust(Player player, MiningStat miningStat, int base) {
        int resultLowDust = miningStat.calcLowDust(base);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> heartInfoDAO.addLowPowder(player, resultLowDust));
    }

    public void calcHighDust(Player player, MiningStat miningStat, int base) {
        int resultHighDust = miningStat.calcHighDust(base);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> heartInfoDAO.addHighPowder(player, resultHighDust));
    }
}
