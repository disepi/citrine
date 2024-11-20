package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.MathUtil;

public class SpeedB extends Check {
    // somewhat accurate
    public SpeedB() {
        super("Friction",  "Orangebird","Speed", 6);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.lerpTicks > 0 || p.getAllowFlight() || d.onGround || p.isImmobile()) {
            d.lastSpeed = 999.f;
            return true;
        }

        float curSpeed = MathUtil.distance(e.x, 0, e.z, d.lastX, 0, d.lastZ);
        if(curSpeed >= 0.3f && curSpeed >= d.lastSpeed) {
            d.lastSpeed = curSpeed;
            this.violate(p, d, 1, true, "speed=" + curSpeed);
            return false;
        }

        d.lastSpeed = curSpeed;
        reward(d, 0.1f);
        return true;
    }
}
