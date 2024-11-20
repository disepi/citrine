package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockPlace implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canPlace || !data.canEdit))
        {
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer() == null) return;
        if(!Citrine.getData(event.getPlayer()).handleBlockPlace(event.getBlock().level, event.getBlock())) {
            event.setCancelled(true);
            return;
        }

    }
}
