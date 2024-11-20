package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canBreak || !data.canEdit))
        {
            event.setCancelled(true);
            return;
        }

        if(event.getPlayer() == null) return;
        if(!Citrine.getData(event.getPlayer()).handleBlockBreak(event.getBlock().level, event.getBlock())) {
            event.setCancelled(true);
            return;
        }
    }
}
