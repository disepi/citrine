package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import com.disepi.citrine.Citrine;

public class PlayerQuit implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // dont send a quit message
        event.setQuitMessage("");

        // handle the player leaving
        Citrine.getLevelData(event.getPlayer().getLevel()).handleLeave(event.getPlayer());
        Citrine.removeData(event.getPlayer());
    }
}
