package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockFromToEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockFromTo implements Listener {
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canModify || !data.canEdit))
            event.setCancelled(true);
    }
}
