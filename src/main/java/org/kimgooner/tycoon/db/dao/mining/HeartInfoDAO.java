package org.kimgooner.tycoon.db.dao.mining;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HeartInfoDAO {
    private final Connection conn;
    public HeartInfoDAO(Connection conn) {
        this.conn = conn;
    }

    private final List<Integer> HEART_KEYS = List.of(
            1, 2, 2, 2, 2, 2, 2, 3, 3
    );

    public void sendHeartLevelUpMessage(Player player, Integer level) {
        player.sendMessage(Component.text("-----------------------------").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text(" 동굴의 심장 ").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true)
                .append(Component.text(String.format("티어 %d", level)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false)));
        player.sendMessage(" ");
        player.sendMessage(Component.text(" 보상").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));
        player.sendMessage(Component.text("  +").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(String.format("%d 심장 해방의 열쇠", HEART_KEYS.get(level))).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)));
        player.sendMessage(Component.text("-----------------------------").color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, true));

        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
    }

    public void init(Player player) {
        try {
            conn.setAutoCommit(false); // 트랜잭션 시작
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT IGNORE INTO cave_heart_info (member_uuid, level, heart_key, low_powder, high_powder, used_heart_key, used_low_powder, used_high_powder) VALUES (?, 0, 0, 0, 0, 0, 0, 0)"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit(); // 커밋
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
            try { conn.rollback(); } catch (SQLException ignored) {}
        }
    }

    public boolean hasData(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getLevel(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT level FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getHeartKey(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT heart_key FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getLowPowder(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT low_powder FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getHighPowder(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT high_powder FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getUsedHeartKey(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT used_heart_key FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getUsedLowPowder(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT used_low_powder FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getUsedHighPowder(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT used_high_powder FROM cave_heart_info WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addLevel(Player player) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET level = level + 1 WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            Integer heartLevel = getLevel(player);
            sendHeartLevelUpMessage(player, heartLevel);
            addHeartKey(player, HEART_KEYS.get(heartLevel));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addHeartKey(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET heart_key = heart_key + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLowPowder(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET low_powder = low_powder + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addHighPowder(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET high_powder = high_powder + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean removeHeartKey(Player player, int value) {
        try {
            Integer newValue = getHeartKey(player) - value;
            if (newValue < 0) { return false; }
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET heart_key = newValue WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            addUsedHeartKey(player, value);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeLowPowder(Player player, int value) {
        try {
            Integer newValue = getLowPowder(player) - value;
            if (newValue < 0) { return false; }
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET low_powder = newValue WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            addUsedLowPowder(player, value);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeHighPowder(Player player, int value) {
        try {
            Integer newValue = getHighPowder(player) - value;
            if (newValue < 0) { return false; }
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET high_powder = newValue WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            addUsedHighPowder(player, value);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUsedHeartKey(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET used_heart_key = used_heart_key + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUsedLowPowder(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET used_low_powder = used_low_powder + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUsedHighPowder(Player player, int value) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET used_high_powder = used_high_powder + ? WHERE member_uuid = ?"
            );
            ps.setInt(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetUsed(Player player) {
        try {
            Integer heartKey = getUsedHeartKey(player);
            Integer lowPowder = getUsedLowPowder(player);
            Integer highPowder = getUsedHighPowder(player);
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cave_heart_info SET used_heart_key = 0 AND used_low_power = 0 AND used_high_powder = 0 WHERE member_uuid = ?"
            );
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            addHeartKey(player, heartKey);
            addLowPowder(player, lowPowder);
            addHighPowder(player, highPowder);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
