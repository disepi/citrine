package com.disepi.citrine.events;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.Log;

public class EntityDamage implements Listener {
    @EventHandler
    public void onEntityDamaged(EntityDamageEvent event) {
        Entity ent = event.getEntity();

        if(ent instanceof Player) {
            PlayerData data = Citrine.getData((Player) ent);
            if(!data.canTakeDamage) {
                event.setCancelled(true);
                return;
            }

            if(data.refData != null) {
                if(ent.getHealth() - event.getFinalDamage() <= 0.f) {
                    data.isSpectating = true;
                    data.handleDeath();
                    event.setCancelled(true);
                }
            }
        }
    }
}
