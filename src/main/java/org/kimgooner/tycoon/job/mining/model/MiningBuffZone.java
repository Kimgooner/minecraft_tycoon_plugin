package org.kimgooner.tycoon.job.mining.model;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class MiningBuffZone extends BukkitRunnable {

    private final Location center;
    private final double radius;
    private final int durationTicks;
    private final Set<Player> affectedPlayers = new HashSet<>();
    private int ticksPassed = 0;

    public MiningBuffZone(Location center, double radius, int durationSeconds) {
        this.center = center.clone().add(0.5, 0, 0.5);
        this.radius = radius;
        this.durationTicks = durationSeconds * 20;
    }

    @Override
    public void run() {
        if (ticksPassed >= durationTicks) {
            this.cancel();
            spawnExplosionEffect();
            return;
        }

        spawnLayeredCircleParticles();
        applyBuffToPlayersInRange();

        ticksPassed += 10; // 10틱마다 갱신
    }

    private void spawnLayeredCircleParticles() {
        World world = center.getWorld();
        int layers = 3; // 레이어 수
        double layerSpacing = 0.5; // 레이어 높이 간격

        for (int l = 0; l < layers; l++) {
            double y = center.getY() + 0.5 + l * layerSpacing;
            int particleCount = 24; // 한 레이어당 파티클 수
            for (double angle = 0; angle < 360; angle += 360.0 / particleCount) {
                double rad = Math.toRadians(angle);
                double x = center.getX() + radius * Math.cos(rad);
                double z = center.getZ() + radius * Math.sin(rad);

                world.spawnParticle(Particle.CRIT, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void applyBuffToPlayersInRange() {
        double radiusSquared = radius * radius;
        affectedPlayers.clear();
        for (Player p : center.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(center) <= radiusSquared) {
                affectedPlayers.add(p);
                applyBuff(p);
            }
        }
    }

    private void applyBuff(Player player) {
        // 채광 속도 버프 예시 (FAST_DIGGING 5초, 레벨 1)
        player.sendMessage("안녕!");
        // 필요시 포춘/더블 드랍 등 커스텀 효과 추가 가능
    }

    private void spawnExplosionEffect() {
        World world = center.getWorld();
        world.spawnParticle(Particle.EXPLOSION, center, 10, radius / 2, 1, radius / 2, 0.1);
    }

    // 플러그인에서 시작할 때 호출
    public void start(JavaPlugin plugin) {
        this.runTaskTimer(plugin, 0, 10); // 10틱마다 갱신
    }
}