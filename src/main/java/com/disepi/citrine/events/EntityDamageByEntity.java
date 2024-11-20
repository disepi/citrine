package com.disepi.citrine.events;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.entity.hub.BoomboxNPC;
import com.disepi.citrine.utils.Log;

public class EntityDamageByEntity implements Listener {
    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity attacker = event.getDamager();
        Entity target = event.getEntity();

        event.setBreakShield(false);

        event.setAttackCooldown(3);

        if(attacker instanceof Player) {
            PlayerData attackerData = Citrine.getData((Player)attacker);
            if(attackerData.refData != null && attackerData.refData.hasHitSpeedMutator) event.setAttackCooldown(1);

            if(!attackerData.canDoDamage) {
                event.setCancelled(true);
                return;
            }
        }

        if(target instanceof Player) {
            if(!Citrine.getData((Player)target).canTakeDamage) {
                event.setCancelled(true);
                return;
            }
        }

    }

}
