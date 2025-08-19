package org.kimgooner.tycoon.job.mining.service;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.kimgooner.tycoon.job.mining.model.MiningStat;

public class MiningAttributeService {
    public void calcMiningSpeed(MiningStat stat, Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        removeMiningSpeedModifier(player);

        attr.setBaseValue(0.125); // 기본 값 설정
        applyMiningSpeedStat(player, stat.getSpeed());
    }

    private void applyMiningSpeedStat(Player player, int speedStat) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        double bonus = speedStat * 0.00625;

        AttributeModifier modifier = new AttributeModifier(
                NamespacedKey.minecraft("custom_break_speed"),
                bonus,
                AttributeModifier.Operation.ADD_NUMBER
        );

        attr.addModifier(modifier);
    }
    private void removeMiningSpeedModifier(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        if (attr == null) return;

        for (AttributeModifier mod : attr.getModifiers()) {
            attr.removeModifier(mod);
        }
        attr.setBaseValue(1.0);
    }
}
