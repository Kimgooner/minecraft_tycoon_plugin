package org.kimgooner.tycoon.global.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.kimgooner.tycoon.db.dao.*;
import org.kimgooner.tycoon.global.item.global.ItemBuilder;
import org.kimgooner.tycoon.global.item.global.ItemUtil;

public class MenuGUI {
    private final MemberDAO memberDAO;
    private final MiningDAO miningDAO;
    private final FarmingDAO farmingDAO;
    private final FishingDAO fishingDAO;
    private final CombatDAO combatDAO;

    public MenuGUI(MemberDAO memberDAO, MiningDAO miningDAO, FarmingDAO farmingDAO, FishingDAO fishingDAO, CombatDAO combatDAO) {
        this.memberDAO = memberDAO;
        this.miningDAO = miningDAO;
        this.farmingDAO = farmingDAO;
        this.fishingDAO = fishingDAO;
        this.combatDAO = combatDAO;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45,
                Component.text("메뉴").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
        );

        //플레이어 정보 - 일반
        String uuid = player.getUniqueId().toString();
        long money = memberDAO.getMoney(player);

        //플레이어 정보 - 채광
        MiningDAO.MiningStats miningStats = miningDAO.getMiningStats(player);

        //플레이어 정보 - 농사
        FarmingDAO.FarmingStats farmingStats = farmingDAO.getFarmingStats(player);

        //플레이어 정보 - 낚시
        FishingDAO.FishingStats fishingStats = fishingDAO.getFishingStats(player);

        //플레이어 정보 - 전투
        CombatDAO.CombatStats combatStats = combatDAO.getCombatStats(player);

        //플레이어 정보 - 기타 ...

        ItemStack playerStat = new ItemBuilder(ItemUtil.createPlayerHead(player))
                .displayName(Component.text(player.getName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(" 정보")).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("소지금: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%,dG", money)).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("누적 접속 시간: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%dH", money)).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(11, playerStat);

        ItemStack miningStat = new ItemBuilder(Material.GOLDEN_PICKAXE)
                .hideAttributeModifiers()
                .displayName(Component.text("채광 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("                       ")))
                .addLore(Component.text("채광 속도: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ⸕", miningStats.getSpeed())).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("채광 행운: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ☘", miningStats.getFortune())).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("순수: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ✧", miningStats.getPristine())).color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("빛: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ✦", miningStats.getLight())).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(12, miningStat);

        ItemStack farmingStat = new ItemBuilder(Material.DIAMOND_HOE)
                .hideAttributeModifiers()
                .displayName(Component.text("농사 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("                       ")))
                .addLore(Component.text("농사 행운: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ☘", farmingStats.getFortune())).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("풍요: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ❈", farmingStats.getRichness())).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(13, farmingStat);

        ItemStack fishingStat = new ItemBuilder(Material.FISHING_ROD)
                .hideAttributeModifiers()
                .displayName(Component.text("낚시 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("                       ")))
                .addLore(Component.text("낚시 속도: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ☂", fishingStats.getSpeed())).color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("다중 루어: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ⚓", fishingStats.getMultihook())).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("경이: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ❂", fishingStats.getWonder())).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(14, fishingStat);

    ItemStack combatStat = new ItemBuilder(Material.NETHERITE_SWORD)
                .hideAttributeModifiers()
                .displayName(Component.text("전투 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text("                       ")))
                .addLore(Component.text("추가 체력: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ❤", combatStats.getHealth())).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("힘: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ❁", combatStats.getStregnth())).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("치명타 확률: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ☣", combatStats.getCritchance())).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("치명타 피해: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ☠", combatStats.getCritdamage())).color(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)))
                .addLore(Component.text("추가 어빌리티 피해: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(String.format("%d ๑", combatStats.getAbility())).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)))
                .build();
        gui.setItem(15, combatStat);

        ItemStack Job = new ItemBuilder(Material.BOOK)
                .hideAttributeModifiers()
                .displayName(Component.text("직업 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("직업 메뉴로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .build();
        gui.setItem(29, Job);

        ItemStack DataChest = new ItemBuilder(Material.ENDER_CHEST)
                .displayName(Component.text("데이터 창고").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("보유한 아이템 데이터를 확인합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .build();
        gui.setItem(30, DataChest);

        ItemStack FastTravel = new ItemBuilder(Material.COMPASS)
                .displayName(Component.text("빠른 이동").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("특정 장소로 바로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .build();
        gui.setItem(32, FastTravel);

        ItemStack Pet = new ItemBuilder(Material.FOX_SPAWN_EGG)
                .displayName(Component.text("펫 정보").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text("펫 메뉴로 이동합니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .addLore(Component.text(" "))
                .addLore(Component.text("클릭하여 이동!").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .build();
        gui.setItem(33, Pet);

        player.openInventory(gui);
    }
}
