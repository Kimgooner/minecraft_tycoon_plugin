package org.kimgooner.tycoon.job.mining.handler;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.Tycoon;
import org.kimgooner.tycoon.db.dao.job.mining.MiningDAO;
import org.kimgooner.tycoon.discord.DiscordWebhookMessageEvent;
import org.kimgooner.tycoon.global.item.job.mining.PickaxeList;

import java.util.Set;
import java.util.UUID;

public class MiningCommandHandler implements CommandExecutor {
    private final Tycoon plugin;
    private final MiningDAO miningDAO;

    public MiningCommandHandler(JavaPlugin plugin, MiningDAO miningDAO) {
        this.plugin = (Tycoon) plugin;
        this.miningDAO = miningDAO;
    }

    public void chatTest(Player player){
        player.sendMessage(Component.text("§f[시스템]: §3채광 §f레벨이 상승했습니다! §84 §f-> §35"));
        player.sendMessage(Component.text("§f[시스템]: §f레벨 보너스:"));
        player.sendMessage(Component.text("§f[시스템]:  §f채광 행운: §84 §f-> §65 ☘"));
        player.sendMessage(Component.text("§f[시스템]: §5동굴의 심장 §f등급이 상승했습니다! §84 §f-> §55"));
        player.sendMessage(Component.text("§f[시스템]: §f등급 달성 보너스:"));
        player.sendMessage(Component.text("§f[시스템]:  §8+§52 심장 해방의 열쇠"));
        DiscordWebhookMessageEvent event = new DiscordWebhookMessageEvent(player);
        plugin.getServer().getPluginManager().callEvent(event);

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
            case "chat":
                chatTest(player);
                return true;
            case "edit":
                UUID uuid = player.getUniqueId();
                Set<UUID> editModePlayers = plugin.getEditModePlayers(); // getter 필요

                if (editModePlayers.contains(uuid)) {
                    editModePlayers.remove(uuid);
                    player.sendMessage("§c[편집 모드 종료] 이제 블럭이 자동 복구됩니다.");
                } else {
                    editModePlayers.add(uuid);
                    player.sendMessage("§a[편집 모드 활성화] 블럭 자동 복구가 비활성화됩니다.");
                }
                return true;
            case "fortune":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining fortune <수치>");
                    return true;
                }
                int fortune = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "fortune", fortune);
                sender.sendMessage("§a[Tycoon] §f채광 행운을 " + fortune + "으로 설정합니다.");
                return true;
            case "speed":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining speed <수치>");
                    return true;
                }
                int speed = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "speed", speed);
                sender.sendMessage("§a[Tycoon] §f채광 속도를 " + speed + "으로 설정합니다.");
                return true;
            case "pristine":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining pristine <수치>");
                    return true;
                }
                int pristine = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "pristine", pristine);
                sender.sendMessage("§a[Tycoon] §f순수를 " + pristine + "으로 설정합니다.");
                return true;
            case "power":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining power <수치>");
                    return true;
                }
                int power = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "power", power);
                sender.sendMessage("§a[Tycoon] §f파괴력을 " + power + "으로 설정합니다.");
                return true;
            case "wisdom":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining wisdom <수치>");
                    return true;
                }
                int wisdom = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "wisdom", wisdom);
                sender.sendMessage("§a[Tycoon] §f경험치 획득량을 " + wisdom + "으로 설정합니다.");
                return true;
            case "light":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining light <수치>");
                    return true;
                }
                int light = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "light", light);
                sender.sendMessage("§a[Tycoon] §f빛을 " + light + "으로 설정합니다.");
                return true;
            case "spread":
                if (args.length < 2) {
                    sender.sendMessage("§c사용법: /mining spread <수치>");
                    return true;
                }
                int spread = Integer.parseInt(args[1]);
                miningDAO.setStat(player, "spread", spread);
                sender.sendMessage("§a[Tycoon] §f연쇄 파괴를 " + spread + "으로 설정합니다.");
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
