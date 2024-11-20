package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockFadeEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;

public class BlockFade implements Listener {
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        LevelData data = Citrine.getLevelData(event.getBlock().level);
        if(data != null && (!data.canFade || !data.canEdit))
            event.setCancelled(true);
    }
}
