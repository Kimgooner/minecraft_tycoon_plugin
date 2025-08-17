package org.kimgooner.tycoon.job.mining.model;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PortalBuffZone extends BukkitRunnable {

    private final Location center;
    private final double radiusX; // 좌우 반경
    private final double radiusY; // 위아래 반경
    private final int durationTicks;
    private final Set<Player> affectedPlayers = new HashSet<>();
    private int ticksPassed = 0;
    private double angleOffset = 0;

    public PortalBuffZone(Location center, double radiusX, double radiusY, int durationSeconds) {
        this.center = center.clone();
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.durationTicks = durationSeconds * 20;
    }

    @Override
    public void run() {
        if (ticksPassed >= durationTicks) {
            this.cancel();
            spawnExplosionEffect();
            return;
        }

        spawnRotatingParticles();
        applyBuffToPlayersInRange();

        angleOffset += Math.toRadians(10);
        ticksPassed += 2;
    }

    private void spawnRotatingParticles() {
        World world = center.getWorld();
        int phiSteps = 4;   // 위아래 단계, 많을수록 구체가 촘촘
        int thetaSteps = 10; // 원주 단계
        double radius = Math.max(radiusX, radiusY); // 구체 반경

        for (int i = 0; i <= phiSteps; i++) {
            double phi = Math.PI * i / phiSteps; // 0 ~ π
            for (int j = 0; j < thetaSteps; j++) {
                double theta = 2 * Math.PI * j / thetaSteps; // 0 ~ 2π

                double x = center.getX() + radius * Math.sin(phi) * Math.cos(theta);
                double y = center.getY() + radius * Math.cos(phi);
                double z = center.getZ() + radius * Math.sin(phi) * Math.sin(theta);

                world.spawnParticle(Particle.WITCH, x, y, z, 1, 0, 0, 0, 0);
            }
        }
    }

    private void applyBuffToPlayersInRange() {
        double maxRadius = Math.max(radiusX, radiusY);
        double radiusSquared = maxRadius * maxRadius;
        affectedPlayers.clear();
        for (Player p : center.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(center) <= radiusSquared) {
                affectedPlayers.add(p);
                applyBuff(p);
            }
        }
    }

    private void applyBuff(Player player) {
        player.sendMessage("a");
    }

    private void spawnExplosionEffect() {
        World world = center.getWorld();
        world.spawnParticle(Particle.EXPLOSION, center, 10, radiusX / 2, radiusY / 2, 0.1, 0.1);
    }

    public void start(JavaPlugin plugin) {
        this.runTaskTimer(plugin, 0, 2);
    }

    public double getRadius() {
        return Math.max(radiusX, radiusY);
    }

    public void removePortal(){
        spawnExplosionEffect();
        this.cancel();
    }

    public Location getCenter() {
        return center.clone();
    }
}