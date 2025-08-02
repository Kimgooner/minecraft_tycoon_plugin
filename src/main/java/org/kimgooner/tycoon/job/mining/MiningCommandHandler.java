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
        }
        sender.sendMessage("§a[Tycoon] 잘못된 명령어입니다.");
        return false;
    }
}
