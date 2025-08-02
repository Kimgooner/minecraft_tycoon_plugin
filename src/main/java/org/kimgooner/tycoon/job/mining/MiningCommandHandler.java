package org.kimgooner.tycoon.job.mining;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kimgooner.tycoon.db.dao.MiningDAO;

public class MiningCommandHandler implements CommandExecutor {
    private final MiningDAO miningDAO;

    public MiningCommandHandler(MiningDAO miningDAO) {
        this.miningDAO = miningDAO;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage("§c사용법: /mining <subcommand>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {

            case "fortune":
                int fortune = Integer.parseInt(args[1]);
                miningDAO.setStat((Player) sender, "fortune", fortune);
                sender.sendMessage("§a[Tycoon] §f채광 행운을 " + fortune + "으로 설정합니다.");
                return true;
            case "speed":
                int speed = Integer.parseInt(args[1]);
                miningDAO.setStat((Player) sender, "speed", speed);
                sender.sendMessage("§a[Tycoon] §f채광 속도를 " + speed + "으로 설정합니다.");
                return true;
            case "pristine":
                int pristine = Integer.parseInt(args[1]);
                miningDAO.setStat((Player) sender, "fortune", pristine);
                sender.sendMessage("§a[Tycoon] §f순수를 " + pristine + "으로 설정합니다.");
                return true;
            case "power":
                int power = Integer.parseInt(args[1]);
                miningDAO.setStat((Player) sender, "fortune", power);
                sender.sendMessage("§a[Tycoon] §f파괴력을 " + power + "으로 설정합니다.");
                return true;
            case "light":
                int light = Integer.parseInt(args[1]);
                miningDAO.setStat((Player) sender, "fortune", light);
                sender.sendMessage("§a[Tycoon] §f빛을 " + light + "으로 설정합니다.");
                return true;
        }
        sender.sendMessage("§a[Tycoon] 잘못된 명령어입니다.");
        return false;
    }
}
