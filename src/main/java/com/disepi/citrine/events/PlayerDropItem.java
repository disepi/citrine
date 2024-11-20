package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDropItemEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class PlayerDropItem implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        LevelData data = Citrine.getLevelData(event.getPlayer().level);
        if(data != null && (!data.canDrop || !data.canEdit))
            event.setCancelled(true);
    }
}
