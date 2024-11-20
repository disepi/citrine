package com.disepi.citrine.anticheat.check.move;

import cn.nukkit.Player;
import com.disepi.citrine.anticheat.check.Check;
import com.disepi.citrine.data.PlayerData;

public class NoSwingA extends Check {
    public NoSwingA() {
        super("Swing",  "405", "Fight",6);
    }

    public boolean checkAttack(PlayerData d, Player p) {
        long delta = System.currentTimeMillis() - d.lastSwingTime;
        // change threshold as needed
        if(delta >= 1000) {
            this.violate(p, d, 1, true, "delta=" + delta);
            return false;
        }

        reward(d, 0.1f);
        return true;
    }
}
