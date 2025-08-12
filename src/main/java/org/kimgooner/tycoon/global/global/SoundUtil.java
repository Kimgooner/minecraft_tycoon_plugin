package org.kimgooner.tycoon.global.global;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public void playPositiveSound(Player player){
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    public void playNegativeSound(Player player){
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);
    }

    public void playAchievementSound(Player player){
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public void playLevelUpSound(Player player){
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void playGUISound(Player player){
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.0f);
    }
}
