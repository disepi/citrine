package com.disepi.citrine.events;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import com.disepi.citrine.Citrine;

import java.lang.reflect.Field;

public class PlayerJoin implements Listener {
    public void setField(Player plr, String fieldName, Object field) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = plr.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(plr, field);
    }

    public void setField2(Player plr, String fieldName, Object field) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = plr.getClass().getSuperclass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(plr, field);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws NoSuchFieldException, IllegalAccessException {
        Player player = event.getPlayer();

        //String randName = "player" + (System.currentTimeMillis()%1000);
        //setField(player, "username", randName);
        //setField(player, "displayName", randName);
        //setField(player, "iusername", randName);
        //player.setDataProperty(new StringEntityData(DATA_NAMETAG, randName), false);

        //UUID newId = UUID.randomUUID();
        //setField2(player, "uuid", newId);
        //setField2(player, "rawUUID", Binary.writeUUID(newId));

        event.getJoinMessage().setText("");
        Citrine.addData(player);
        player.setLevel(Citrine.hubLevel);
        Citrine.getLevelData(Citrine.hubLevel).introduce(player, false);
        player.usedChunks.clear();
    }
}
