package org.kimgooner.tycoon.job.mining;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.db.dao.MiningDAO;
import org.kimgooner.tycoon.global.item.job.mining.PickaxeList;

public class MiningCommandHandler implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MiningDAO miningDAO;

    public MiningCommandHandler(JavaPlugin plugin, MiningDAO miningDAO) {
        this.plugin = plugin;
        this.miningDAO = miningDAO;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if(args.length == 0){
            sender.sendMessage("§c사용법: /mining <subcommand>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {

            case "fortune":
                int fortune = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "fortune", fortune);
                sender.sendMessage("§a[Tycoon] §f채광 행운을 " + fortune + "으로 설정합니다.");
                return true;
            case "speed":
                int speed = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "speed", speed);
                sender.sendMessage("§a[Tycoon] §f채광 속도를 " + speed + "으로 설정합니다.");
                return true;
            case "pristine":
                int pristine = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "pristine", pristine);
                sender.sendMessage("§a[Tycoon] §f순수를 " + pristine + "으로 설정합니다.");
                return true;
            case "power":
                int power = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "power", power);
                sender.sendMessage("§a[Tycoon] §f파괴력을 " + power + "으로 설정합니다.");
                return true;
            case "light":
                int light = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "light", light);
                sender.sendMessage("§a[Tycoon] §f빛을 " + light + "으로 설정합니다.");
                return true;
            case "pickaxe":
                int id = Integer.parseInt(args[1]);
                PickaxeList pickaxeList = new PickaxeList(plugin);
                switch (id){
                    case 0 -> player.getInventory().addItem(pickaxeList.getTestPickaxe(player));
                }
                sender.sendMessage("곡괭이 아이템을 획득했습니다. id: " + id);
                return true;
        }
        sender.sendMessage("§a[Tycoon] 잘못된 명령어입니다.");
        return false;
    }
}
