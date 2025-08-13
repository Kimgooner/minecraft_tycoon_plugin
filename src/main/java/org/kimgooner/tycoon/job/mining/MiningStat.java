package org.kimgooner.tycoon.job.mining;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Builder
public class MiningStat {
    private int power;
    private int speed;                          // 채광 속도
    private int fortune;                        // 채광 행운
    private int spread;                         // 연쇄 파괴
    private double pristine;                       // 순수
    private int light;                          // 빛

    private double wisdom;                         // 경험치 획득량

    private int dust;                           // 가루 획득량
    private int low_base;                       // 하위 가루 기본값
    private int high_base;                      // 상위 가루 기본값

    private int regen_time;

    //확률 관련
    private double find_ore;                    // 광물 발견 확률
    private double find_block;                  // 풍부한 광물 발견 확률
    private double find_chest;                  // 상자 드랍 확률
    private double find_highChest;              // 상위 상자 드랍 확률

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
    private boolean checkChanceMul(double baseChance, double bonusMultiplier) {
        double resultChance = baseChance * (1.0 + bonusMultiplier);
        int weight = (int) Math.floor(resultChance * 100);
        int roll = ThreadLocalRandom.current().nextInt(10000) + 1;
        return roll <= weight;
    }
    private boolean checkChanceAdd(double baseChance, double bonusAdd) {
        double resultChance = baseChance + bonusAdd;
        int weight = (int) Math.floor(resultChance * 100);
        int roll = ThreadLocalRandom.current().nextInt(10000) + 1;
        return roll <= weight;
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
    public boolean isPristine() {return checkChanceAdd(5.0, pristine);}
    public boolean oreFind() {return checkChanceMul(50.0, find_ore);}
    public boolean blockFind() {return checkChanceMul(20.0, find_block);}
    public boolean chestFind(int area) {
        return switch (area) {
            case 1 -> checkChanceMul(0.14, find_chest);
            case 2 -> checkChanceMul(0.18, find_chest);
            case 3 -> checkChanceMul(0.20, find_chest);
            case 4 -> checkChanceMul(0.27, find_chest);
            default -> false;
        };
    }
    public boolean highChestFind() {return checkChanceMul(20.0, find_highChest);}
}
