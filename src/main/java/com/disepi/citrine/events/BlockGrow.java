package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockGrowEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockGrow implements Listener {
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canGrow || !data.canEdit))
            event.setCancelled(true);
    }
}
