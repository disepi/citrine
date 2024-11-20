package com.disepi.citrine.events;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.*;
import com.disepi.citrine.Citrine;
import com.disepi.citrine.anticheat.AnticheatMain;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.Log;

public class PlayerOutgoingPacket implements Listener {

    boolean sendingCust = false;
    int ignoreTickAmt = 5;

    @EventHandler
    public void onPlayerOutgoingPacket(DataPacketSendEvent event) {
        DataPacket packet = event.getPacket();

        PlayerData dat = Citrine.getData(event.getPlayer());

        if(packet instanceof PlaySoundPacket) {
            // disable chest sounds from playing
            PlaySoundPacket playSound = (PlaySoundPacket) packet;
            if(playSound.name == "random.chestopen" || playSound.name == "random.chestclosed" || playSound.name == "random.enderchestopen" || playSound.name == "random.enderchestclosed")
                event.setCancelled(true);
        }

        if(packet instanceof BlockEventPacket) {
            // disable chest opening/closing animations
            BlockEventPacket blockEvent = (BlockEventPacket) packet;
            if(blockEvent.case1 == 1 && blockEvent.case2 == 0 || blockEvent.case1 == 1 && blockEvent.case2 == 2)
                event.setCancelled(true);
        }

        // disabling crafting
        if(packet instanceof CraftingDataPacket) {
            if(sendingCust) return;
            event.setCancelled(true);
            CraftingDataPacket packetCraft = new CraftingDataPacket();
            sendingCust = true;
            event.getPlayer().dataPacket(packetCraft);
            sendingCust = false;
        }

        if(packet instanceof MovePlayerPacket) {
            MovePlayerPacket move = (MovePlayerPacket) packet;
            if(move.eid != event.getPlayer().getId()) return;
            dat.lastX = move.x;
            dat.lastY = move.y;
            dat.lastZ = move.z;
            dat.lastPitch = move.pitch;
            dat.lastYaw = move.yaw;
            dat.targetTeleportPos = new Vector3f(move.x, move.y, move.z);
            dat.touchedGroundSinceTp = false;

            for(Check c : AnticheatMain.checks)
                c.resetVl(dat);
        }

        if(packet instanceof ChangeDimensionPacket) {
            ChangeDimensionPacket change = (ChangeDimensionPacket) packet;
            dat.lastX = change.x;
            dat.lastY = change.y;
            dat.lastZ = change.z;
            dat.targetTeleportPos = new Vector3f(change.x, change.y, change.z);
            dat.touchedGroundSinceTp = false;

            for(Check c : AnticheatMain.checks)
                c.resetVl(dat);
        }

        // knockback
        if(packet instanceof SetEntityMotionPacket) {
            if(dat == null) return;
            SetEntityMotionPacket setMot = (SetEntityMotionPacket)packet;
            if(setMot.eid != event.getPlayer().getId()) return;
            dat.lerpTicks = 20;
        }
    }
}
