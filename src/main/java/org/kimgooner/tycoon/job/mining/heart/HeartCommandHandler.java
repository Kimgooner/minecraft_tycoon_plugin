package org.kimgooner.tycoon.job.mining.heart;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.job.mining.HeartDAO;
import org.kimgooner.tycoon.db.dao.job.mining.HeartInfoDAO;

public class HeartCommandHandler implements CommandExecutor {
    private final JavaPlugin plugin;
    private final HeartDAO heartDAO;
    private final HeartInfoDAO heartInfoDAO;

    public HeartCommandHandler(JavaPlugin plugin, HeartDAO heartDAO, HeartInfoDAO heartInfoDAO) {
        this.plugin = plugin;
        this.heartDAO = heartDAO;
        this.heartInfoDAO = heartInfoDAO;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if(args.length == 0){
            sender.sendMessage("§c사용법: /heart <subcommand>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "level":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /heart level <수치>");
                    return true;
                }
                int level = Integer.parseInt(args[1]);
                sender.sendMessage("§a[Tycoon] §f동굴의 심장 등급을 " + level + "만큼 올립니다.");
                for(int i = 0; i < level; i++) {
                    heartInfoDAO.addLevel(player);
                }
                return true;
            case "key":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /heart key <수치>");
                    return true;
                }
                int key = Integer.parseInt(args[1]);
                heartInfoDAO.addHeartKey(player, key);
                sender.sendMessage("§a[Tycoon] §f해방의 열쇠를 " + key + "개 획득합니다.");
                return true;
            case "powder":
                if (args.length < 3) {
                    sender.sendMessage("§c사용법: /mining speed <하위> <상위>");
                    return true;
                }
                int low = Integer.parseInt(args[1]);
                int high = Integer.parseInt(args[2]);
                heartInfoDAO.addLowPowder(player, low);
                heartInfoDAO.addHighPowder(player, high);
                sender.sendMessage("§a[Tycoon] §f하위, 상위 광석의 가루를 각각 " + low + ", " + high + " 만큼 획득합니다.");
                return true;
        }
        sender.sendMessage("§a[Tycoon] 잘못된 명령어입니다.");
        return false;
    }
}
