package org.kimgooner.tycoon.global.item.global;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
public class ItemGlowUtil {

    // 글로우 적용
    public static void applyGlowColor(Item itemEntity, int grade) {
        ChatColor glowColor = getBukkitColorByGrade(grade); // Team에는 여전히 ChatColor 사용됨
        String teamName = "glow-" + glowColor.name().toLowerCase();

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
            team.setColor(glowColor);
        }

        team.addEntry(itemEntity.getUniqueId().toString());
        itemEntity.setGlowing(true);
    }

    // 이름 색상 포함 Prefix
    public static NamedTextColor getDisplayColor(int grade) {
        return switch (grade) {
            case 0 -> NamedTextColor.DARK_GRAY;
            case 1 -> NamedTextColor.GRAY;
            case 2 -> NamedTextColor.GREEN;
            case 3 -> NamedTextColor.BLUE;
            case 4 -> NamedTextColor.DARK_PURPLE;
            case 5 -> NamedTextColor.GOLD;
            case 6 -> NamedTextColor.LIGHT_PURPLE;
            case 7 -> NamedTextColor.AQUA;
            case 8 -> NamedTextColor.DARK_AQUA;
            case 9 -> NamedTextColor.RED;
            case 10 -> NamedTextColor.DARK_RED;
            default -> NamedTextColor.WHITE;
        };
    }

    // Scoreboard 팀 색상용: ChatColor (구버전 호환)
    public static ChatColor getBukkitColorByGrade(int grade) {
        return switch (grade) {
            case 0 -> ChatColor.DARK_GRAY;
            case 1 -> ChatColor.GRAY;
            case 2 -> ChatColor.GREEN;
            case 3 -> ChatColor.BLUE;
            case 4 -> ChatColor.DARK_PURPLE;
            case 5 -> ChatColor.GOLD;
            case 6 -> ChatColor.LIGHT_PURPLE;
            case 7 -> ChatColor.AQUA;
            case 8 -> ChatColor.DARK_AQUA;
            case 9 -> ChatColor.RED;
            case 10 -> ChatColor.DARK_RED;
            default -> ChatColor.WHITE;
        };
    }
}
