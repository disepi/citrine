package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.LiquidFlowEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class LiquidFlow implements Listener {
    @EventHandler
    public void onLiquidFlow(LiquidFlowEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canFlow || !data.canEdit))
            event.setCancelled(true);
    }
}
