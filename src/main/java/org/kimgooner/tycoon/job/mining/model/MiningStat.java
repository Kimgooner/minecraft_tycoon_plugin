package org.kimgooner.tycoon.job.mining.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Builder
public class MiningStat {
    private int level;
    private double exp;

    private int power;
    private int speed;                          // 채광 속도
    private int fortune;                        // 채광 행운
    private int spread;                         // 연쇄 파괴
    private int light;                          // 빛

    private int wisdom;                         // 경험치 획득량

    private int dust;                           // 가루 획득량
    private int low_base;                       // 하위 가루 기본값
    private int high_base;                      // 상위 가루 기본값

    private Long regen_time;

    //확률 관련
    private int find_ore;                    // 광물 발견 확률
    private int find_block;                  // 풍부한 광물 발견 확률
    private int find_chest;                  // 상자 드랍 확률
    private int find_highChest;              // 상위 상자 드랍 확률

    //패시브 스킬
    private boolean is_consecutiveFortune;      // 연속적인 채광: 행운
    private boolean is_consecutiveSpeed;        // 연속적인 채광: 속도
    private boolean is_eventFortune;            // 이벤트 보너스: 행운
    private boolean is_eventSpeed;              // 이벤트 보너스: 속도
    private boolean is_eventChest;              // 이벤트 보너스: 상자
    private boolean is_eventDust;               // 이벤트 보너스: 가루
    private boolean is_umbralOre;               // 움브랄나이트 탐사
    private boolean is_riftOre;                 // 균열 광물 탐사
    private boolean is_oreTransmutation;        // 광석 변이
    private boolean is_caveBlessing;            // 동굴의 축복

    public void printStat(Player player){
        player.sendMessage("§f채광 속도: §6%,d".formatted(speed));
        player.sendMessage("§f채광 행운: §6%,d".formatted(fortune));
        player.sendMessage("§f연쇄 파괴: §e%,d".formatted(spread));
        player.sendMessage("§f빛: §e%,d".formatted(light));
        player.sendMessage("§f채광 숙련: §3%,d".formatted(wisdom));
        player.sendMessage("§f가루 획득량: §3%,d".formatted(dust));
        player.sendMessage("§f추가 하위 가루 기본 값: §a%,d".formatted(low_base));
        player.sendMessage("§f추가 상위 가루 기본 값: §5%,d".formatted(high_base));
        player.sendMessage("§f광물 발견: §7%,d".formatted(find_ore));
        player.sendMessage("§f풍부 발견: §7%,d".formatted(find_block));
        player.sendMessage("§f상자 드랍: §e%,d".formatted(find_chest));
        player.sendMessage("§f상위 상자 드랍: §e%,d".formatted(find_highChest));
    }

    public boolean consecutiveFortune() {return this.is_consecutiveFortune;}
    public boolean consecutiveSpeed() {return this.is_consecutiveSpeed;}
    public boolean eventFortune() {return this.is_eventFortune;}
    public boolean eventSpeed() {return this.is_eventSpeed;}
    public boolean eventChest() {return this.is_eventChest;}
    public boolean eventDust() {return this.is_eventDust;}
    public boolean oreTransmutation() {return this.is_oreTransmutation;}
    public boolean caveBlessing() {return this.is_caveBlessing;}
    public boolean riftOre() {return this.is_riftOre;}
    public boolean umbralOre() {return this.is_umbralOre;}

    //확률 템플릿
    private boolean checkChance(int baseChance, int bonusChance) {
        int chance = baseChance * (100 + bonusChance);
        int roll = ThreadLocalRandom.current().nextInt(10000) + 1;
        return roll <= chance;
    }

    private boolean checkBigChance(int baseChance, int bonusChance) {
        int chance = baseChance * (100 + bonusChance);
        int roll = ThreadLocalRandom.current().nextInt(1000000) + 1;
        return roll <= chance;
    }


    private int getCount(int stat){
        int guaranteed = stat / 100;
        int chance = stat - (guaranteed * 100);
        int roll = ThreadLocalRandom.current().nextInt(100) + 1;
        if(roll <= chance) return guaranteed + 1;
        else return guaranteed;
    }

    public void bonusMul(int bonus) {fortune = (int) (fortune * (1 + (bonus * 5.0) / 100));}
    public int calcFortune() {return getCount(fortune);}
    public int calcSpread() {return getCount(spread);}
    public double calcExp(double exp) {return exp * (1 + (wisdom / 100.0));}
    public int calcLowDust(int base) {return (int) ((base + low_base) + (base + low_base) * (dust / 100.0));}
    public int calcHighDust(int base) {return (int) ((base + high_base) + (base + high_base) * (dust / 100.0));}
    public boolean oreFind() {return checkChance(50, find_ore);}
    public boolean blockFind() {return checkChance(20, find_block);}
    public boolean chestFind(int area) {
        return switch (area) {
            case 1 -> checkBigChance(14, find_chest);
            case 2 -> checkBigChance(18, find_chest);
            case 3 -> checkBigChance(20, find_chest);
            case 4 -> checkBigChance(27, find_chest);
            default -> false;
        };
    }
    public boolean highChestFind() {return checkChance(20, find_highChest);}
}
