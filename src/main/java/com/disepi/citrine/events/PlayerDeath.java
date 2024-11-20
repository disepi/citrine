package com.disepi.citrine.events;

import cn.nukkit.Player;
import cn.nukkit.command.selector.args.impl.R;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.network.protocol.RespawnPacket;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.data.LevelData;
import com.disepi.citrine.data.PlayerData;

public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        RespawnPacket respawn = new RespawnPacket();
        respawn.x = (float) event.getEntity().x;
        respawn.y = (float) event.getEntity().y;
        respawn.z = (float) event.getEntity().z;
        respawn.respawnState = RespawnPacket.STATE_READY_TO_SPAWN;
        respawn.runtimeEntityId = event.getEntity().getId();
        event.getEntity().dataPacket(respawn);

        Entity ent = event.getEntity();

        if(ent instanceof Player) {
            PlayerData data = Citrine.getData((Player) ent);

            if(data.refData != null) {
                    data.isSpectating = true;
                    data.handleDeath();
                    event.setCancelled(true);
            }
        }
    }
}
