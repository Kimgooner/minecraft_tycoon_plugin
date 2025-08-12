package org.kimgooner.tycoon.db.dao.job.mining;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.db.dao.job.JonDAOUtil;
import org.kimgooner.tycoon.discord.DiscordWebhookLevelUpEvent;
import org.kimgooner.tycoon.discord.LevelUpType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MiningDAO {
    private final DatabaseManager databaseManager;
    private final JavaPlugin plugin;
    private final GlobalDAOController globalDAOController;
    private final JonDAOUtil util =  new JonDAOUtil();

    public MiningDAO(DatabaseManager databaseManager, JavaPlugin plugin, GlobalDAOController globalDAOController) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
        this.globalDAOController = globalDAOController;
    }

    public void sendMiningLevelUpMessage(Player player, Integer level) {
        player.sendMessage(Component.text("§f[시스템]: §3채광 §f레벨이 상승했습니다! §8%d §f-> §3%d".formatted(level-1, level)));
        player.sendMessage(Component.text("§f[시스템]: §f레벨 보너스:"));
        player.sendMessage(Component.text("§f[시스템]:  §f채광 행운: §8%d §f-> §6%d ☘".formatted((level-1) * 4, level * 4)));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static class MiningStats {
        int level;
        double exp;
        int wisdom;
        int fortune;
        int speed;
        int pristine;
        int power;
        int light;
        int spread;

        public MiningStats(int level, double exp, int wisdom, int fortune, int speed, int pristine, int power, int light, int spread) {
            this.level = level;
            this.exp = exp;
            this.wisdom = wisdom;
            this.fortune = fortune;
            this.speed = speed;
            this.pristine = pristine;
            this.power = power;
            this.light = light;
            this.spread = spread;
        }
        public int getLevel() { return level; }
        public double getExp() { return exp; }
        public int getWisdom() { return wisdom; }
        public int getFortune() { return fortune; }
        public int getSpeed() { return speed; }
        public int getPristine() { return pristine; }
        public int getPower() { return power; }
        public int getLight() { return light; }
        public int getSpread() { return spread; }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM minings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void init(Player player) {
        if (hasData(player)) return; // 이미 있으면 무시
        String sql = "INSERT INTO minings (member_uuid, level, exp, wisdom, fortune, speed, pristine, power, light, spread) " +
                "VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Double getExp(Player player) {
        String sql = "SELECT exp FROM minings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Integer getLevel(Player player) {
        String sql = "SELECT level FROM minings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setExp(Player player, Double value) {
        String sql = "UPDATE minings SET exp = ? WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, value);
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(Player player) {
        String sql = "UPDATE minings SET level = level + 1 WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);

            Integer cur = getLevel(player);
            DiscordWebhookLevelUpEvent event = new DiscordWebhookLevelUpEvent(player, LevelUpType.MINING, cur);
            sendMiningLevelUpMessage(player, cur);
            plugin.getServer().getPluginManager().callEvent(event);

            if (cur % 5 == 0 && cur != 0 && cur <= 45) {
                globalDAOController.getHeartInfoDAO().addLevel(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addExp(Player player, Double value) {
        try (Connection conn = databaseManager.getConnection()) {
            Double newValue = getExp(player) + value;
            if (newValue > util.EXP_LISTS.get(getLevel(player))) {
                setExp(player, newValue - util.EXP_LISTS.get(getLevel(player)));
                addLevel(player);
            }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("UPDATE minings SET exp = exp + ? WHERE member_uuid = ?")) {
                ps.setDouble(1, value);
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MiningStats getMiningStats(Player player) {
        String sql = "SELECT level, exp, wisdom, fortune, speed, pristine, power, light, spread FROM minings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MiningStats(
                            rs.getInt("level"),
                            rs.getDouble("exp"),
                            rs.getInt("wisdom"),
                            rs.getInt("fortune"),
                            rs.getInt("speed"),
                            rs.getInt("pristine"),
                            rs.getInt("power"),
                            rs.getInt("light"),
                            rs.getInt("spread")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 기본값 반환 (DB 삽입은 init()에서만 함)
        return new MiningStats(1, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "pristine", "power", "light", "spread");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE minings SET " + tag + " = ? WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, amount);
                ps.setString(2, player.getUniqueId().toString());
                ps.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getStat(Player player, String tag) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "pristine", "power", "light", "spread");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "SELECT " + tag + " FROM minings WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
