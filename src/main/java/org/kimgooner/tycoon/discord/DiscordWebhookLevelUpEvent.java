package org.kimgooner.tycoon.discord;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiscordWebhookLevelUpEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final LevelUpType levelUpType;  // 추가된 필드
    private final Integer level;

    public DiscordWebhookLevelUpEvent(Player player, LevelUpType levelUpType, Integer level) {
        this.player = player;
        this.levelUpType = levelUpType;
        this.level = level;
    }

    public Player getPlayer() {
        return player;
    }

    public LevelUpType getLevelUpType() {
        return levelUpType;
    }

    public Integer getLevel() {
        return level;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
