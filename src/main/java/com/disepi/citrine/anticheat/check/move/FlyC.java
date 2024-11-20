package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class FlyC extends Check {
    public FlyC() {
        super("Airjump",  "405","Fly",4);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.offGroundTicks <= 10 || !d.touchedGroundSinceTp || d.lerpTicks > 0 || e.y < 0.f || p.getAllowFlight()) return true;

        if(e.y > d.lastY) {
            this.violate(p, d, 1, true, "offGroundTicks=" + d.offGroundTicks);
            return false;
        }

        reward(d, 0.1f);
        return true;
    }
}
