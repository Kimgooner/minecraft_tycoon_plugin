package org.kimgooner.tycoon.global.npc.mining;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kimgooner.tycoon.global.gui.GlobalGUIController;

public class MiningNPCHandler implements Listener {
    private final GlobalGUIController globalGUIController;

    public MiningNPCHandler(GlobalGUIController globalGUIController) {
        this.globalGUIController = globalGUIController;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        if(event.getNPC().getName().equalsIgnoreCase("동굴의 심장")){
            event.getClicker().sendMessage("정답!");
        }
        else{
            event.getClicker().sendMessage("풉!");
        }
    }
}
