package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.LeavesDecayEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class LeavesDecay implements Listener {
    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canDecay || !data.canEdit))
            event.setCancelled(true);
    }
}
