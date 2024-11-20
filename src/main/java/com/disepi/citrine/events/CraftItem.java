package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.CraftItemEvent;

public class CraftItem implements Listener {
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        event.setCancelled(true);
    }
}
