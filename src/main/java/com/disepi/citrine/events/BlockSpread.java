package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockSpreadEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockSpread implements Listener {
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canSpread || !data.canEdit))
            event.setCancelled(true);
    }
}
