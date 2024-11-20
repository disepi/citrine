package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.MovePlayerPacket;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;
import com.disepi.citrine.utils.MathUtil;

public class SpeedA extends Check {
    // accurate old speed check
    public SpeedA() {
        super("MoveSpeed",  "Bluebird", "Speed",5);
    }

    public boolean check(MovePlayerPacket e, PlayerData d, Player p) {
        if(d.lerpTicks > 0 || p.getAllowFlight()) return true;

        float maxSpeed = 0.6475837678752038f;

        float curSpeed = MathUtil.distance(e.x, 0, e.z, d.lastX, 0, d.lastZ);
        if(curSpeed > maxSpeed) {
            this.violate(p, d, 1, true, "speed=" + curSpeed);
            return false;
        }

        reward(d, 0.1f);
        return true;
    }
}
