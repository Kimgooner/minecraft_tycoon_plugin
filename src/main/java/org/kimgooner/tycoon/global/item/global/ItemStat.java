package org.kimgooner.tycoon.global.item.global;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ItemStat {
    //채광
    private int mining_power;
    private int mining_speed;
    private int mining_fortune;
    private int mining_spread;
    private int mining_dust;
    private int mining_wisdom;

    public ItemStat add(ItemStat other) {
        return ItemStat.builder()
                .mining_power(this.mining_power + other.mining_power)
                .mining_speed(this.mining_speed + other.mining_speed)
                .mining_fortune(this.mining_fortune + other.mining_fortune)
                .mining_spread(this.mining_spread + other.mining_spread)
                .mining_dust(this.mining_dust + other.mining_dust)
                .mining_wisdom(this.mining_wisdom + other.mining_wisdom)
                .build();
    }

    public static ItemStat empty() {
        return ItemStat.builder()
                .mining_power(0)
                .mining_speed(0)
                .mining_fortune(0)
                .mining_spread(0)
                .mining_dust(0)
                .mining_wisdom(0)
                .build();
    }
}