package org.kimgooner.tycoon.global.npc.mining;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

public class MiningNPCHandler implements Listener {
    private final JavaPlugin plugin;
    private final GlobalGUIController globalGUIController;

    public MiningNPCHandler(JavaPlugin plugin, GlobalGUIController globalGUIController) {
        this.plugin = plugin;
        this.globalGUIController = globalGUIController;
    }

    private void makeMessage(Player player, String name, String message) {
        player.sendMessage(Component.text("[").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(name.substring(4)).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
                .append(Component.text("]: ").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                .append(Component.text(message).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
        );
    }

    @EventHandler
    public void onPlayerInteract(NpcInteractEvent event) {
        Npc npc = event.getNpc();
        String name = npc.getData().getDisplayName();
        Player player = event.getPlayer();
        if(npc.getData().getName().equalsIgnoreCase("mine1")){
            makeMessage(player, name, "가루 좀 많이 가지고 있어?");
            Bukkit.getScheduler().runTaskLater(plugin, () -> globalGUIController.openCaveHeart(player), 10L); // 1틱 뒤 베드락
        }
    }

}
