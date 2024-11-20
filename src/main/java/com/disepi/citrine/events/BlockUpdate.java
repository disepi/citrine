package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockUpdateEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockUpdate implements Listener {
    @EventHandler
    public void onBlockUpdate(BlockUpdateEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canUpdate || !data.canEdit))
            event.setCancelled(true);
    }
}
