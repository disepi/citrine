package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;

public class InventoryPickupItem implements Listener {
    public static boolean cancelPickup = true;
    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        event.setCancelled(cancelPickup);
    }
}
