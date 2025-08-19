package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.global.item.global.ItemStat;
import org.kimgooner.tycoon.global.item.global.ItemStatCalculator;
import org.kimgooner.tycoon.job.mining.dto.MiningDataRequestDto;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MiningStatService {
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

    public MiningStat getCachedStat(Player player, int floor, MiningDataRequestDto dto) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (miningStatCache.containsKey(uuid)) {
            long lastUpdate = statCacheTime.getOrDefault(uuid, 0L);
            if (now - lastUpdate < CACHE_DURATION_MS) {
                return miningStatCache.get(uuid);
            }
        }

        MiningStat newStat = calculateStat(player, floor, dto.level(), dto.heartLevels());
        miningStatCache.put(uuid, newStat);
        statCacheTime.put(uuid, now);
        return newStat;
    }

    private MiningStat calculateStat(Player player, int floor, int level, Map<Integer, Integer> heartLevels) {
        UUID uuid = player.getUniqueId();
        ItemStat fromEquipment = itemStatCalculator.getStatMiningTotal(player);

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

        // 1% -> 5%

        int find_ore = 50000 + ((heartLevels.getOrDefault(3, 0) + heartLevels.getOrDefault(21, 0)) * 500);
        int find_block = 20000 + ((heartLevels.getOrDefault(5, 0) + heartLevels.getOrDefault(19, 0)) * 200);
        int find_chest = 200 + (heartLevels.getOrDefault(7, 0) * 2);
        int find_highChest = 20000 + (heartLevels.getOrDefault(9, 0) * 200);

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
                .consecutiveFortune(heartLevels.getOrDefault(2, 0) != 0)         // 연속 채광: 행운
                .consecutiveSpeed(heartLevels.getOrDefault(6, 0) != 0)
                .eventSpeed(heartLevels.getOrDefault(15, 0) != 0)                // 이벤트 보너스 : 속도
                .eventFortune(heartLevels.getOrDefault(17, 0) != 0)              // 이벤트 보너스 : 행운
                .eventChest(heartLevels.getOrDefault(26, 0) != 0)                // 이벤트 보너스 : 상자
                .eventDust(heartLevels.getOrDefault(30, 0) != 0)                 // 이벤트 보너스 : 가루
                .oreTransmutation(heartLevels.getOrDefault(31, 0) != 0)          // 광석 변이
                .caveBlessing(heartLevels.getOrDefault(33, 0) != 0)              // 동굴의 축복
                .umbralOre(heartLevels.getOrDefault(28, 0) != 0)                 // 움브랄나이트 탐사
                .riftOre(heartLevels.getOrDefault(12, 0) != 0)                   // 균열 광물 탐사
                .build();
        return miningStat;
    }
}
