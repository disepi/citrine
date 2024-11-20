package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.MathUtil;

public class SpeedC extends Check {
    // bad recreation  mostly to stop abuse
    public SpeedC() {
        super("Timer",  "Bluebord","Speed",2);
    }

    float max = 14.25f;

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.lerpTicks > 0 || p.getAllowFlight() || p.isImmobile()) {
            d.moveBuffer = 0.f;
            return true;
        }

        long cur = System.currentTimeMillis();
        if(cur - d.lastMoveBufferTime >= 1000) {
            d.lastMoveBufferTime = cur;

            if(d.moveBuffer >= max) {
                this.violate(p, d, 1, true, "moveBuffer=" + d.moveBuffer);
                d.moveBuffer = 0.f;
                return false;
            }
            else reward(d, 0.5f);
            d.moveBuffer = 0.f;
        }

        float curDist = MathUtil.distance(e.x, 0, e.z, d.lastX, 0, d.lastZ);
        d.moveBuffer += curDist;


        return true;
    }
}
