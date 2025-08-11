package org.kimgooner.tycoon.db.dao.mining;

import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.discord.DiscordWebhookLevelUpEvent;
import org.kimgooner.tycoon.discord.LevelUpType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MiningDAO {
    private final Connection conn;
    private final JavaPlugin plugin;
    private final GlobalDAOController globalDAOController;

    public MiningDAO(Connection conn, JavaPlugin plugin, GlobalDAOController globalDAOController) {
        this.conn = conn;
        this.plugin = plugin;
        this.globalDAOController = globalDAOController;

    }

    private final List<Double> EXP_LISTS = List.of(
            50.0, 125.0, 200.0, 300.0, 500.0, 750.0, 1000.0, 1500.0, 2000.0,
            3500.0, 5000.0, 7500.0, 10000.0, 15000.0, 20000.0, 30000.0, 50000.0, 75000.0, 100000.0,
            200000.0, 300000.0, 400000.0, 500000.0, 600000.0, 700000.0, 800000.0, 900000.0, 1000000.0, 1100000.0, 1200000.0,
            1300000.0, 1400000.0, 1500000.0, 1600000.0, 1700000.0, 1800000.0, 1900000.0, 2000000.0, 2100000.0, 2200000.0,
            2300000.0, 2400000.0, 2500000.0, 2600000.0, 2750000.0, 2900000.0, 3100000.0, 3400000.0, 3700000.0, 4000000.0,
            4300000.0, 4600000.0, 4900000.0, 5200000.0, 5500000.0, 5800000.0, 6100000.0, 6400000.0, 6700000.0, 7000000.0
    );

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
        public int getLevel() {
            return level;
        }
        public double getExp() {
            return exp;
        }
        public int getWisdom() {
            return wisdom;
        }
        public int getFortune() {
            return fortune;
        }
        public int getSpeed() {
            return speed;
        }
        public int getPristine() {
            return pristine;
        }
        public int getPower() {
            return power;
        }
        public int getLight() {
            return light;
        }
        public int getSpread() {
            return spread;
        }
    }

    public boolean hasData(Player player) {
        String sql = "SELECT 1 FROM minings WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Double getExp(Player player) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT exp FROM minings WHERE member_uuid = ?");
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
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT level FROM minings WHERE member_uuid = ?");
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
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps =  conn.prepareStatement("UPDATE minings SET exp = ? WHERE member_uuid = ?");
            ps.setDouble(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(Player player) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement("UPDATE minings SET level = level + 1 WHERE member_uuid = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            Integer cur = getLevel(player);
            DiscordWebhookLevelUpEvent event = new DiscordWebhookLevelUpEvent(player, LevelUpType.MINING, cur);
            sendMiningLevelUpMessage(player, cur);
            plugin.getServer().getPluginManager().callEvent(event);
            if(cur % 5 == 0 && cur != 0 && cur <= 45) {
                globalDAOController.getHeartInfoDAO().addLevel(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addExp(Player player, Double value) {
        try {
            Double newValue = getExp(player) + value;
            if(newValue > EXP_LISTS.get(getLevel(player))){
                setExp(player, newValue - EXP_LISTS.get(getLevel(player)));
                addLevel(player);
            }
            conn.setAutoCommit(false);
            PreparedStatement ps =  conn.prepareStatement("UPDATE minings SET exp = exp + ? WHERE member_uuid = ?");
            ps.setDouble(1, value);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MiningDAO.MiningStats getMiningStats(Player player) {
        String sql = "SELECT level, exp, wisdom, fortune, speed, pristine, power, light, spread FROM minings WHERE member_uuid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MiningDAO.MiningStats(
                            rs.getInt("level"),
                            rs.getInt("exp"),
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
        return new MiningDAO.MiningStats(1, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public void setStat(Player player, String tag, int amount) {
        List<String> allowedColumns = List.of("level", "exp", "wisdom", "fortune", "speed", "pristine", "power", "light", "spread");
        if (!allowedColumns.contains(tag)) {
            throw new IllegalArgumentException("Invalid column name: " + tag);
        }

        String sql = "UPDATE minings SET " + tag + " = ? WHERE member_uuid = ?";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, amount);
            ps.setString(2, player.getUniqueId().toString());
            ps.executeUpdate();
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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
