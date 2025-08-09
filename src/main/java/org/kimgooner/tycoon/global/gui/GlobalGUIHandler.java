package org.kimgooner.tycoon.global.gui;

import org.bukkit.event.Listener;

import java.util.List;

public class GlobalGUIHandler implements Listener {
    private final List<String> GUI_NAMES = List.of(
            "메뉴",
            "데이터 보관함",
            "데이터 보관함 - 채광",
            "데이터 보관함 - 농사",
            "데이터 보관함 - 낚시",
            "데이터 보관함 - 전투"
    );
}
