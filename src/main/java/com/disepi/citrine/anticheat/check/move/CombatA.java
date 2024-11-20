package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class CombatA extends Check {
    public CombatA() {
        super("Autoclicker",  "CIA","Fight",3);
    }

    public boolean checkAttack(PlayerData d, Player p) {
        long cur = System.currentTimeMillis();
        if(cur - d.lastAttackBufferTime >= 1000) {
            d.lastAttackBufferTime = cur;
            if(d.attackBuffer >= 17) {
                this.violate(p, d, 1, true, "aps=" + d.attackBuffer);
                d.attackBuffer = 0;
                return false;
            }
            else
                reward(d, 0.1f);
            d.attackBuffer = 0;
        }

        d.attackBuffer++;
        return true;
    }
}
