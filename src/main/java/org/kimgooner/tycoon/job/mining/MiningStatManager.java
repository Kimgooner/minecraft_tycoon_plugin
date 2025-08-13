package org.kimgooner.tycoon.job.mining;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MiningStatManager {
    private final JavaPlugin plugin;
    private final MiningDAO miningDAO;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;

    private final Map<UUID, Integer> buff_1;
    private final Map<UUID, Integer> buff_2;

    private final Map<UUID, MiningStat> miningStatCache;
    private final Map<UUID, Long> miningStatCacheTime;
    private final long CACHE_DURATION_MS = 5000;
    public MiningStatManager(GlobalController globalController, JavaPlugin plugin, MiningController miningController) {
        this.miningDAO = globalController.getGlobalDaoController().getMiningDAO();
        this.heartDAO = globalController.getGlobalDaoController().getHeartDAO();
        this.heartInfoDAO = globalController.getGlobalDaoController().getHeartInfoDAO();
        this.plugin = plugin;

        this.buff_1 = miningController.getBuffMap_1();
        this.buff_2 = miningController.getBuffMap_2();

        this.miningStatCache = miningController.getMiningStatCache();
        this.miningStatCacheTime = miningController.getMiningStatCacheTime();
    }

    private final List<String> STAT_KEYS = List.of("speed", "fortune", "spread", "pristine");
    private final List<String> ENCHANT_KEYS = java.util.List.of("enchant_speed", "enchant_fortune", "enchant_spread", "enchant_pristine");
    private record itemStat(int power, int speed, int fortune, int spread, double pristine) {}
    private itemStat getStatFromItem(PersistentDataContainer data) {
        int power = data.getOrDefault(new NamespacedKey(plugin, "power"), PersistentDataType.INTEGER, 0);

        int speed = data.getOrDefault(new NamespacedKey(plugin, "speed"), PersistentDataType.INTEGER, 0)
                + data.getOrDefault(new NamespacedKey(plugin, "enchant_speed"), PersistentDataType.INTEGER, 0);
        int fortune = data.getOrDefault(new NamespacedKey(plugin, "fortune"), PersistentDataType.INTEGER, 0)
                + data.getOrDefault(new NamespacedKey(plugin, "enchant_fortune"), PersistentDataType.INTEGER, 0);
        int spread = data.getOrDefault(new NamespacedKey(plugin, "spread"), PersistentDataType.INTEGER, 0)
                + data.getOrDefault(new NamespacedKey(plugin, "enchant_spread"), PersistentDataType.INTEGER, 0);

        double pristine = data.getOrDefault(new NamespacedKey(plugin, "pristine"), PersistentDataType.DOUBLE, 0.0)
                + data.getOrDefault(new NamespacedKey(plugin, "enchant_pristine"), PersistentDataType.DOUBLE, 0.0);

        return new itemStat(power, speed, fortune, spread, pristine);
    }

    private PersistentDataContainer getSafePDC(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return new ItemStack(Material.STONE).getItemMeta().getPersistentDataContainer(); // 빈 PDC
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null ? meta.getPersistentDataContainer()
                : new ItemStack(Material.STONE).getItemMeta().getPersistentDataContainer();
    }

    private itemStat getStatFromEquipment(Player player) {
        List<ItemStack> items = List.of(
                player.getInventory().getItemInMainHand(),
                player.getInventory().getHelmet(),
                player.getInventory().getChestplate(),
                player.getInventory().getLeggings(),
                player.getInventory().getBoots()
        );

        int totalPower = 0;
        int totalSpeed = 0;
        int totalFortune = 0;
        int totalSpread = 0;
        double totalPristine = 0.0;

        for (ItemStack item : items) {
            itemStat stat = getStatFromItem(getSafePDC(item));
            totalPower += stat.power();
            totalSpeed += stat.speed();
            totalFortune += stat.fortune();
            totalSpread += stat.spread();
            totalPristine += stat.pristine();
        }

        return new itemStat(totalPower, totalSpeed, totalFortune, totalSpread, totalPristine);
    }

    private MiningStat calculateStat(Player player, Map<Integer,Integer> heartLevels, int level) {
        UUID uuid = player.getUniqueId();
        itemStat fromEquipment = getStatFromEquipment(player);
        int power = fromEquipment.power();
        int speed = fromEquipment.speed()
                + heartLevels.getOrDefault(4, 0) * 20
                + heartLevels.getOrDefault(16, 0) * 5
                + heartLevels.getOrDefault(25, 0) * 40
                + buff_1.getOrDefault(uuid, 0);

        int fortune = fromEquipment.fortune()
                + level * 4
                + heartLevels.getOrDefault(4, 0) * 2
                + heartLevels.getOrDefault(16, 0)
                + heartLevels.getOrDefault(25, 0) * 3
                + buff_2.getOrDefault(uuid, 0);

        int spread = fromEquipment.spread()
                + heartLevels.getOrDefault(8, 0) * 2
                + heartLevels.getOrDefault(20, 0) * 3;

        double pristine = fromEquipment.pristine()
                + heartLevels.getOrDefault(13, 0) * 0.1
                + heartLevels.getOrDefault(27, 0) * 0.1;

        int light = heartLevels.getOrDefault(11, 0) * 10
                + heartLevels.getOrDefault(29, 0) * 10;

        int dust = heartLevels.getOrDefault(24, 0);

        int regen_time = 60 - heartLevels.getOrDefault(10, 0) * 2;

        double wisdom = heartLevels.getOrDefault(14, 0) * 0.5;
        double find_ore = heartLevels.getOrDefault(14, 0) * 0.1 + heartLevels.getOrDefault(21, 0) * 0.1;
        double find_block = heartLevels.getOrDefault(14, 0) * 0.1 + heartLevels.getOrDefault(19, 0) * 0.1;
        double find_chest = heartLevels.getOrDefault(14, 0) * 0.1;
        double find_highChest = heartLevels.getOrDefault(14, 0) * 0.1;

        int low_base = heartLevels.getOrDefault(18, 0);
        int high_base = heartLevels.getOrDefault(22, 0);

        MiningStat miningStat = MiningStat.builder()
                .power(power)                        // 파괴력
                .speed(speed)                        // 채광 속도
                .fortune(fortune)                      // 채광 행운
                .spread(spread)                       // 연쇄 파괴
                .pristine(pristine)                     // 순수
                .light(light)                        // 빛
                .dust(dust)                         // 가루 획득량
                .wisdom(wisdom)                       // 채광 숙련
                .regen_time(regen_time)
                .find_ore(find_ore)                     // 광물을 발견할 확률
                .find_block(find_block)                   // 풍부한 광물을 발견할 확률
                .find_chest(find_chest)                   // 상자를 획득할 확률
                .find_highChest(find_highChest)               // 상위 상자를 획득할 확률
                .low_base(low_base)                     // 하위 가루 기본값
                .high_base(high_base)                    // 상위 가루 기본값
                .is_consecutiveFortune(heartDAO.getLevel(player, 2) != 0)        // 연속 채광: 행운
                .is_consecutiveSpeed(heartDAO.getLevel(player, 6) != 0)
                .is_eventSpeed(heartDAO.getLevel(player, 15) != 0)               // 이벤트 보너스 : 속도
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

    public MiningStat getCachedStat(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (miningStatCache.containsKey(uuid)) {
            long lastUpdate = miningStatCacheTime.getOrDefault(uuid, 0L);
            if (now - lastUpdate < CACHE_DURATION_MS) {
                return miningStatCache.get(uuid);
            }
        }

        // 한 번에 모든 heart 레벨 조회
        Map<Integer, Integer> heartLevels = heartDAO.getAllLevels(player);
        int level = miningDAO.getLevel(player);

        MiningStat newStat = calculateStat(player, heartLevels, level);
        miningStatCache.put(uuid, newStat);
        miningStatCacheTime.put(uuid, now);
        return newStat;
    }

    public void calcExp(Player player, MiningStat miningStat, int exp) {
        double resultExp = miningStat.calcExp(exp);
        miningDAO.addExp(player, resultExp);
    }

    public void calcLowDust(Player player, MiningStat miningStat, int base) {
        int resultLowDust = miningStat.calcLowDust(base);
        heartInfoDAO.addLowPowder(player, resultLowDust);
    }

    public void calcHighDust(Player player, MiningStat miningStat, int base) {
        int resultHighDust = miningStat.calcHighDust(base);
        heartInfoDAO.addHighPowder(player, resultHighDust);
    }
}
