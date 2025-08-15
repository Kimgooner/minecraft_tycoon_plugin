package org.kimgooner.tycoon.job.mining.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.GlobalController;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.global.item.job.mining.PickaxeList;
import org.kimgooner.tycoon.job.mining.controller.MiningController;

import java.util.Set;
import java.util.UUID;

public class MiningCommandHandler implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MiningDAO miningDAO;
    private final PickaxeList pickaxeList;
    private final Set<UUID> editingModeSet;
    private final Set<UUID> statModeSet;
    public MiningCommandHandler(JavaPlugin plugin, GlobalController globalController, MiningController miningController) {
        this.plugin = plugin;
        this.miningDAO = globalController.getGlobalDaoController().getMiningDAO();
        this.pickaxeList = new PickaxeList(plugin);
        this.editingModeSet = miningController.getEditingMode();
        this.statModeSet = miningController.getStatMode();
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

        switch (args[0]) {
            case "pickaxe" -> {
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining pickaxe <index>");
                    return true;
                }
                try {
                    int index = Integer.parseInt(args[1]);
                    ((Player) sender).give(pickaxeList.getTestPickaxe(player));
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cindex는 숫자여야 합니다.");
                    return true;
                }
                return true;
            }
            case "edit" -> {
                if(!editingModeSet.contains(player.getUniqueId())){
                    editingModeSet.add(player.getUniqueId());
                    sender.sendMessage("§a[Tycoon] Edit 모드가 켜졌습니다.");
                }
                else{
                    editingModeSet.remove(player.getUniqueId());
                    sender.sendMessage("§c[Tycoon] Edit 모드가 꺼졌습니다.");
                }
            }
            case "stat" -> {
                if(!editingModeSet.contains(player.getUniqueId())){
                    statModeSet.add(player.getUniqueId());
                    sender.sendMessage("§a[Tycoon] Stat 모드가 켜졌습니다.");
                }
                else{
                    statModeSet.remove(player.getUniqueId());
                    sender.sendMessage("§c[Tycoon] Stat 모드가 꺼졌습니다.");
                }
            }
            default -> {
                sender.sendMessage("§a[Tycoon] 잘못된 명령어입니다.");
                return false;
            }
        }
        return false;
    }
}
