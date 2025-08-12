package org.kimgooner.tycoon.discord;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DiscordWebhookListener implements Listener {

    private final DiscordWebhookSender webhookSender;

    public DiscordWebhookListener(DiscordWebhookSender webhookSender) {
        this.webhookSender = webhookSender;
    }

    @EventHandler
    public void onDicordWebhookLevelUpEvent(DiscordWebhookLevelUpEvent event) {
        switch (event.getLevelUpType()) {
            case MINING ->
                    webhookSender.sendPlayerEmbed(
                            event.getPlayer(),
                            "**채광** 레벨 상승!",
                            "채광 레벨 **" + event.getLevel().toString() + "**(을)를 달성했습니다.",
                            "https://minecraft.wiki/images/Invicon_Diamond_Pickaxe.png",
                            3447003
                    );
            case FARMING ->
                    webhookSender.sendPlayerEmbed(
                            event.getPlayer(),
                            "**농사** 레벨 상승!",
                            "농사 레벨 **" + event.getLevel().toString() + "**(을)를 달성했습니다.",
                            "https://minecraft.wiki/images/Diamond_Hoe_JE3_BE3.png",
                            3447003
                    );
            case FISHING ->
                    webhookSender.sendPlayerEmbed(
                            event.getPlayer(),
                            "**낚시** 레벨 상승!",
                            "낚시 레벨 **" + event.getLevel().toString() + "**(을)를 달성했습니다.",
                            "https://minecraft.wiki/images/Fishing_Rod_JE2_BE2.png",
                            3447003
                    );
            case COMBAT ->
                    webhookSender.sendPlayerEmbed(
                            event.getPlayer(),
                            "**전투** 레벨 상승!",
                            "전투 레벨 **" + event.getLevel().toString() + "**(을)를 달성했습니다.",
                            "https://minecraft.wiki/images/Diamond_Sword_JE3_BE3.png",
                            3447003
                    );
            case HEART ->
                    webhookSender.sendPlayerEmbed(
                            event.getPlayer(),
                            "**동굴의 심장** 등급 상승!",
                            "동굴의 심장 **" + event.getLevel().toString() + "**티어 를 달성했습니다.",
                            "https://minecraft.wiki/images/Invicon_Diamond_Pickaxe.png",
                            10181046
                    );
        }

    }
}
