package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class FlyB extends Check {
    public FlyB() {
        super("Highjump",  "Platinumo","Fly",3);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.offGroundTicks <= 1 || !d.touchedGroundSinceTp || e.y < 0.f || p.getAllowFlight()) return true;

        float delta = e.y - d.lastGroundPos.y;
        if(delta > 2.0723f) {
            this.violate(p, d, 1, true, "delta=" + delta);
            return false;
        }

        reward(d, 0.1f);
        return true;
    }
}
