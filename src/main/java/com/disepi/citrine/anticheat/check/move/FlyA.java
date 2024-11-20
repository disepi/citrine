package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class FlyA extends Check {
    public FlyA() {
        super("Glide",  "Element1","Fly",4);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.offGroundTicks <= 1 || !d.touchedGroundSinceTp || e.y < 0.f || p.getAllowFlight() || p.isImmobile()) return true;

        float delta = Math.abs(Math.abs(e.y) - Math.abs(d.lastY));
        if(delta < 0.002998352f || delta == d.lastDelta) {
            this.violate(p, d, 1, true, "delta=" + delta);
            d.lastDelta = delta;
            return false;
        }
        d.lastDelta = delta;

        reward(d, 0.1f);
        return true;
    }
}
