package org.kimgooner.tycoon.db.dao.job.mining;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.DatabaseManager;
import org.kimgooner.tycoon.db.GlobalDAOController;
import org.kimgooner.tycoon.db.dao.job.JobDAOUtil;
import org.kimgooner.tycoon.discord.DiscordWebhookLevelUpEvent;
import org.kimgooner.tycoon.discord.LevelUpType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MiningDAO {
    private final DatabaseManager databaseManager;
    private final JavaPlugin plugin;
    private final GlobalDAOController globalDAOController;
    private final JobDAOUtil util =  new JobDAOUtil();

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
        String sql = "INSERT INTO minings (member_uuid, level, exp) " +
                "VALUES (?, 0, 0)";
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
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getPluginManager().callEvent(new DiscordWebhookLevelUpEvent(player, LevelUpType.MINING, cur));
            });
            sendMiningLevelUpMessage(player, cur);

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

    public void addExpOnly(Player player, Double value) {
        try (Connection conn = databaseManager.getConnection()) {
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

    public void processLevelUpIfNeeded(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try (Connection conn = databaseManager.getConnection()) {
                while (true) {
                    int level = getLevel(player);
                    double exp = getExp(player);

                    double requiredExp = util.EXP_LISTS.get(level);
                    if (exp < requiredExp) break;

                    // 경험치 차감
                    setExp(player, exp - requiredExp);

                    // 레벨업
                    addLevelSync(player);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addLevelSync(Player player) {
        String sql = "UPDATE minings SET level = level + 1 WHERE member_uuid = ?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getUniqueId().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Integer cur = getLevel(player);
        Bukkit.getPluginManager().callEvent(new DiscordWebhookLevelUpEvent(player, LevelUpType.MINING, cur));
        sendMiningLevelUpMessage(player, cur);

        if (cur % 5 == 0 && cur != 0 && cur <= 45) {
            globalDAOController.getHeartInfoDAO().addLevel(player);
        }
    }
}
