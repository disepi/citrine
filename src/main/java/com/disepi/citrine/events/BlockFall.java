package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockFallEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockFall implements Listener {
    @EventHandler
    public void onBlockFall(BlockFallEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canFall || !data.canEdit))
            event.setCancelled(true);
    }
}
